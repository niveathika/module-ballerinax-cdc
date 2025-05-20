// Copyright (c) 2025, WSO2 LLC. (https://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied. See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/test;

@test:Config {}
function testGetDebeziumProperties() {
    // Expected properties map
    map<string> expectedProperties = {
        "name": "ballerina-cdc-connector",
        "max.queue.size": "8192",
        "max.batch.size": "2048",
        "event.processing.failure.handling.mode": "warn",
        "snapshot.mode": "initial",
        "skipped.operations": "t",
        "skip.messages.without.change": "false",
        "tombstones.on.delete": "false",
        "decimal.handling.mode": "double",
        "schema.history.internal": "io.debezium.storage.kafka.history.KafkaSchemaHistory",
        "topic.prefix": "bal_cdc_schema_history",
        "schema.history.internal.kafka.bootstrap.servers": "",
        "schema.history.internal.kafka.topic": "bal_cdc_internal_schema_history",
        "offset.storage": "org.apache.kafka.connect.storage.KafkaOffsetBackingStore",
        "offset.flush.interval.ms": "60000",
        "offset.flush.timeout.ms": "5000",
        "bootstrap.servers": "",
        "offset.storage.topic": "bal_cdc_offsets",
        "offset.storage.partitions": "1",
        "offset.storage.replication.factor": "2",
        "include.schema.changes": "false",
        "database.query.timeout.ms": "60000"
    };

    ListenerConfiguration config = {
        offsetStorage: {
            bootstrapServers: ""
        },
        internalSchemaStorage: {
            bootstrapServers: ""
        }
    };

    map<string> actualProperties = {};
    // Call the function to test
    populateDebeziumProperties(config, actualProperties);

    // Validate the returned properties
    test:assertEquals(actualProperties, expectedProperties, msg = "Debezium properties do not match the expected values.");
}

@test:Config {}
function testGetDatabaseDebeziumProperties() {
    // Expected properties map
    map<string> expectedProperties = {
        "connector.class": "",
        "database.hostname": "localhost",
        "database.port": "3307",
        "database.user": "root",
        "database.password": "root",
        "tasks.max": "1",
        "connect.timeout.ms": "600000",
        "database.ssl.mode": "disabled",
        "database.ssl.keystore": "",
        "database.ssl.keystore.password": "",
        "database.ssl.truststore": "",
        "database.ssl.truststore.password": "",
        "table.include.list": "",
        "column.include.list": "ya,tan"
        };

    DatabaseConnection config = {
        username: "root",
        password: "root",
        port: 3307,
        hostname: "localhost",
        connectorClass: "",
        connectTimeout: 600,
        tasksMax: 1,
        secure: {
            sslMode: DISABLED,
            keyStore: {path: "", password: ""},
            trustStore: {path: "", password: ""}
        },
        includedTables: "",
        includedColumns: ["ya", "tan"]
    };

    map<string> actualProperties = {};
    // Call the function to test
    populateDatabaseConfigurations(config, actualProperties);

    // Validate the returned properties
    test:assertEquals(actualProperties, expectedProperties, msg = "Debezium properties do not match the expected values.");
}
