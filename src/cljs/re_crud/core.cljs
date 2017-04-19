(ns re-crud.core
  (:require [re-crud.events :as events]
            [re-crud.swagger :as swagger]
            [re-frame.core :as re-frame :refer [dispatch reg-event-db]]))

(defn init-service [db service-name service-config]
  (dispatch [:crud-swagger-get service-name service-config])
  (assoc-in db [:crud-service-configs service-name] service-config))

(defn init [service-configs]
  (events/register-events)
  (swagger/init)
  (doseq [[service-name service-config] service-configs]
    (dispatch [:init-service service-name service-config])))

(reg-event-db
 :init-service
 (fn [db [_ service-name service-config]]
   (init-service db service-name service-config)))
