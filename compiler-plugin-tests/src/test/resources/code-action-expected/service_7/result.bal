import ballerinax/cdc;

service cdc:Service on new MockListener(database = {
    username: "root",
    password: "root"
}) {

    remote function onRead(record {||} after) returns string? {
        return "Hello World";
    }

    remote function onCreate(record {||} after) {

    }

}
