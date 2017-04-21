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
