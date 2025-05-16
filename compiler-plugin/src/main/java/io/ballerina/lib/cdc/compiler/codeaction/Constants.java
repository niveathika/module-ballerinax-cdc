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
package io.ballerina.lib.cdc.compiler.codeaction;

public final class Constants {

    // Code template related constants
    public static final String NODE_LOCATION = "node.location";
    public static final String IS_POSTGRES_LISTENER = "is.postgres.listener";
    public static final String LS = System.lineSeparator();
    public static final String TAB = "    ";
    public static final String CODE_TEMPLATE_NAME = "ADD_FUNCTIONS_CODE_SNIPPET";
    public static final String CODE_TEMPLATE_NAME_WITH_TABLE_NAME = "ADD_FUNCTIONS_W_TABLE_NAME_CODE_SNIPPET";
    public static final String MAKE_FUNCTION_REMOTE = "MAKE_FUNCTION_REMOTE";
    public static final String CHANGE_RETURN_TYPE_TO_CDC_ERROR = "CHANGE_RETURN_TYPE_CDC:ERROR?";
    public static final String CHANGE_RETURN_TYPE_TO_ERROR = "CHANGE_RETURN_TYPE_ERROR?";

    private Constants() {   // Prevent instantiation
    }
}
