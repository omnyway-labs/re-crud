(ns re-crud-example.events
    (:require [re-frame.core :as re-frame]
              [re-crud-example.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))

(defn assoc-into-db [k]
  (fn [db [_ & args]]
    (let [ks (butlast args)
          value (last args)]
      (assoc-in db (cons k ks) value))))

(re-frame/reg-event-db
 :ui
 (assoc-into-db :ui))
