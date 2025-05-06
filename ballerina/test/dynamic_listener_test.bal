// Copyright (c) 2025, WSO2 LLC. (http://www.wso2.org).
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





// type MyRecord record {
//     record {}   a;
//     record {}?   b?;
//     record {}?   c?; // field is optional
//     record {}?   d?;
// };

// type MyRecord1 record {
//     record{} a = { tag: "CHI", name: "CH1" };
//     record{} b = { tag: "SE1", name: "SE1" };   
//   //  record{} c = { tag: "SE2", name: "SE2" };
//     record{} d = { tag: "SE3", name: "SE3" };
// };


// function getRecord1Values returns MyRecord1 {
//     MyRecord1 customeRecord = {
//         a: ()
// };

// if anyof values present {
// customeRecord.a = { tag: "CHI", name: "CH1" };
// }

type Tag record {
    string tag? = "CH1";
    string name?;
};

type MyRecord record {
    string tag;
    string name1?; // name1 is optional, if present the type is string
    Tag? name?; // name 2 is optional, if present the type is string or nil -> "Truncatable fields" preserveEmptyFields

};


MyRecord myrecord = {
    tag: ""
};

MyRecord myrecord2 = {
    tag: ""
};

if //anyof values present {
    myrecord2.name = {}
}



SE1*asdad**asdada*asdad*~

SE1*asdad*~
