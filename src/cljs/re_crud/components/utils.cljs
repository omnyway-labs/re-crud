(ns re-crud.components.utils
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]))

(defn create-fx [f]
  (let [event-name (keyword (str (random-uuid)))]
    (reg-event-fx event-name (fn [_ [_ & args]]
                               (apply f args)
                               {}))
    event-name))

(defn user-input-path [id]
  [:crud-components id :ui :user-input])

(defn state-path [id]
  [:crud-components id :resource-info])

(defn resource-path [id & ks]
  (concat (state-path id) ks))

(defn update-form-params-fx [id f]
  (let [event-name (keyword (str (random-uuid)))]
    (reg-event-db event-name (fn [db _]
                               (update-in db (user-input-path id) f)))
    event-name))
