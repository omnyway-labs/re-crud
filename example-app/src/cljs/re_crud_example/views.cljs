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
(ns re-crud-example.views
  (:require [re-frame.core :as re-frame :refer [reg-event-fx dispatch]]
            [re-crud-example.view.user :as user]
            [re-crud-example.view.todo :as todo]))

(defn tabs []
  [:div
   [:ul {:class "mui-tabs__bar mui-tabs__bar"}
    [:li {:class "mui--is-active"}
     [:a {:data-mui-toggle "tab", :data-mui-controls "users-pane"} "Users"]]
    [:li
     [:a {:data-mui-toggle "tab", :data-mui-controls "todos-pane"} "Todos"]]]])

(defn main-panel []
  (fn []
    [:div.mui-container
     [tabs]
     [user/pane]
     [todo/pane]]))
