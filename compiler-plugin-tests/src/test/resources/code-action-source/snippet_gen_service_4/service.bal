import ballerinax/cdc;

service on new cdc:MySqlListener(database = {
    username: "root",
    password: "root"
}) {

    remote function onRead(record {||} after) returns string? {
        return "Hello World";
    }

    function onCreate(record {||} after) {

    }

}
