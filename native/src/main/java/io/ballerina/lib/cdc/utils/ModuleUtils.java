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
package io.ballerina.lib.cdc.utils;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.Module;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static io.ballerina.runtime.api.constants.RuntimeConstants.ORG_NAME_SEPARATOR;

/**
 * This class will hold module related utility functions.
 */
public class ModuleUtils {

    private static Module module = null;
    private static String packageIdentifier;

    private ModuleUtils() {
    }

    public static Module getModule() {
        return module;
    }

    public static void setModule(Environment env) {
        module = env.getCurrentModule();
        packageIdentifier = Constants.PACKAGE + ORG_NAME_SEPARATOR + Constants.MODULE +
                Constants.COLON + module.getMajorVersion();
    }

    public static String getPackageIdentifier() {
        return packageIdentifier;
    }

    public static void initializeLoggingConfigurations() {
        // todo Need further investigation to see if we can disable only kafka and debezium logs
        // Root logger
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        // Set level to SEVERE
        rootLogger.setLevel(Level.SEVERE);
    }
}
