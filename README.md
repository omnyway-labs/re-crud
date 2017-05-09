# re-crud

[![Clojars Project](https://img.shields.io/clojars/v/org.omnyway/re-crud.svg)](https://clojars.org/org.omnyway/re-crud)
[![CircleCI](https://circleci.com/gh/omnypay/re-crud.svg?style=svg)](https://circleci.com/gh/omnypay/re-crud)

A re-frame library for developing CRUD applications.

If your service exposes a swagger API, even better! This can consume
swagger.json and perform HTTP calls to show, list, create and update resources.

![example-interface-screencast](https://media.giphy.com/media/zzYgeqXRUO9fG/giphy.gif "re-crud example interface")


## Installation
re-crud is (will be) on clojars: `[re-crud "0.1.0"]`

## Example app
- There is a comprehensive example-app in this repo: [example-app](example-app/)
- You can find a running instance of this app here: [demo](https://omnypay.github.io/re-crud/)
- This uses a fairly minimal CRUD web service written in Rails, deployed here: [web app](https://re-crud-example.herokuapp.com/swagger/index.html)

## Usage

Initialize `re-crud` soon after you initialize your re-frame app db.
`re-crud.core/init` takes a map of `service-name`:`config` pairs.

```clojure
(require '[re-crud.core :as crud])
(crud/init
 {"service-name" {:service-host "https://my-service.host"
                  :swagger-url "https://my-service.host/swagger.json"
                  :dispatch-on-ready [:on-ready]}})
```

The `dispatch-on-ready` event from service config is dispatched once `re-crud` has parsed the swagger spec for that service. Initialize your views after this event has been triggered.

Add [crud.css](css/crud.css) in your app for styling. `re-crud` comes with a skin that appies [MUI CSS](https://www.muicss.com/).

Use component-generators from `re-crud.components` to generate the view compnent and associated events.

A simple component to retrieve and display a resource would look like this.

```clojure
(def show
  (re-crud.components/show {:id     :user.show
                            :fetch  {:operation-id "getUser"}
                            :view   {:title "User info"
                                     :skin :mui
                                     :resource-name "User"}
                            :config {:service-name "my-service"}}))
```

- `:id` identifies the component to the library
- `:fetch` describes how to fetch the resource to show
- `:view` configures UI details

TODO: add documentation around `:load-component`

Here's a slightly more involved example:

```clojure
(def update
  (re-crud.components/update {:id      :user.update
                              :fetch   {:operation-id "getUser"
                                        :after (re-crud.components.utils/update-form-params-fx :user.update add-user-id)}
                              :form    {:operation-id "updateUser"}
                              :perform {:operation-id "updateUser"
                                        :after (re-crud.components.utils/create-fx
                                                #(dispatch [:goto-route :show-user {:user-id (:id %)}]))}
                              :view    {:skin :mui
                                        :resource-name "User"}
                              :config  {:service-name "my-service"}}))
```

- `:form` will render user input fields based on the `operation-id`'s request-schema
- `:perform` describes how to send the form fields to (say) create/update resources
- `:after` is an event that is triggered after `fetch` or `perform`.

Here's an example of what you''d get on creating a component
```clojure
{:id                :user.show
 :events            {:fetch       :crud-fetch-user.show
                     :after-fetch :crud-after-fetch-user.show
                     :perform     :crud-perform-user.show
                     :refresh     :crud-refresh-user.show}
 :reagent-component re-crud.fn_some_generated-fn
 :state-path        [:crud-components :user.show :resource-info]}
```

- `:reagent-component` is what you can add to your app's view
- `:events` are `id`s of the re-frame events you can dispatch
- `:events :refresh` needs to be implemented by the user

## Running tests

`make test` (requires phantomjs)

## License - Apache 2.0

Copyright 2017 Omnyway Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
