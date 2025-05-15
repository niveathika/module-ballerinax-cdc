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

import io.ballerina.projects.plugins.codeaction.CodeActionArgument;
import io.ballerina.projects.plugins.codeaction.CodeActionInfo;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.LineRange;
import io.ballerina.tools.text.TextRange;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.EMPTY_SERVICE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.EMPTY_SERVICE_POSTGRESQL;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.FUNCTION_SHOULD_BE_REMOTE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_RETURN_TYPE_ERROR_OR_NIL;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.CHANGE_RETURN_TYPE_TO_CDC_ERROR;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.CHANGE_RETURN_TYPE_TO_ERROR;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.CODE_TEMPLATE_NAME;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.CODE_TEMPLATE_NAME_WITH_TABLE_NAME;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.IS_POSTGRES_LISTENER;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.MAKE_FUNCTION_REMOTE;
import static io.ballerina.lib.cdc.compiler.codeaction.Constants.NODE_LOCATION;

/**
 * A class for testing code snippet generation code actions.
 */
public class CodeSnippetGenerationCodeActionTest extends AbstractCodeActionTest {

    private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources").toAbsolutePath();
    private static final String BALLERINA_SOURCES = "code-action-source";
    private static final String EXPECTED_SOURCES = "code-action-expected";
    private static final String EXPECTED_FILE_NAME = "result.bal";
    private static final String SOURCE_FILE_NAME = "service.bal";

    @Test
    public void testEmptyServiceCodeAction() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_1"),
                LinePosition.from(6, 0), 2,
                getExpectedCodeAction(6, "Add all functions", EMPTY_SERVICE.getCode(), CODE_TEMPLATE_NAME, false),
                getResultPath("service_1")
        );
    }

    @Test
    public void testEmptyServiceCodeActionWithTableName() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_1"),
                LinePosition.from(6, 0), 2,
                getExpectedCodeAction(6, "Add all functions with tableName parameter",
                        EMPTY_SERVICE.getCode(), CODE_TEMPLATE_NAME_WITH_TABLE_NAME, false),
                getResultPath("service_2")
        );
    }

    @Test
    public void testServiceWithVariablesCodeAction() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_2"),
                LinePosition.from(8, 0), 2,
                getExpectedCodeAction(8, "Add all functions", EMPTY_SERVICE.getCode(), CODE_TEMPLATE_NAME, false),
                getResultPath("service_3")
        );
    }

    @Test
    public void testServiceWithVariablesCodeActionWithTableName() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_2"),
                LinePosition.from(8, 0), 2,
                getExpectedCodeAction(8, "Add all functions with tableName parameter", EMPTY_SERVICE.getCode(),
                        CODE_TEMPLATE_NAME_WITH_TABLE_NAME, false),
                getResultPath("service_4")
        );
    }

    @Test
    public void testPostgresEmptyServiceCodeAction() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_3"),
                LinePosition.from(7, 0), 2,
                getExpectedCodeAction(7, "Add all functions", EMPTY_SERVICE_POSTGRESQL.getCode(), CODE_TEMPLATE_NAME, true),
                getResultPath("service_5")
        );
    }

    @Test
    public void testPostgresEmptyServiceCodeActionWithTableName() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_3"),
                LinePosition.from(7, 0), 2,
                getExpectedCodeAction(7, "Add all functions with tableName parameter",
                        EMPTY_SERVICE_POSTGRESQL.getCode(), CODE_TEMPLATE_NAME_WITH_TABLE_NAME, true),
                getResultPath("service_6")
        );
    }

    @Test
    public void testMakeFunctionRemote() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_4"),
                LinePosition.from(12, 9), 1,
                getExpectedCodeActionForRemoteFunction(224, "Make the function remote",
                        FUNCTION_SHOULD_BE_REMOTE.getCode(), MAKE_FUNCTION_REMOTE),
                getResultPath("service_7")
        );
    }


    @Test
    public void testChangeReturnTypeToError() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_4"),
                LinePosition.from(7, 57), 2,
                getExpectedCodeActionForReturnTypeErrors(173, 7, "Change return type to error?",
                        INVALID_RETURN_TYPE_ERROR_OR_NIL.getCode(), CHANGE_RETURN_TYPE_TO_ERROR),
                getResultPath("service_8")
        );
    }

    @Test
    public void testChangeReturnTypeToCdcError() throws IOException {
        performTest(
                getFilePath("snippet_gen_service_4"),
                LinePosition.from(7, 57), 2,
                getExpectedCodeActionForReturnTypeErrors(173, 7, "Change return type to cdc:Error?",
                        INVALID_RETURN_TYPE_ERROR_OR_NIL.getCode(), CHANGE_RETURN_TYPE_TO_CDC_ERROR),
                getResultPath("service_9")
        );
    }

    private Path getFilePath(String directory) {
        return RESOURCE_DIRECTORY.resolve(BALLERINA_SOURCES).resolve(directory).resolve(SOURCE_FILE_NAME);
    }

    private Path getResultPath(String directory) {
        return RESOURCE_DIRECTORY.resolve(EXPECTED_SOURCES).resolve(directory).resolve(EXPECTED_FILE_NAME);
    }

    private CodeActionInfo getExpectedCodeAction(int line, String actionName, String diagnosticCode,
                                                 String templateName, boolean isPostgresqlListener) {
        LineRange lineRange = LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(line, 1));
        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, lineRange);
        CodeActionArgument isPosgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, isPostgresqlListener);
        CodeActionInfo codeAction = CodeActionInfo.from(actionName, List.of(locationArg, isPosgresListener));
        codeAction.setProviderName(diagnosticCode + "/ballerinax/cdc/" + templateName);
        return codeAction;
    }

    private CodeActionInfo getExpectedCodeActionForRemoteFunction(int startOffset, String actionName,
                                                                  String diagnosticCode, String templateName) {
        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, startOffset);
        CodeActionInfo codeAction = CodeActionInfo.from(actionName, List.of(locationArg));
        codeAction.setProviderName(diagnosticCode + "/ballerinax/cdc/" + templateName);
        return codeAction;
    }

    private CodeActionInfo getExpectedCodeActionForReturnTypeErrors(int startOffset, int interval, String actionName,
                                                                    String diagnosticCode, String templateName) {
        TextRange textRange = TextRange.from(startOffset, interval);
        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, textRange);
        CodeActionInfo codeAction = CodeActionInfo.from(actionName, List.of(locationArg));
        codeAction.setProviderName(diagnosticCode + "/ballerinax/cdc/" + templateName);
        return codeAction;
    }
}
