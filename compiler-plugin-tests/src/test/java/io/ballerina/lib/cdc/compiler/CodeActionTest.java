/**
 * Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.lib.cdc.compiler;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.ballerina.projects.CodeActionManager;
import io.ballerina.projects.Document;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.projects.plugins.codeaction.CodeActionArgument;
import io.ballerina.projects.plugins.codeaction.CodeActionContextImpl;
import io.ballerina.projects.plugins.codeaction.CodeActionExecutionContext;
import io.ballerina.projects.plugins.codeaction.CodeActionExecutionContextImpl;
import io.ballerina.projects.plugins.codeaction.CodeActionInfo;
import io.ballerina.projects.plugins.codeaction.DocumentEdit;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.LineRange;
import io.ballerina.tools.text.TextRange;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.EMPTY_SERVICE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.EMPTY_SERVICE_POSTGRESQL;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.FUNCTION_SHOULD_BE_REMOTE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_RETURN_TYPE_ERROR_OR_NIL;
import static io.ballerina.lib.cdc.compiler.TestUtils.getEnvironmentBuilder;
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
public class CodeActionTest {

    private static final Gson GSON = new Gson();
    private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources").toAbsolutePath();
    private static final Path BALLERINA_SOURCES = RESOURCE_DIRECTORY.resolve("code-action-source");
    private static final Path EXPECTED_SOURCES = RESOURCE_DIRECTORY.resolve("code-action-expected");
    private static final String EXPECTED_FILE_NAME = "result.bal";
    private static final String SOURCE_FILE_NAME = "service.bal";

    @Test
    public void testEmptyServiceCodeAction() throws IOException {

        LineRange lineRange =
                LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(6, 1));
        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, lineRange);
        CodeActionArgument isPostgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, false);

        CodeActionInfo expectedCodeAction =
                CodeActionInfo.from("Add all functions", List.of(locationArg, isPostgresListener));
        expectedCodeAction.setProviderName(EMPTY_SERVICE.getCode() + "/ballerinax/cdc/" + CODE_TEMPLATE_NAME);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_1").resolve(SOURCE_FILE_NAME),
                LinePosition.from(6, 0), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_1").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test
    public void testEmptyServiceCodeActionWithTableName() throws IOException {
        LineRange lineRange =
                LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(6, 1));
        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, lineRange);
        CodeActionArgument isPostgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, false);

        CodeActionInfo expectedCodeAction = CodeActionInfo.from("Add all functions with tableName parameter",
                List.of(locationArg, isPostgresListener));
        expectedCodeAction.setProviderName(
                EMPTY_SERVICE.getCode() + "/ballerinax/cdc/" + CODE_TEMPLATE_NAME_WITH_TABLE_NAME);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_1").resolve(SOURCE_FILE_NAME),
                LinePosition.from(6, 0), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_2").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test
    public void testServiceWithVariablesCodeAction() throws IOException {

        LineRange lineRange = LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(8, 1));
        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, lineRange);
        CodeActionArgument isPostgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, false);

        CodeActionInfo expectedCodeAction =
                CodeActionInfo.from("Add all functions", List.of(locationArg, isPostgresListener));
        expectedCodeAction.setProviderName(EMPTY_SERVICE.getCode() + "/ballerinax/cdc/" + CODE_TEMPLATE_NAME);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_2").resolve(SOURCE_FILE_NAME),
                LinePosition.from(8, 0), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_3").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test
    public void testServiceWithVariablesCodeActionWithTableName() throws IOException {

        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION,
                LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(8, 1)));
        CodeActionArgument isPostgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, false);

        CodeActionInfo expectedCodeAction = CodeActionInfo.from(
                "Add all functions with tableName parameter", List.of(locationArg, isPostgresListener));
        expectedCodeAction.setProviderName(
                EMPTY_SERVICE.getCode() + "/ballerinax/cdc/" + CODE_TEMPLATE_NAME_WITH_TABLE_NAME);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_2").resolve(SOURCE_FILE_NAME),
                LinePosition.from(8, 0), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_4").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test(enabled = false)
    public void testPostgresEmptyServiceCodeAction() throws IOException {

        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION,
                LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(7, 1)));
        CodeActionArgument isPostgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, true);

        CodeActionInfo expectedCodeAction =
                CodeActionInfo.from("Add all functions", List.of(locationArg, isPostgresListener));
        expectedCodeAction.setProviderName(
                EMPTY_SERVICE_POSTGRESQL.getCode() + "/ballerinax/cdc/" + CODE_TEMPLATE_NAME);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_3").resolve(SOURCE_FILE_NAME),
                LinePosition.from(7, 0), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_5").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test(enabled = false)
    public void testPostgresEmptyServiceCodeActionWithTableName() throws IOException {

        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION,
                LineRange.from(SOURCE_FILE_NAME, LinePosition.from(2, 0), LinePosition.from(7, 1)));
        CodeActionArgument isPostgresListener = CodeActionArgument.from(IS_POSTGRES_LISTENER, true);

        CodeActionInfo expectedCodeAction = CodeActionInfo.from(
                "Add all functions with tableName parameter", List.of(locationArg, isPostgresListener));
        expectedCodeAction.setProviderName(
                EMPTY_SERVICE_POSTGRESQL.getCode() + "/ballerinax/cdc/" + CODE_TEMPLATE_NAME_WITH_TABLE_NAME);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_3").resolve(SOURCE_FILE_NAME),
                LinePosition.from(7, 0), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_6").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test
    public void testMakeFunctionRemote() throws IOException {

        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, 231);
        CodeActionInfo expectedCodeAction = CodeActionInfo.from("Make the function remote", List.of(locationArg));
        expectedCodeAction.setProviderName(
                FUNCTION_SHOULD_BE_REMOTE.getCode() + "/ballerinax/cdc/" + MAKE_FUNCTION_REMOTE);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_4").resolve(SOURCE_FILE_NAME),
                LinePosition.from(12, 9), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_7").resolve(EXPECTED_FILE_NAME)
        );
    }


    @Test
    public void testChangeReturnTypeToError() throws IOException {

        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, TextRange.from(180, 7));
        CodeActionInfo expectedCodeAction =
                CodeActionInfo.from("Change return type to error?", List.of(locationArg));
        expectedCodeAction.setProviderName(
                INVALID_RETURN_TYPE_ERROR_OR_NIL.getCode() + "/ballerinax/cdc/" + CHANGE_RETURN_TYPE_TO_ERROR);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_4").resolve(SOURCE_FILE_NAME),
                LinePosition.from(7, 57), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_8").resolve(EXPECTED_FILE_NAME)
        );
    }

    @Test
    public void testChangeReturnTypeToCdcError() throws IOException {

        CodeActionArgument locationArg = CodeActionArgument.from(NODE_LOCATION, TextRange.from(180, 7));
        CodeActionInfo expectedCodeAction =
                CodeActionInfo.from("Change return type to cdc:Error?", List.of(locationArg));
        expectedCodeAction.setProviderName(
                INVALID_RETURN_TYPE_ERROR_OR_NIL.getCode() + "/ballerinax/cdc/" + CHANGE_RETURN_TYPE_TO_CDC_ERROR);

        performTest(
                BALLERINA_SOURCES.resolve("snippet_gen_service_4").resolve(SOURCE_FILE_NAME),
                LinePosition.from(7, 57), expectedCodeAction,
                EXPECTED_SOURCES.resolve("service_9").resolve(EXPECTED_FILE_NAME)
        );
    }

    private void performTest(Path srcPath, LinePosition cursorPos, CodeActionInfo expected, Path expectedSrc)
            throws IOException {
        Project project = ProjectLoader.loadProject(srcPath, getEnvironmentBuilder());
        List<CodeActionInfo> codeActions = getCodeActions(srcPath, cursorPos, project);

        Assert.assertFalse(codeActions.isEmpty(), "Expect at least 1 code actions");

        Optional<CodeActionInfo> found = findCodeAction(codeActions, expected);
        Assert.assertTrue(found.isPresent(), "Code action not found: " + GSON.toJson(expected));

        List<DocumentEdit> actualEdits = executeCodeAction(project, srcPath, found.get());
        Assert.assertEquals(actualEdits.size(), 1, "Expected changes to 1 file");

        String expectedFileUri = srcPath.toUri().toString();
        Optional<DocumentEdit> actualEdit = actualEdits.stream()
                .filter(docEdit -> docEdit.getFileUri().equals(expectedFileUri))
                .findFirst();

        Assert.assertTrue(actualEdit.isPresent(), "Edits not found for fileUri: " + expectedFileUri);

        String modifiedSourceCode = actualEdit.get().getModifiedSyntaxTree().toSourceCode();
        // Normalized actual to match Linux based expected source codes
        String normalizedModifiedSourceCode = modifiedSourceCode.replace(System.lineSeparator(), "\n");

        String expectedSourceCode = Files.readString(expectedSrc).replace(System.lineSeparator(), "\n");
        Assert.assertEquals(normalizedModifiedSourceCode, expectedSourceCode,
                "Actual source code didn't match expected source code");
    }

    private List<CodeActionInfo> getCodeActions(Path filePath, LinePosition cursorPos, Project project) {
        Package currentPackage = project.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        CodeActionManager codeActionManager = compilation.getCodeActionManager();

        DocumentId documentId = project.documentId(filePath);
        Document document = currentPackage.getDefaultModule().document(documentId);

        return compilation.diagnosticResult().diagnostics().stream()
                .filter(diagnostic -> TestUtils.isWithinRange(diagnostic.location().lineRange(), cursorPos))
                .flatMap(diagnostic -> {
                    CodeActionContextImpl context = CodeActionContextImpl.from(
                            filePath.toUri().toString(),
                            filePath,
                            cursorPos,
                            document,
                            compilation.getSemanticModel(documentId.moduleId()),
                            diagnostic);
                    return codeActionManager.codeActions(context).getCodeActions().stream();
                })
                .collect(Collectors.toList());
    }

    private List<DocumentEdit> executeCodeAction(Project project, Path filePath, CodeActionInfo codeAction) {
        Package currentPackage = project.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();

        DocumentId documentId = project.documentId(filePath);
        Document document = currentPackage.getDefaultModule().document(documentId);

        List<CodeActionArgument> codeActionArguments = codeAction.getArguments().stream()
                .map(arg -> CodeActionArgument.from(GSON.toJsonTree(arg)))
                .collect(Collectors.toList());

        CodeActionExecutionContext executionContext = CodeActionExecutionContextImpl.from(
                filePath.toUri().toString(),
                filePath,
                null,
                document,
                compilation.getSemanticModel(document.documentId().moduleId()),
                codeActionArguments);

        return compilation.getCodeActionManager()
                .executeCodeAction(codeAction.getProviderName(), executionContext);
    }

    private Optional<CodeActionInfo> findCodeAction(List<CodeActionInfo> codeActions, CodeActionInfo expected) {
        JsonObject expectedCodeAction = GSON.toJsonTree(expected).getAsJsonObject();
        return codeActions.stream()
                .filter(codeActionInfo -> {
                    JsonObject actualCodeAction = GSON.toJsonTree(codeActionInfo).getAsJsonObject();
                    return actualCodeAction.equals(expectedCodeAction);
                })
                .findFirst();
    }

}
