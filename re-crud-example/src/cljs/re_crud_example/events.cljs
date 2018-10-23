;;
;; re-crud - A crud library for re-frame apps
;;
;; Copyright 2017 Omnyway Inc.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;   http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
;;
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
