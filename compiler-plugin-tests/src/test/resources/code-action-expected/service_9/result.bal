import ballerinax/cdc;

service on new cdc:MySqlListener(database = {
    username: "root",
    password: "root"
}) {

    remote function onRead(record {||} after) returns cdc:Error? {
        return "Hello World";
    }

    function onCreate(record {||} after) {

    }

}
