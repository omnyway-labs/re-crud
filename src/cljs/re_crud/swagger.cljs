(ns re-crud.swagger
  (:require [ajax.core :refer [POST] :as ajax]
            [clojure.string :as s]
            [clojure.walk :as cw]
            [re-crud.http-client :as client]
            [re-frame.core :refer [reg-event-db dispatch]]))

(defn minimize-schema
  [{:keys [type] :as schema}]
  (cw/prewalk
   (fn [x]
     (if (and (map? x) (:type x))
       (:type x)
       x))
   schema))

(defn schema-ref-name-type [schema]
  (let [[list-ref obj-ref] [(get-in schema [:items :$ref]) (get-in schema [:$ref])]
        ref-name (some-> (or list-ref obj-ref)
                         name
                         (s/split #"/")
                         last
                         keyword)]
    {:klass (if list-ref :array :object)
     :ref-name ref-name}))

(defn expand-schema [schema definitions]
  (cw/prewalk
   (fn [x]
     (let [{:keys [klass ref-name]} (schema-ref-name-type x)]
       (if-not (and (map? x) ref-name)
         x
         (let [expanded-schema (expand-schema (get-in definitions [ref-name :properties])
                                              definitions)]
           (case klass
             :object expanded-schema
             :array [expanded-schema])))))
   schema))

(defn body-params [parameters]
  (-> (filter #(= "body" (:in %)) parameters)
      first
      (get :schema)))

(defn post-request-schema [parameters definitions]
  (let [schema (body-params parameters)]
    (-> (expand-schema schema definitions)
        cw/keywordize-keys
        minimize-schema)))

(defn url-request-schema [parameters definitions]
  (->> parameters
       (map (fn [x] [(keyword (:name x)) (:type x)]))
       (into {})))

(defn request-schema [method parameters definitions]
  (case method
    :get (url-request-schema parameters definitions)
    :post (post-request-schema parameters definitions)
    :put (post-request-schema parameters definitions)
    :patch (post-request-schema parameters definitions)
    :delete (url-request-schema parameters definitions)))

(defn api-spec [path method spec definitions]
  (let [{:keys [tags summary parameters operationId]} spec]
    {:operation-id operationId
     :summary summary
     :method method
     :url (subs (str path) 1)
     :request-schema (request-schema method parameters definitions)}))

(defn parse [{:keys [definitions paths]}]
  (->> (for [[path actions] paths
             [method spec] actions]
         (api-spec path method spec definitions))
       (group-by :operation-id)
       (map (fn [[oid [action]]] [oid action]))
       (into {})))

(defn get-swagger-spec [service-name service-config]
  (client/make-request
   :crud-swagger-parse
   :get
   (:swagger-url service-config)
   nil
   :on-success [:crud-swagger-parse service-name]))

(defn init []
  (reg-event-db
   :crud-swagger-get
   (fn [db [_ service-name service-config]]
     (get-swagger-spec service-name service-config)
     db))

  (reg-event-db
   :crud-swagger-parse
   (fn [db [_ service-name response]]
     (let [{:keys [dispatch-on-ready service-config-path]}
           (get-in db [:crud-service-configs service-name])]
       (dispatch dispatch-on-ready)
       (assoc-in db [:crud-service-configs service-name :operations] (parse response))))))
