import ballerinax/cdc;

// This is added to test some auto generated code segments.
// Please ignore the indentation.

service on new MockListener(database = {
    username: "root",
    password: "root"
}) {
    r
}

# Represents a Ballerina CDC MySQL Listener.
public isolated class MockListener {
    *cdc:Listener;
    public isolated function init(*MySqlListenerConfiguration config) {
    }

    public isolated function attach(cdc:Service s, string[]|string? name = ()) returns cdc:Error? {
    }

    public isolated function 'start() returns cdc:Error? {
    }

    public isolated function detach(cdc:Service s) returns cdc:Error? {
    }

    public isolated function gracefulStop() returns cdc:Error? {
    }

    public isolated function immediateStop() returns cdc:Error? {
    }
}

public type MySqlListenerConfiguration record {|
    MySqlDatabaseConnection database;
    *cdc:ListenerConfiguration;
|};

public type MySqlDatabaseConnection record {|
    *cdc:DatabaseConnection;
    string connectorClass = "io.debezium.connector.mysql.MySqlConnector";
    string hostname = "localhost";
    int port = 3306;
|};
