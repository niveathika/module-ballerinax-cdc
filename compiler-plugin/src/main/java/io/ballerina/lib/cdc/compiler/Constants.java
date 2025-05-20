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

import java.util.List;

public final class Constants {
    public static final String PACKAGE_ORG = "ballerinax";
    public static final String PACKAGE_PREFIX = "cdc";
    public static final String POSTGRESQL_PACKAGE_PREFIX = "postgres";

    // Parameters
    public static final String ERROR_PARAM = "Error";

    public static final String CDC_LISTENER_NAME = "Listener";
    public static final List<String> VALID_FUNCTIONS = List.of(
            ServiceMethodNames.ON_READ,
            ServiceMethodNames.ON_CREATE,
            ServiceMethodNames.ON_DELETE,
            ServiceMethodNames.ON_UPDATE,
            ServiceMethodNames.ON_TRUNCATE
    );
    public static final List<String> VALID_FUNCTIONS_NON_POSTGRES = List.of(
            ServiceMethodNames.ON_READ,
            ServiceMethodNames.ON_CREATE,
            ServiceMethodNames.ON_DELETE,
            ServiceMethodNames.ON_UPDATE
    );

    private Constants() {
    }

    public static final class ServiceMethodNames {
        public static final String ON_READ = "onRead";
        public static final String ON_CREATE = "onCreate";
        public static final String ON_UPDATE = "onUpdate";
        public static final String ON_DELETE = "onDelete";
        public static final String ON_ERROR = "onError";
        public static final String ON_TRUNCATE = "onTruncate";

        private ServiceMethodNames() {
        }
    }
}
