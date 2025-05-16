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
package io.ballerina.lib.cdc.compiler;

import io.ballerina.tools.diagnostics.DiagnosticSeverity;

import static io.ballerina.tools.diagnostics.DiagnosticSeverity.ERROR;
import static io.ballerina.tools.diagnostics.DiagnosticSeverity.INTERNAL;

public enum DiagnosticCodes {
    NO_VALID_FUNCTION("CDC_101", ERROR, "missing valid remote function: expected at least one of %s functions"),
    INVALID_RESOURCE_FUNCTION("CDC_102", ERROR, "resource functions are not allowed"),
    FUNCTION_SHOULD_BE_REMOTE("CDC_103", ERROR, "must be a ''remote'' function"),
    INVALID_PARAM_COUNT("CDC_104", ERROR, "invalid parameter count: expected %s"),
    MUST_BE_REQUIRED_PARAM("CDC_105", ERROR, "must be a required parameter"),
    INVALID_PARAM_TYPE("CDC_106", ERROR, "invalid type: expected ''%s''"),
    NOT_OF_SAME_TYPE("CDC_107", ERROR, "invalid type: must be of the same type"),
    INVALID_RETURN_TYPE_ERROR_OR_NIL("CDC_108", ERROR, "invalid return type: expected ''error?'' or ''cdc:Error?''"),
    INVALID_MULTIPLE_LISTENERS("CDC_109", ERROR, "service can only be attached to one ''cdc:Listener''"),
    // Internal diagnostics used to indicate empty service
    EMPTY_SERVICE("CDC_601", INTERNAL, ""),
    EMPTY_SERVICE_POSTGRESQL("CDC_602", INTERNAL, "");

    private final String code;
    private final DiagnosticSeverity severity;
    private final String message;

    DiagnosticCodes(String code, DiagnosticSeverity severity, String message) {
        this.code = code;
        this.severity = severity;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public DiagnosticSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }
}
