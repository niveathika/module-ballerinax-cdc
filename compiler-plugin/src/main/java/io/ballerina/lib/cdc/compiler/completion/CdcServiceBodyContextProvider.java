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
package io.ballerina.lib.cdc.compiler.completion;

import io.ballerina.compiler.syntax.tree.ServiceDeclarationNode;
import io.ballerina.lib.cdc.compiler.Constants;
import io.ballerina.projects.plugins.completion.CompletionContext;
import io.ballerina.projects.plugins.completion.CompletionException;
import io.ballerina.projects.plugins.completion.CompletionItem;
import io.ballerina.projects.plugins.completion.CompletionProvider;
import io.ballerina.projects.plugins.completion.CompletionUtil;

import java.util.List;
import java.util.stream.Collectors;

import static io.ballerina.lib.cdc.compiler.Constants.ServiceMethodNames.ON_CREATE;
import static io.ballerina.lib.cdc.compiler.Constants.ServiceMethodNames.ON_DELETE;
import static io.ballerina.lib.cdc.compiler.Constants.ServiceMethodNames.ON_ERROR;
import static io.ballerina.lib.cdc.compiler.Constants.ServiceMethodNames.ON_READ;
import static io.ballerina.lib.cdc.compiler.Constants.ServiceMethodNames.ON_TRUNCATE;
import static io.ballerina.lib.cdc.compiler.Constants.ServiceMethodNames.ON_UPDATE;

public class CdcServiceBodyContextProvider implements CompletionProvider<ServiceDeclarationNode> {

    private static final String COMPLETION_ACTION_NAME = "CdcServiceBodyProvider";

    @Override
    public List<CompletionItem> getCompletions(CompletionContext context, ServiceDeclarationNode serviceDeclarationNode)
            throws CompletionException {
        return Constants.VALID_FUNCTIONS.stream()
                .filter(methodName -> !methodName.equals(ON_ERROR))
                .map(methodName -> {
                    String label = "remote function " + methodName + "()";
                    String insertText = String.format("remote function %s(%s",
                            methodName,
                            getFunctionSignature(methodName)
                    );
                    return new CompletionItem(label, insertText, CompletionItem.Priority.HIGH);
                }).collect(Collectors.toList());
    }

    private String getFunctionSignature(String methodName) {
        return switch (methodName) {
            case ON_READ, ON_CREATE -> String.format("%s after) %s{%s}",
                    CompletionUtil.getPlaceHolderText(1, "record {|anydata...;|}"),
                    CompletionUtil.getPlaceHolderText(2),
                    CompletionUtil.LINE_BREAK + CompletionUtil.PADDING + CompletionUtil.getPlaceHolderText(3)
                            + CompletionUtil.LINE_BREAK);
            case ON_DELETE -> String.format("%s before) %s{%s}",
                    CompletionUtil.getPlaceHolderText(1, "record {|anydata...;|}"),
                    CompletionUtil.getPlaceHolderText(2),
                    CompletionUtil.LINE_BREAK + CompletionUtil.PADDING + CompletionUtil.getPlaceHolderText(3)
                            + CompletionUtil.LINE_BREAK);
            case ON_UPDATE -> String.format("%s before, %s after) %s{%s}",
                    CompletionUtil.getPlaceHolderText(1, "record {|anydata...;|}"),
                    CompletionUtil.getPlaceHolderText(2, "record {|anydata...;|}"),
                    CompletionUtil.getPlaceHolderText(3),
                    CompletionUtil.LINE_BREAK + CompletionUtil.PADDING + CompletionUtil.getPlaceHolderText(4)
                            + CompletionUtil.LINE_BREAK);
            case ON_TRUNCATE -> String.format(") %s{%s}",
                    CompletionUtil.getPlaceHolderText(1),
                    CompletionUtil.LINE_BREAK + CompletionUtil.PADDING + CompletionUtil.getPlaceHolderText(2)
                            + CompletionUtil.LINE_BREAK);
            default -> "";
        };
    }

    @Override
    public List<Class<ServiceDeclarationNode>> getSupportedNodes() {
        return List.of(ServiceDeclarationNode.class);
    }

    @Override
    public String name() {
        return COMPLETION_ACTION_NAME;
    }
}
