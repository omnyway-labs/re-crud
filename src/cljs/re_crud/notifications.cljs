(ns re-crud.notifications
  (:require [re-frame.core :refer [reg-event-db dispatch reg-sub]]))

(reg-event-db
 :crud-notify
 (fn [db [_ operation-id status response]]
   (.log js/console (str "Failed:" operation-id " Status:" status " Response:" response))
   db))
