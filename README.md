# re-crud

A re-frame library for developing CRUD applications.

If your service exposes a swagger API, even better! This can consume
swagger.json and perform HTTP calls to show, list, create and update resources.

![example-interface-screencast](https://media.giphy.com/media/zzYgeqXRUO9fG/giphy.gif "re-crud example interface")


## Installation
re-crud is (will be) on clojars: `[re-crud "0.1.0"]`

## Usage

A simple component to retrieve and display a resource would look like this.

```clojure
(def show
  (crud/show {:id :user.show
              :fetch {:operation-id "getUser"}
              :view {:title "User info"
                     :skin :mui
                     :resource-name "User"}
              :config {:service-name "my-service"}}))
```

- `:id` identifies the component to the library
- `:fetch` describes how to fetch the resource to show
- `:view` configures UI details

Here's a slightly more involved example:

```clojure
(def update
  (crud/update {:id      :user.update
                :fetch   {:operation-id "getUser"
                          :after (u/update-form-params-fx :user.update add-user-id)}
                :form    {:operation-id "updateUser"}
                :perform {:operation-id "updateUser"
                          :after (u/create-fx
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
{:id :user.show
 :events {:fetch       :crud-fetch-user.show
          :after-fetch :crud-after-fetch-user.show
	  :perform     :crud-perform-user.show
	  :refresh     :crud-refresh-user.show}
 :reagent-component re-crud.fn_some_generated-fn
 :state-path [:crud-components :user.show :resource-info]}
```

- `:reagent-component` is what you can add to your app's view
- `:events` are `id`s of the re-frame events you can dispatch
- `:events :refresh` needs to be implemented by the user
