(ns re-crud.http-client
  (:require [re-frame.core :refer [dispatch]]
            [ajax.core :refer [POST GET] :as ajax]
            [cljs.reader :as reader]
            [clojure.string :as s]))

(defn log [& args]
  (.log js/console :info args))

(defn make-url [url request-params]
  (reduce (fn [u [k v]] (s/replace u (str "{"(name k)"}") v))
          url
          request-params))

(defn parse-json-string [string]
  (js->clj (.parse js/JSON string)))

(defn parse-response [response]
  (let [parsed-response (reader/read-string response)]
    (-> parsed-response
        (update-in [:response :body] parse-json-string)
        (update-in [:response :body] clojure.walk/keywordize-keys)
        (update :response select-keys [:status :body :headers]))))

(defn response-handler [log-id request-body response operation-id on-success]
  (.done js/NProgress)
  (let [parsed-response (parse-response response)
        response-body (get-in parsed-response [:response :body])
        status (get-in parsed-response [:response :status])]
    (if (ajax/success? status)
      (when on-success
        (dispatch (conj on-success response-body)))
      (dispatch [:crud-http-fail operation-id status response-body]))))

(defn make-request [operation-id method url request-body & {:keys [on-success]}]
  (let [request {:method method :url url :body request-body}
        log-id (random-uuid)]
    (.start js/NProgress)
    (POST "/requester"
          {:params request
           :format :json
           :handler #(response-handler log-id request-body % operation-id  on-success)
           :error-handler #(prn %)})))
