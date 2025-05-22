import ballerinax/cdc;

service cdc:Service on new MockListener(database = {
    username: "root",
    password: "root"
}) {

    remote function onRead(record {||} after) returns cdc:Error? {
        return "Hello World";
    }

    function onCreate(record {||} after) {

    }

}
