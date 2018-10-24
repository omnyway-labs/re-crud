(ns re-crud.test-data)

(def swagger
  {"swagger" "2.0"
   "info"
   {"title" "re-crud-example API"
    "description" "This is a regular CRUD app for demo purposes"
    "version" "1.0.0"}
   "host" "localhost:8000"
   "schemes" ["https"]
   "basePath" "/"
   "produces" ["application/json"]
   "paths"
   {"/users"
    {"get"
     {"operationId" "listUsers"
      "summary" "List all Users"
      "responses"
      {"200"
       {"description" "An array of users"
        "schema" {"type" "array"
                  "items" {"$ref" "#/definitions/User"}}}}}
     "post"
     {"summary" "Create a user"
      "operationId" "createUser"
      "responses" {"200" {"description" "Create A user"
                          "schema" {"$ref" "#/definitions/User"}}}
      "parameters" [{"name" "body"
                     "in" "body"
                     "required" true
                     "schema" {"$ref" "#/definitions/UserParam"}}]}}
    "/users/{user-id}"
    {"get"
     {"summary" "Get a user"
      "operationId" "getUser"
      "responses" {"200" {"description" "A user"
                          "schema" {"$ref" "#/definitions/User"}}}
      "parameters" [{"name" "user-id"
                     "in" "path"
                     "required" true
                     "type" "number"
                     "format" "integer"}]}
     "patch"
     {"summary" "Update a user info"
      "operationId" "updateUser"
      "responses" {"200" {"description" "Update A user"
                          "schema" {"$ref" "#/definitions/User"}}}
      "parameters"
      [{"name" "user-id"
        "in" "path"
        "required" true
        "type" "number"
        "format" "integer"}
       {"name" "UserParam"
        "in" "body"
        "required" true
        "schema" {"$ref" "#/definitions/UserParam"}}]}
     "delete"
     {"summary" "Delete a user"
      "operationId" "deleteUser"
      "responses" {"204" {"description" "User deleted empty response"}}
      "parameters" [{"name" "user-id"
                     "in" "path"
                     "required" true
                     "type" "number"
                     "format" "integer"}]}}}
   "definitions"
   {"User"
    {"type" "object"
     "properties"
     {"id" {"type" "integer"}
      "first-name" {"type" "string"}
      "last-name" {"type" "string"}
      "email" {"type" "string"}}}
    "UserParam"
    {"type" "object"
     "properties" {"first-name" {"type" "string"}
                   "last-name" {"type" "string"}
                   "email" {"type" "string"}}}}})

(def user
  {:id 1
   :first-name "Foo"
   :last-name "Bar"
   :email "foo@bar.com"
   :url "/user/1"})
