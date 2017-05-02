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
(ns re-crud-example.core
  (:require [reagent.core :as reagent]
            [re-frame.core :as re-frame :refer [reg-event-fx]]
            [re-crud.core :as re-crud]
            [re-crud-example.events]
            [re-crud-example.subs]
            [re-crud-example.views :as views]
            [re-crud-example.config :as config]
            [re-crud-example.view.user :as user]
            [re-crud-example.view.todo :as todo]))


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn on-ready [& _]
  (mount-root)
  {:dispatch-n [[:crud-load-component user/list]
                [:crud-load-component todo/user-selector-list]]})

(defn ^:export init []
  (reg-event-fx :on-ready on-ready)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (re-crud/init {"re-crud-example" {:service-host "https://re-crud-example.herokuapp.com"
                                    :swagger-url "https://re-crud-example.herokuapp.com/api/swagger.json"
                                    :dispatch-on-ready [:on-ready]}}))
