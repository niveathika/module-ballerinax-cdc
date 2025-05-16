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

import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.tools.diagnostics.Diagnostic;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.FUNCTION_SHOULD_BE_REMOTE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_MULTIPLE_LISTENERS;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_PARAM_COUNT;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_PARAM_TYPE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_RESOURCE_FUNCTION;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.INVALID_RETURN_TYPE_ERROR_OR_NIL;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.MUST_BE_REQUIRED_PARAM;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.NOT_OF_SAME_TYPE;
import static io.ballerina.lib.cdc.compiler.DiagnosticCodes.NO_VALID_FUNCTION;
import static io.ballerina.lib.cdc.compiler.TestUtils.getEnvironmentBuilder;

/**
 * Tests the custom CDC compiler plugin.
 */
public class CompilerPluginTest {

    private static final Path RESOURCE_DIRECTORY =
            Paths.get("src", "test", "resources", "diagnostics").toAbsolutePath();

    private PackageCompilation loadAndCompilePackage(String path) {
        Path projectDirPath = RESOURCE_DIRECTORY.resolve(path);
        BuildProject project = BuildProject.load(getEnvironmentBuilder(), projectDirPath);
        return project.currentPackage().getCompilation();
    }

    private void assertDiagnostics(DiagnosticResult diagnosticResult, Object[][] expectedErrors) {
        Assert.assertEquals(diagnosticResult.errors().size(), expectedErrors.length);
        Diagnostic[] diagnostics = diagnosticResult.errors().toArray(new Diagnostic[0]);
        for (int i = 0; i < expectedErrors.length; i++) {
            String expectedCode =
                    expectedErrors[i][0] instanceof String ?
                            (String) expectedErrors[i][0] :
                            ((DiagnosticCodes) expectedErrors[i][0]).getCode();
            String expectedMessage = (String) expectedErrors[i][1];
            String location = (String) expectedErrors[i][2];
            Assert.assertEquals(diagnostics[i].diagnosticInfo().code(), expectedCode);
            Assert.assertEquals(diagnostics[i].diagnosticInfo().messageFormat(), expectedMessage);
            Assert.assertEquals(diagnostics[i].location().lineRange().toString(), location);
        }
    }

    @Test(description = "Service validating return types")
    public void testValidService1() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_1");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validating import as")
    public void testValidService2() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_2");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate basic service")
    public void testValidService3() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_3");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validating using only record type")
    public void testValidService4() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_4");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate onUpdate method")
    public void testValidService5() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_5");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validating readonly record param type")
    public void testValidService6() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_6");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate onError function")
    public void testValidService7() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_7");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate onError function return types")
    public void testValidService8() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_8");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate data binding parameter")
    public void testValidService9() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_9");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate readonly data binding parameter")
    public void testValidService10() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_10");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate applying to only cdc listener")
    public void testValidService11() {
        PackageCompilation currentPackage = loadAndCompilePackage("valid_service_11");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        Assert.assertEquals(diagnosticResult.errors().size(), 0);
    }

    @Test(description = "Validate no remote method")
    public void testInvalidService1() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_1");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        NO_VALID_FUNCTION,
                        "missing valid remote function: expected at least one of " +
                                "''onRead'', ''onCreate'', ''onUpdate'' or ''onDelete'' functions",
                        "(23:0,24:1)"
                },
                {
                        NO_VALID_FUNCTION,
                        "missing valid remote function: expected at least one of " +
                                "''onRead'', ''onCreate'', ''onUpdate'', ''onDelete'' or ''onTruncate'' functions",
                        "(32:0,33:1)"
                }
        });
    }

    @Test(description = "No validation if base compilation unsuccessful")
    public void testInvalidService2() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_2");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        "BCE2063",
                        "missing.required.parameter",
                        "(18:41,21:2)"
                },
                {
                        "BCE2039",
                        "undefined.parameter",
                        "(18:46,21:1)"
                }});
    }

    @Test(description = "Validate onRead without remote keyword")
    public void testInvalidService3() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_3");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        FUNCTION_SHOULD_BE_REMOTE,
                        "must be a ''remote'' function",
                        "(25:4,27:5)"
                }
        });
    }

    @Test(description = "Validate invalid parameter as the data binding param")
    public void testInvalidService4() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_4");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(25:27,25:33)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(29:29,29:37)"
                }
        });
    }

    @Test(description = "Validate invalid parameter in 2 parameter scenario")
    public void testInvalidService5() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_5");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(25:29,25:35)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(31:49,31:55)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(37:29,37:35)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(37:44,37:50)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(43:29,43:35)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(43:44,43:52)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''record''",
                        "(51:49,51:69)"
                },
                {
                        NOT_OF_SAME_TYPE,
                        "invalid type: must be of the same type",
                        "(61:29,61:74)"
                }
        });
    }

    @Test(description = "Validate invalid return type")
    public void testInvalidService6() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_6");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_RETURN_TYPE_ERROR_OR_NIL,
                        "invalid return type: expected ''error?'' or ''cdc:Error?''",
                        "(24:76,24:82)"
                }
        });
    }

    @Test(description = "Validate tableName param in 1 parameter scenario")
    public void testInvalidService7() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_7");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''string''",
                        "(24:68,24:72)"
                }
        });
    }

    @Test(description = "Validate onUpdate has same type")
    public void testInvalidService8() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_8");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        NOT_OF_SAME_TYPE,
                        "invalid type: must be of the same type",
                        "(24:29,24:56)"
                }
        });
    }

    @Test(description = "Validate multiple listeners")
    public void testInvalidService9() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_9");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_MULTIPLE_LISTENERS,
                        "service can only be attached to one ''cdc:Listener''",
                        "(28:0,32:1)"
                }
        });
    }

    @Test(description = "Validate resource function")
    public void testInvalidService10() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_10");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_RESOURCE_FUNCTION,
                        "resource functions are not allowed",
                        "(25:4,27:5)"
                }
        });
    }

    @Test(description = "Validate 0 parameter scenario")
    public void testInvalidService11() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_11");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected one parameter of type ''record'' and " +
                                "may include an additional parameter of type ''string''",
                        "(24:28,24:30)"
                },
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected two parameters of type ''record'' and " +
                                "may include an additional parameter of type ''string''",
                        "(27:28,27:30)"
                },
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected two parameters of type ''record'' and " +
                                "may include an additional parameter of type ''string''",
                        "(32:28,32:48)"
                }
        });
    }

    @Test(description = "Validate non-required parameter")
    public void testInvalidService12() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_12");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        MUST_BE_REQUIRED_PARAM,
                        "must be a required parameter",
                        "(24:47,24:68)"
                }
        });
    }

    @Test(description = "Validate invalid parameter for onError")
    public void testInvalidService14() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_14");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''error?'' or ''cdc:Error?''",
                        "(28:28,28:39)"
                }
        });
    }

    @Test(description = "Validate 0 parameter for onError")
    public void testInvalidService15() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_15");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected one parameter of type ''error?'' or ''cdc:Error?''",
                        "(28:27,28:29)"
                }
        });
    }

    @Test(description = "Validate no remote keyword for onError")
    public void testInvalidService17() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_17");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        FUNCTION_SHOULD_BE_REMOTE,
                        "must be a ''remote'' function",
                        "(28:4,30:5)"
                }
        });
    }

    @Test(description = "Validate resource functions with onError")
    public void testInvalidService18() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_18");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_RESOURCE_FUNCTION,
                        "resource functions are not allowed",
                        "(28:4,29:5)"
                }
        });
    }

    @Test(description = "Validate parameters more than 3 for onConsumerRecord")
    public void testInvalidService19() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_19");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected one parameter of type ''record'' and " +
                                "may include an additional parameter of type ''string''",
                        "(24:28,24:70)"
                },
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected two parameters of type ''record'' and " +
                                "may include an additional parameter of type ''string''",
                        "(28:28,28:89)"
                },
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected one parameter of type ''error?'' or ''cdc:Error?''",
                        "(32:27,32:58)"
                }
        });
    }

    @Test(description = "Validate parameters for onTruncate")
    public void testInvalidService20() {
        PackageCompilation currentPackage = loadAndCompilePackage("invalid_service_20");
        DiagnosticResult diagnosticResult = currentPackage.diagnosticResult();
        assertDiagnostics(diagnosticResult, new Object[][]{
                {
                        INVALID_PARAM_COUNT,
                        "invalid parameter count: expected no parameters or " +
                                "at most one optional parameter of type ''string''",
                        "(38:30,38:55)"
                },
                {
                        INVALID_PARAM_TYPE,
                        "invalid type: expected ''string''",
                        "(44:31,44:35)"
                },
                {
                        INVALID_RETURN_TYPE_ERROR_OR_NIL,
                        "invalid return type: expected ''error?'' or ''cdc:Error?''",
                        "(64:51,64:67)"
                },
                {
                        INVALID_RETURN_TYPE_ERROR_OR_NIL,
                        "invalid return type: expected ''error?'' or ''cdc:Error?''",
                        "(70:51,70:57)"
                }
        });
    }

}
