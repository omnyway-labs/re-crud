(ns re-crud.mock-http-server
  (:require [org.httpkit.server :as http-server]
            [bidi.ring :as bidi]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.util.response :as response]
            [re-crud.test-data :as data]))

(def server-instance (atom nil))

(defn swagger [_]
  (response/response data/swagger))

(defn get-user [_]
  (response/response data/user))

(defn update-user [{:keys [body] :as request}]
  (response/response (merge data/user (select-keys body [:first-name :last-name :email]))))

(def handler
  (bidi/make-handler
   ["/" [["swagger.json" swagger]
         ["users" [[true {:get get-user
                          :patch update-user}]]]]]))

(def server
  (-> handler
      (wrap-json-body {:keywords? true :bigdecimals? true})
      (wrap-json-response)))

(defn start-server []
  (when-not (some? @server-instance)
    (reset! server-instance (http-server/run-server #'server {:port 8000}))
    (println "Web server started at port" 8000)))

(defn stop-server []
  (when-not (nil? @server-instance)
     (@server-instance :timeout 100)
     (reset! server-instance nil)))

(defn -main [& args]
  (start-server))
