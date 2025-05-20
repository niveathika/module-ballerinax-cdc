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
import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NonTerminalNode;
import io.ballerina.projects.CompletionManager;
import io.ballerina.projects.CompletionResult;
import io.ballerina.projects.Document;
import io.ballerina.projects.DocumentId;
import io.ballerina.projects.Module;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.Project;
import io.ballerina.projects.directory.ProjectLoader;
import io.ballerina.projects.plugins.completion.CompletionContext;
import io.ballerina.projects.plugins.completion.CompletionContextImpl;
import io.ballerina.projects.plugins.completion.CompletionException;
import io.ballerina.projects.plugins.completion.CompletionItem;
import io.ballerina.tools.text.LinePosition;
import io.ballerina.tools.text.TextRange;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CompletionTest {

    private static final Gson GSON = new Gson();
    private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources").toAbsolutePath();

    @DataProvider(name = "completion-data-provider")
    public Object[][] dataProvider() {
        return new Object[][]{
                {"sample_1/main.bal", 9, 5, "completions.json"},
                {"sample_2/main.bal", 10, 5, "completions.json"},
                {"sample_2/main.bal", 25, 5, "completions.json"}
        };
    }

    @Test(enabled = false, dataProvider = "completion-data-provider")
    public void test(String sourceFile, int line, int offset, String expectedFile) throws IOException {
        Path sourceFilePath = RESOURCE_DIRECTORY.resolve("completions-source").resolve(sourceFile);
        Path expectedFilePath = RESOURCE_DIRECTORY.resolve("completions-expected").resolve(expectedFile);
        TestConfig expectedList = GSON.fromJson(Files.newBufferedReader(expectedFilePath), TestConfig.class);

        List<CompletionItem> expectedItems = expectedList.getItems();
        LinePosition cursorPos = LinePosition.from(line, offset);

        Project project = ProjectLoader.loadProject(sourceFilePath, TestUtils.getEnvironmentBuilder());
        CompletionResult completionResult = getCompletions(sourceFilePath, cursorPos, project);
        List<CompletionItem> actualItems = completionResult.getCompletionItems();
        List<CompletionException> errors = completionResult.getErrors();
        Assert.assertTrue(errors.isEmpty());
        Assert.assertTrue(compareCompletionItems(actualItems, expectedItems));

    }

    private CompletionResult getCompletions(Path filePath, LinePosition cursorPos, Project project) {

        io.ballerina.projects.Package currentPackage = project.currentPackage();
        PackageCompilation compilation = currentPackage.getCompilation();
        CompletionManager completionManager = compilation.getCompletionManager();

        DocumentId documentId = project.documentId(filePath);
        Document document = currentPackage.getDefaultModule().document(documentId);
        Module module = project.currentPackage().module(documentId.moduleId());

        int cursorPositionInTree = document.textDocument().textPositionFrom(cursorPos);
        TextRange range = TextRange.from(cursorPositionInTree, 0);
        NonTerminalNode nodeAtCursor = ((ModulePartNode) document.syntaxTree().rootNode()).findNode(range);

        CompletionContext completionContext = CompletionContextImpl.from(filePath.toUri().toString(),
                filePath, cursorPos, cursorPositionInTree, nodeAtCursor, document,
                module.getCompilation().getSemanticModel());

        return completionManager.completions(completionContext);
    }

    private static boolean compareCompletionItems(List<CompletionItem> actualItems,
                                                  List<CompletionItem> expectedItems) {
        List<String> actualList = actualItems.stream()
                .map(CompletionTest::getCompletionItemPropertyString)
                .toList();
        List<String> expectedList = expectedItems.stream()
                .map(CompletionTest::getCompletionItemPropertyString)
                .toList();
        return actualList.containsAll(expectedList) && actualItems.size() == expectedItems.size();
    }

    private static String getCompletionItemPropertyString(CompletionItem completionItem) {
        // Here we replace the Windows specific \r\n to \n for evaluation only
        String additionalTextEdits = "";
        if (completionItem.getAdditionalTextEdits() != null && !completionItem.getAdditionalTextEdits().isEmpty()) {
            additionalTextEdits = "," + GSON.toJson(completionItem.getAdditionalTextEdits());
        }
        return ("{" +
                completionItem.getInsertText() + "," +
                completionItem.getLabel() + "," +
                completionItem.getPriority() +
                additionalTextEdits +
                "}").replace("\r\n", "\n").replace("\\r\\n", "\\n");
    }

    /**
     * Represents the completion test config.
     */
    public static class TestConfig {
        private List<CompletionItem> items;

        public List<CompletionItem> getItems() {
            return items;
        }
    }
}


