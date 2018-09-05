(ns re-crud.events
  (:require [re-frame.core :refer [reg-event-db dispatch reg-sub reg-event-fx]]
            [re-crud.notifications :as notifications]
            [re-crud.coerce :as coerce]
            [re-crud.http-client :as client]))

(defn assoc-into-db [k]
  (fn [db [_ & args]]
    (let [ks (butlast args)
          value (last args)]
      (assoc-in db (cons k ks) value))))

(defn get-in-db [k]
  (fn [db [_ & ks]]
    (get-in db (cons k ks))))

(defn crud-load-component [{:keys [db]} [_
                                         {:keys [id events type] :as component}
                                         {:keys [fetch form] :as params}]]
  (let [dispatch-events (concat []
                                (when (:fetch events) [[(:fetch events) fetch]])
                                (when (:form-event events) [[(:form-event events)]]))]
    (merge
     (when-not (empty? dispatch-events) {:dispatch-n dispatch-events})
     {:db (assoc-in db [:crud-components id :ui :user-input] form)})))

(defn register-events []
  (reg-event-fx :crud-load-component crud-load-component)
  (reg-sub :crud-service-configs (get-in-db :crud-service-configs))
  (reg-sub :crud-components (get-in-db :crud-components))

  (reg-event-db :crud-components (assoc-into-db :crud-components))

  (reg-event-db
   :crud-http-request
   (fn [db [_ id operation-id params service-name on-success on-failure url-params]]
     (let [service-config (get-in db [:crud-service-configs service-name])
           service-url (:service-url service-config)
           {:keys [url method request-schema categories resource-type] :as operation}
           (get-in service-config [:operations operation-id])]
       (client/make-request operation-id
                            method
                            (client/make-url service-url url (or url-params
                                                                 (if (map? params)
                                                                   params
                                                                   {})))
                            (coerce/request params request-schema)
                            :on-success [:crud-received-response id on-success]
                            :on-failure on-failure
                            :service-name service-name)
       db)))

  (reg-event-db
   :crud-received-response
   (fn [db [_ id on-success response]]
     (when on-success (dispatch [on-success response]))
     db))

  (reg-event-db
   :crud-http-fail
   (fn [db [_ operation-id status response]]
     (dispatch [:crud-notify operation-id status response])
     db)))
