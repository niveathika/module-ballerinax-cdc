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

import io.ballerina.compiler.api.symbols.MethodSymbol;
import io.ballerina.compiler.api.symbols.ModuleSymbol;
import io.ballerina.compiler.api.symbols.ServiceDeclarationSymbol;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import io.ballerina.compiler.syntax.tree.FunctionDefinitionNode;
import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.compiler.syntax.tree.NodeList;
import io.ballerina.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerina.lib.cdc.compiler.Utils;
import io.ballerina.projects.plugins.SyntaxNodeAnalysisContext;
import io.ballerina.tools.diagnostics.DiagnosticInfo;

import java.util.List;
import java.util.Optional;

import static io.ballerina.compiler.syntax.tree.SyntaxKind.OBJECT_METHOD_DEFINITION;
import static io.ballerina.compiler.syntax.tree.SyntaxKind.RESOURCE_ACCESSOR_DEFINITION;
import static io.ballerina.lib.cdc.compiler.Constants.PACKAGE_ORG;
import static io.ballerina.lib.cdc.compiler.Constants.POSTGRESQL_PACKAGE_PREFIX;
import static io.ballerina.lib.cdc.compiler.Constants.VALID_FUNCTIONS;
import static io.ballerina.lib.cdc.compiler.Constants.VALID_FUNCTIONS_NON_POSTGRES;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.EMPTY_SERVICE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.EMPTY_SERVICE_POSTGRESQL;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_MULTIPLE_LISTENERS;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_RESOURCE_FUNCTION;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.NO_VALID_FUNCTION;
import static io.ballerina.lib.cdc.compiler.Utils.getMethodSymbol;
import static io.ballerina.tools.diagnostics.DiagnosticFactory.createDiagnostic;
import static io.ballerina.tools.diagnostics.DiagnosticSeverity.INTERNAL;

public record CdcServiceValidator(SyntaxNodeAnalysisContext context, ServiceDeclarationNode serviceDeclarationNode) {

    public void validate() {
        NodeList<Node> memberNodes = serviceDeclarationNode.members();

        Optional<ServiceDeclarationSymbol> serviceSymbolOpt = getServiceSymbol(serviceDeclarationNode);
        if (serviceSymbolOpt.isEmpty()) {
            return;
        }

        ServiceDeclarationSymbol serviceSymbol = serviceSymbolOpt.get();

        validateAttachedListeners(serviceSymbol);

        Optional<TypeSymbol> listenerOpt = serviceSymbol.listenerTypes().stream().findFirst();
        if (listenerOpt.isEmpty()) {
            return;
        }

        TypeSymbol listener = listenerOpt.get();

        boolean isPostgresListener = listener.getModule()
                .map(this::isPostgresListener)
                .orElse(false);

        boolean hasValidFunction = serviceDeclarationNode.members().stream()
                .filter(node -> node.kind() == OBJECT_METHOD_DEFINITION)
                .map(FunctionDefinitionNode.class::cast)
                .anyMatch(functionNode -> isValidFunction(functionNode, isPostgresListener));

        if (serviceDeclarationNode.members().isEmpty() || !hasValidFunction) {
            reportEmptyServiceDiagnostics(serviceDeclarationNode, isPostgresListener);
        }

        validateServiceMembers(memberNodes);
    }

    private void validateAttachedListeners(ServiceDeclarationSymbol serviceDeclarationSymbol) {
        List<TypeSymbol> listeners = serviceDeclarationSymbol.listenerTypes();
        if (listeners.size() > 1) {
            context.reportDiagnostic(Utils.createDiagnostic(INVALID_MULTIPLE_LISTENERS,
                    serviceDeclarationNode.location()));
        }
    }

    private Optional<ServiceDeclarationSymbol> getServiceSymbol(ServiceDeclarationNode serviceDeclarationNode) {
        return context.semanticModel().symbol(serviceDeclarationNode)
                .filter(ServiceDeclarationSymbol.class::isInstance)
                .map(ServiceDeclarationSymbol.class::cast);
    }

    private boolean isPostgresListener(ModuleSymbol moduleSymbol) {
        if (moduleSymbol == null || moduleSymbol.id() == null) {
            return false;
        }
        String moduleName = moduleSymbol.id().moduleName();
        String orgName = moduleSymbol.id().orgName();
        return POSTGRESQL_PACKAGE_PREFIX.equals(moduleName) && PACKAGE_ORG.equals(orgName);
    }

    private boolean isValidFunction(FunctionDefinitionNode functionNode, boolean isPostgresListener) {
        Optional<MethodSymbol> methodSymbolOpt = getMethodSymbol(context.semanticModel(), functionNode);
        if (methodSymbolOpt.isEmpty()) {
            return false;
        }

        String functionName = functionNode.functionName().toString();
        return isPostgresListener ?
                VALID_FUNCTIONS.contains(functionName) :
                VALID_FUNCTIONS_NON_POSTGRES.contains(functionName);
    }

    private void reportEmptyServiceDiagnostics(ServiceDeclarationNode node, boolean isPostgresListener) {
        DiagnosticInfo diagnosticInfo = new DiagnosticInfo(
                isPostgresListener ? EMPTY_SERVICE_POSTGRESQL.getCode() : EMPTY_SERVICE.getCode(),
                EMPTY_SERVICE.getMessage(),
                INTERNAL
        );
        context.reportDiagnostic(createDiagnostic(diagnosticInfo, node.location()));

        String validFunctions = isPostgresListener ?
                "''onRead'', ''onCreate'', ''onUpdate'', ''onDelete'' or ''onTruncate''" :
                "''onRead'', ''onCreate'', ''onUpdate'' or ''onDelete''";
        context.reportDiagnostic(Utils.createDiagnostic(NO_VALID_FUNCTION, node.location(), validFunctions));
    }

    private void validateServiceMembers(NodeList<Node> memberNodes) {
        memberNodes.forEach(node -> {
            if (node.kind() == OBJECT_METHOD_DEFINITION) {
                FunctionDefinitionNode functionDefinitionNode = (FunctionDefinitionNode) node;
                new CdcFunctionValidator(context, functionDefinitionNode).validate();
            } else if (node.kind() == RESOURCE_ACCESSOR_DEFINITION) {
                context.reportDiagnostic(Utils.createDiagnostic(INVALID_RESOURCE_FUNCTION, node.location()));
            }
        });
    }
}
