# Changelog

## [0.1.13] - 2018-09-26
- Bugfix: Pass path as `nil` when no path is available

## [0.1.12] - 2018-09-26
- Make it possible to add skins
- Table enhancements
- Use fields-order to sort form fields
- Update example-app
- Fix error-handler in http-client. This fixes the argument for
  `crud-notify` for HTTP failures
- Always clear user input on crud-load-component
- Add perform-params-fn. Support a custom fn to decide whether to send
  the request and rewrite params
- Disable form submit buttons when a corresponding request is active
- Show an error if boolean value in a form is not selected

## [0.1.11] - 2018-07-12
- Dispatch optional `on-failure` event for http failures

## [0.1.10] - 2018-06-14
- Support requests with a list as body params

## [0.1.9] - 2018-04-17
- Clear previous user inputs when loading create-components

## [0.1.8] - 2018-02-14
- Rename `service-host` to `service-url` in service-config
- Send `x-re-crud-service` header in requests
- These changes are useful when the re-crud app needs to do cross-origin request through a proxy
- The service-url can be a relative url to the proxy
- The header value can be used to disambiguate services at the proxy

## [0.1.7] - 2018-02-08
- Dispatch `before-form` event on `crud-load-component`

## [0.1.6] - 2018-01-16
- Add "Select" option in select to to dismiss the impression that default value is selected, which is not.
- Support custom form field components

## [0.1.5] - 2018-01-15
- Fix to reflect param-schema edits in UI

## [0.1.4] - 2018-01-15
- Parse enum params in swagger schema to sets in service-config
- Use select-fields for enums and booleans

## [0.1.3] - 2017-12-07
- Coerce `number` type to floats

## [0.1.2] - 2017-10-11
- Remove a prn that printed urls for every http request

## [0.1.1] - 2017-10-11
- Fixed swagger parser to correctly parse fields named `type`
- Fixed :crud-notify with a default event that logs the args to it
