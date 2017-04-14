(ns re-crud.core
  (:require [re-crud.events :as events]
            [re-crud.swagger :as swagger]
            [re-frame.core :as re-frame :refer [dispatch reg-event-db]]))

(defn init-service [db service-name service-config]
  (let [service-config-on-file (get-in db [:config service-name])
        merged-config (merge service-config service-config-on-file)]
    (dispatch [:crud-swagger-get service-name])
    (assoc-in db [:crud-service-configs service-name] merged-config)))

(defn init [service-configs]
  (events/register-events)
  (swagger/init)
  (doseq [[service-name service-config] service-configs]
    (dispatch [:init-service service-name service-config])))

(reg-event-db
 :init-service
 (fn [db [_ service-name service-config]]
   (init-service db service-name service-config)))
