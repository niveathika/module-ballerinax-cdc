import ballerinax/cdc;

service cdc:Service on new MockListener(database = {
    username: "root",
    password: "root"
}) {
    int x = 5;
    string y = "xx";
}
