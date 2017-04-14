(ns re-crud.notifications
  (:require [re-frame.core :refer [reg-event-db dispatch reg-sub]]
            [clojure.string :as s]
            [cljs.pprint :refer [pprint]]))


(defn notification-html [operation-id status response]
  (str "<div class='crud-notification'>"
       "<strong>" operation-id " failed with status " status "</strong>"
       "<br>"
       "<pre>"
       (with-out-str (pprint response))
       "</pre>"
       "</div>"))

(defn register-events []
  (reg-event-db
   :crud-notify
   (fn [db [_ operation-id status response]]
     (.alert js/notie
             "warning"
             (notification-html operation-id status response))
     db)))
