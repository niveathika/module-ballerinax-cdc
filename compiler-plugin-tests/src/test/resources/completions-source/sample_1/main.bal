import ballerinax/cdc;

// This is added to test some auto generated code segments.
// Please ignore the indentation.

service cdc:Service on new cdc:MySqlListener(database = {
    username: "root",
    password: "root"
}) {
    r
}