/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.lib.cdc.compiler;

import io.ballerina.compiler.syntax.tree.Node;
import io.ballerina.lib.cdc.compiler.codeaction.CdcCodeTemplate;
import io.ballerina.lib.cdc.compiler.codeaction.CdcCodeTemplateWithTableName;
import io.ballerina.lib.cdc.compiler.codeaction.ChangeReturnTypeToCdcError;
import io.ballerina.lib.cdc.compiler.codeaction.ChangeReturnTypeToError;
import io.ballerina.lib.cdc.compiler.codeaction.ChangeToRemoteMethod;
import io.ballerina.lib.cdc.compiler.completion.CdcServiceBodyContextProvider;
import io.ballerina.projects.plugins.CompilerPlugin;
import io.ballerina.projects.plugins.CompilerPluginContext;
import io.ballerina.projects.plugins.codeaction.CodeAction;
import io.ballerina.projects.plugins.completion.CompletionProvider;

import java.util.List;

/**
 * This is the compiler plugin for Ballerina Cdc package.
 */
public class CdcCompilerPlugin extends CompilerPlugin {
    @Override
    public void init(CompilerPluginContext compilerPluginContext) {
        compilerPluginContext.addCodeAnalyzer(new CdcCodeAnalyzer());
        getCodeActions().forEach(compilerPluginContext::addCodeAction);
        getCompletionProviders().forEach(compilerPluginContext::addCompletionProvider);
    }

    private List<CodeAction> getCodeActions() {
        return List.of(
                new CdcCodeTemplate(),
                new CdcCodeTemplateWithTableName(),
                new ChangeToRemoteMethod(),
                new ChangeReturnTypeToCdcError(),
                new ChangeReturnTypeToError());
    }

    private List<CompletionProvider<? extends Node>> getCompletionProviders() {
        return List.of(new CdcServiceBodyContextProvider());
    }
}
