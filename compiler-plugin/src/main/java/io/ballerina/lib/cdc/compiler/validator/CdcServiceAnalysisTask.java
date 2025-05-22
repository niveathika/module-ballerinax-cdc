/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.lib.cdc.compiler.validator;

import io.ballerina.compiler.api.SemanticModel;
import io.ballerina.compiler.api.symbols.ClassSymbol;
import io.ballerina.compiler.api.symbols.ServiceDeclarationSymbol;
import io.ballerina.compiler.api.symbols.Symbol;
import io.ballerina.compiler.api.symbols.TypeDescKind;
import io.ballerina.compiler.api.symbols.TypeReferenceTypeSymbol;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import io.ballerina.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerina.lib.cdc.compiler.Utils;
import io.ballerina.projects.plugins.AnalysisTask;
import io.ballerina.projects.plugins.SyntaxNodeAnalysisContext;
import io.ballerina.tools.diagnostics.Diagnostic;
import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import java.util.List;
import java.util.Optional;

import static io.ballerina.lib.cdc.compiler.Constants.CDC_LISTENER_NAME;

public class CdcServiceAnalysisTask implements AnalysisTask<SyntaxNodeAnalysisContext> {

    @Override
    public void perform(SyntaxNodeAnalysisContext context) {
        List<Diagnostic> diagnostics = context.semanticModel().diagnostics();
        for (Diagnostic diagnostic : diagnostics) {
            if (diagnostic.diagnosticInfo().severity() == DiagnosticSeverity.ERROR) {
                return;
            }
        }
        if (!isCdcService(context)) {
            return;
        }
        // Created inner class to keep context as class param
        new CdcServiceValidator(context, (ServiceDeclarationNode) context.node()).validate();
    }

    private boolean isCdcService(SyntaxNodeAnalysisContext context) {
        SemanticModel semanticModel = context.semanticModel();
        ServiceDeclarationNode serviceDeclarationNode = (ServiceDeclarationNode) context.node();
        Optional<Symbol> symbolOpt = semanticModel.symbol(serviceDeclarationNode);
        if (symbolOpt.isEmpty() || !(symbolOpt.get() instanceof ServiceDeclarationSymbol serviceSymbol)) {
            return false;
        }

        // Prefer checking the service type descriptor if available
        Optional<TypeSymbol> serviceTypeOpt = serviceSymbol.typeDescriptor();
        if (serviceTypeOpt.isPresent()) {
            TypeSymbol serviceType = serviceTypeOpt.get();
            if (serviceType.getModule().isPresent() && Utils.isCdcModule(serviceType.getModule().get())) {
                return true;
            }
        }

        // Fallback: check listener types
        List<TypeSymbol> listeners = serviceSymbol.listenerTypes();
        if (listeners.size() != 1) {
            return false;
        }
        TypeSymbol listener = listeners.getFirst();
        if (listener.typeKind() != TypeDescKind.TYPE_REFERENCE) {
            return false;
        }

        TypeSymbol refType = ((TypeReferenceTypeSymbol) listener).typeDescriptor();
        if (refType.typeKind() != TypeDescKind.OBJECT || !(refType instanceof ClassSymbol)) {
            return false;
        }

        List<TypeSymbol> inclusions = ((ClassSymbol) refType).typeInclusions();
        if (inclusions.size() != 1) {
            return false;
        }
        TypeSymbol inclusion = inclusions.getFirst();
        if (inclusion.typeKind() != TypeDescKind.TYPE_REFERENCE || inclusion.getModule().isEmpty()) {
            return false;
        }

        return Utils.isCdcModule(inclusion.getModule().get()) && inclusion.nameEquals(CDC_LISTENER_NAME);
    }
}
