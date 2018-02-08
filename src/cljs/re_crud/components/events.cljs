(ns re-crud.components.events
  (:require [re-frame.core :refer [dispatch reg-event-fx reg-event-db]]
            [goog.string :as gstring]
            [goog.string.format]))


(defn event-name [event-type id]
  (keyword (gstring/format "crud-%s-%s" event-type (name id))))

(def after-fetch-event-name (partial event-name "after-fetch"))
(def fetch-event-name (partial event-name "fetch"))
(def perform-event-name (partial event-name "perform"))
(def refresh-event-name (partial event-name "refresh"))

(defn ignore-lists [x]
  (if (map? x)
    x
    {}))

(defn create-after-fetch-event [id after]
  (reg-event-fx
   (after-fetch-event-name id)
   (fn [{:keys [db]} [_ response]]
     (let [dispatch-data (when after {:dispatch [after response]})]
       (merge dispatch-data
              {:db (-> db
                       (assoc-in [:crud-components id :resource-info] response)
                       (update-in [:crud-components id :ui :user-input] merge (ignore-lists response)))})))))

(defn create-fetch-event [id {:keys [operation-id after]
                              :as fetch-event-params} config]
  (create-after-fetch-event id after)
  (reg-event-fx (fetch-event-name id)
                (fn [{:keys [db]} [_ fetch-params]]
                  {:db (-> db (assoc-in [:crud-components id :resource-info] nil))
                   :dispatch [:crud-http-request id operation-id fetch-params (:service-name config) (after-fetch-event-name id)]}))
  (fetch-event-name id))

(defn create-perform-event [id {:keys [operation-id after]
                                :as perform-event-params} config]
  (reg-event-fx (perform-event-name id)
                (fn [{:keys [db]} [_ params]]
                  (let [user-input (get-in db [:crud-components id :ui :user-input])
                        req-params (merge params user-input)]
                    {:dispatch [:crud-http-request id operation-id req-params (:service-name config) after]})))
  (perform-event-name id))

(defn events [{:keys [id fetch form perform config] :as params}]
  {:fetch       (when fetch (create-fetch-event id fetch config))
   :after-fetch (when fetch (after-fetch-event-name id))
   :form-event  (:event form)
   :perform     (when perform (create-perform-event id perform config))
   :refresh     (refresh-event-name id)})
