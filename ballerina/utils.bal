// Copyright (c) 2025 WSO2 LLC. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

const string NAME = "name";
const string CONNECTOR_CLASS = "connector.class";
const string MAX_QUEUE_SIZE = "max.queue.size";
const string MAX_BATCH_SIZE = "max.batch.size";
const string EVENT_PROCESSING_FAILURE_HANDLING_MODE = "event.processing.failure.handling.mode";
const string SNAPSHOT_MODE = "snapshot.mode";
const string SKIPPED_OPERATIONS = "skipped.operations";
const string SKIP_MESSAGES_WITHOUT_CHANGE = "skip.messages.without.change";
const string DATABASE_HOSTNAME = "database.hostname";
const string DATABASE_PORT = "database.port";
const string DATABASE_USER = "database.user";
const string DATABASE_PASSWORD = "database.password";
const string DATABASE_QUERY_TIMEOUTS_MS = "database.query.timeout.ms";
const string DECIMAL_HANDLING_MODE = "decimal.handling.mode";
const string SCHEMA_HISTORY_INTERNAL = "schema.history.internal";
const string TOPIC_PREFIX = "topic.prefix";
const string SCHEMA_HISTORY_INTERNAL_KAFKA_BOOTSTRAP_SERVERS = "schema.history.internal.kafka.bootstrap.servers";
const string SCHEMA_HISTORY_INTERNAL_KAFKA_TOPIC = "schema.history.internal.kafka.topic";
const string SCHEMA_HISTORY_INTERNAL_FILE_FILENAME = "schema.history.internal.file.filename";
const string OFFSET_STORAGE = "offset.storage";
const string OFFSET_FLUSH_INTERVAL_MS = "offset.flush.interval.ms";
const string OFFSET_FLUSH_TIMEOUT_MS = "offset.flush.timeout.ms";
const string OFFSET_STORAGE_FILE_FILENAME = "offset.storage.file.filename";
const string OFFSET_BOOTSTRAP_SERVERS = "bootstrap.servers";
const string OFFSET_STORAGE_TOPIC = "offset.storage.topic";
const string OFFSET_STORAGE_PARTITIONS = "offset.storage.partitions";
const string OFFSET_STORAGE_REPLICATION_FACTOR = "offset.storage.replication.factor";
const string INCLUDE_SCHEMA_CHANGES = "include.schema.changes";
const string TOMBSTONES_ON_DELETE = "tombstones.on.delete";

const string SCHEMA_INCLUDE_LIST = "schema.include.list";
const string SCHEMA_EXCLUDE_LIST = "schema.exclude.list";

const string MYSQL_DATABASE_SERVER_ID = "database.server.id";
const string MYSQL_DATABASE_INCLUDE_LIST = "database.include.list";
const string MYSQL_DATABASE_EXCLUDE_LIST = "database.exclude.list";

const string MSSQL_DATABASE_NAMES = "database.names";
const string MSSQL_DATABASE_INSTANCE = "database.instance";
const string MSSQL_DATABASE_ENCRYPT = "database.encrypt";

const string POSTGRESQL_DATABASE_NAME = "database.dbname";
const string POSTGRESQL_PLUGIN_NAME = "plugin.name";
const string POSTGRESQL_SLOT_NAME = "slot.name";
const string POSTGRESQL_PUBLICATION_NAME = "publication.name";

const string ORACLE_DATABASE_NAME = "database.dbname";
const string ORACLE_URL = "database.url";
const string ORACLE_PDB_NAME = "database.dbname";
const string ORACLE_CONNECTION_ADAPTER = "database.connection.adapter";

public isolated function getDebeziumProperties(ListenerConfiguration config) returns map<string> {
    map<string> configMap = {};

    // Common configurations
    populateCommonConfigurations(config, configMap);

    // Schema history storage configurations
    populateSchemaHistoryConfigurations(config.internalSchemaStorage, configMap);

    // Offset storage configurations
    populateOffsetStorageConfigurations(config.offsetStorage, configMap);

    // Database-specific configurations
  //  populateDatabaseConfigurations(config.database, configMap);

    configMap[INCLUDE_SCHEMA_CHANGES] = "false";
    return configMap;
}

// Populates common configurations shared across all databases
isolated function populateCommonConfigurations(ListenerConfiguration config, map<string> configMap) {
    configMap[NAME] = config.engineName;
    configMap[CONNECTOR_CLASS] = config.connectorClass;
    configMap[MAX_QUEUE_SIZE] = config.maxQueueSize.toString();
    configMap[MAX_BATCH_SIZE] = config.maxBatchSize.toString();
    configMap[EVENT_PROCESSING_FAILURE_HANDLING_MODE] = config.eventProcessingFailureHandlingMode;
    configMap[SNAPSHOT_MODE] = config.snapshotMode;
    configMap[SKIPPED_OPERATIONS] = string:'join(",", ...config.skippedOperations);
    configMap[SKIP_MESSAGES_WITHOUT_CHANGE] = config.skipMessagesWithoutChange.toString();
    configMap[TOMBSTONES_ON_DELETE] = config.sendTombstonesOnDelete.toString();
    configMap[DECIMAL_HANDLING_MODE] = config.decimalHandlingMode;
    configMap[DATABASE_QUERY_TIMEOUTS_MS] = getMillisecondValueOf(config.queryTimeout);
}

// Populates schema history storage configurations
isolated function populateSchemaHistoryConfigurations(FileInternalSchemaStorage|KafkaInternalSchemaStorage schemaHistoryInternal, map<string> configMap) {
    configMap[SCHEMA_HISTORY_INTERNAL] = schemaHistoryInternal.className;
    configMap[TOPIC_PREFIX] = schemaHistoryInternal.topicPrefix;

    if schemaHistoryInternal is KafkaInternalSchemaStorage {
        string|string[] bootstrapServers = schemaHistoryInternal.bootstrapServers;
        configMap[SCHEMA_HISTORY_INTERNAL_KAFKA_BOOTSTRAP_SERVERS] = bootstrapServers is string ? bootstrapServers : string:'join(",", ...bootstrapServers);
        configMap[SCHEMA_HISTORY_INTERNAL_KAFKA_TOPIC] = schemaHistoryInternal.topicName;
    } else {
        configMap[SCHEMA_HISTORY_INTERNAL_FILE_FILENAME] = schemaHistoryInternal.fileName;
    }
}

// Populates offset storage configurations
isolated function populateOffsetStorageConfigurations(FileOffsetStorage|KafkaOffsetStorage offsetStorage, map<string> configMap) {
    configMap[OFFSET_STORAGE] = offsetStorage.className;
    configMap[OFFSET_FLUSH_INTERVAL_MS] = getMillisecondValueOf(offsetStorage.flushInterval);
    configMap[OFFSET_FLUSH_TIMEOUT_MS] = getMillisecondValueOf(offsetStorage.flushTimeout);

    if offsetStorage is FileOffsetStorage {
        configMap[OFFSET_STORAGE_FILE_FILENAME] = offsetStorage.fileName;
    } else {
        string|string[] offsetStorageBootstrapServers = offsetStorage.bootstrapServers;
        configMap[OFFSET_BOOTSTRAP_SERVERS] = offsetStorageBootstrapServers is string ? offsetStorageBootstrapServers : string:'join(",", ...offsetStorageBootstrapServers);
        configMap[OFFSET_STORAGE_TOPIC] = offsetStorage.topicName;
        configMap[OFFSET_STORAGE_PARTITIONS] = offsetStorage.partitions.toString();
        configMap[OFFSET_STORAGE_REPLICATION_FACTOR] = offsetStorage.replicationFactor.toString();
    }
}


// Populates MSSQL-specific configurations
isolated function populateMsSqlConfigurations(MsSqlDatabaseConnection connection, map<string> configMap) {
    if connection.databaseInstance !is () {
        configMap[MSSQL_DATABASE_INSTANCE] = connection.databaseInstance ?: "";
    }

    string|string[] databaseNames = connection.databaseNames;
    configMap[MSSQL_DATABASE_NAMES] = databaseNames is string ? databaseNames : string:'join(",", ...databaseNames);

    populateSchemaConfigurations(connection, configMap);

    if connection.secure is () {
        configMap[MSSQL_DATABASE_ENCRYPT] = "false";
    }
}

// Populates PostgreSQL-specific configurations
isolated function populatePostgresConfigurations(PostgresDatabaseConnection connection, map<string> configMap) {
    configMap[POSTGRESQL_DATABASE_NAME] = connection.databaseName;
    populateSchemaConfigurations(connection, configMap);
    configMap[POSTGRESQL_PLUGIN_NAME] = connection.pluginName;
    configMap[POSTGRESQL_SLOT_NAME] = connection.slotName;
    configMap[POSTGRESQL_PUBLICATION_NAME] = connection.publicationName;
}

// Populates Oracle-specific configurations
isolated function populateOracleConfigurations(OracleDatabaseConnection connection, map<string> configMap) {
    configMap[ORACLE_DATABASE_NAME] = connection.databaseName;

    if connection.url !is () {
        configMap[ORACLE_URL] = connection.url ?: "";
    }

    if connection.pdbName !is () {
        configMap[ORACLE_PDB_NAME] = connection.pdbName ?: "";
    }

    configMap[ORACLE_CONNECTION_ADAPTER] = connection.connectionAdopter;
    populateSchemaConfigurations(connection, configMap);
}

// Populates schema inclusion/exclusion configurations
isolated function populateSchemaConfigurations(MsSqlDatabaseConnection|PostgresDatabaseConnection|OracleDatabaseConnection connection, map<string> configMap) {
    string|string[]? includedSchemas = connection.includedSchemas;
    if includedSchemas !is () {
        configMap[SCHEMA_INCLUDE_LIST] = includedSchemas is string ? includedSchemas : string:'join(",", ...includedSchemas);
    }

    string|string[]? excludedSchemas = connection.excludedSchemas;
    if excludedSchemas !is () {
        configMap[SCHEMA_EXCLUDE_LIST] = excludedSchemas is string ? excludedSchemas : string:'join(",", ...excludedSchemas);
    }
}

public isolated function getMillisecondValueOf(decimal value) returns string {
    string milliSecondVal = (value * 1000).toBalString();
    return milliSecondVal.substring(0, milliSecondVal.indexOf(".") ?: milliSecondVal.length());
}
