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
(ns re-crud-example.view.user
  (:refer-clojure :exclude [list update])
  (:require [re-frame.core :as re-frame :refer [reg-event-fx dispatch subscribe]]
            [re-crud.components :as crud]
            [re-crud.components.utils :as u]
            [re-crud.skins.mui :as mui]
            [re-crud-example.config :as config]))

(declare list)

(defn user-params [user]
  {:user-id (:id user)})

(def show
  (crud/show {:id :user.show
              :fetch {:operation-id "getUser"}
              :config config/service
              :view {:title "User Info"
                     :skin :mui
                     :resource-name "User"}}))

(reg-event-fx
 (:refresh (:events show))
 (fn [{:keys [db]} _]
   (let [user-id (get-in db (u/resource-path (:id show) :id))]
     {:dispatch [(:fetch (:events show)) {:user-id user-id}]})))


(def after-create-update-event
  (u/create-fx
   (fn [user]
     (dispatch [:crud-load-component show {:fetch (user-params user)}])
     (dispatch [:ui :user-action :show])
     (dispatch [(:refresh (:events list))]))))

(def update
  (crud/update {:id      :user.update
                :fetch   {:operation-id "getUser"}
                :form    {:operation-id "updateUser"}
                :perform {:operation-id "updateUser"
                          :after after-create-update-event}
                :view    {:skin :mui
                          :resource-name "User"}
                :config config/service}))

(def create
  (crud/create {:id :user.create
                :form {:operation-id "createUser"}
                :perform {:operation-id "createUser"
                          :after after-create-update-event}
                :view {:skin :mui}
                :config config/service}))


(def delete
  (crud/delete {:id :user.delete
                :perform {:operation-id "deleteUser"
                          :after (u/create-fx (fn [_]
                                                (dispatch [:ui :user-action :create])
                                                (dispatch [(:refresh (:events list))])))}
                :config config/service}))

(def list-actions
  (mui/actions {:resource-name :merchant
                :actions {"View" {:dispatch-fn (fn [user]
                                                 (fn [& _]
                                                   (dispatch [:ui :user-action :show])
                                                   (dispatch [:crud-load-component show {:fetch (user-params user)}])))}
                          "Edit" {:dispatch-fn (fn [user]
                                                 (fn [& _]
                                                   (dispatch [:ui :user-action :update])
                                                   (dispatch [:crud-load-component update {:fetch (user-params user)
                                                                                           :form (user-params user)}])))}
                          "Delete" {:dispatch-fn (fn [user]
                                                   #(dispatch [(:perform (:events delete)) (user-params user)]))}}}))


(def list
  (crud/list {:id :user.list
              :fetch {:operation-id "listUsers"}
              :view {:title "Users"
                     :skin :mui
                     :new (u/create-fx #(dispatch [:ui :user-action :create]))
                     :fields [:id :first-name :last-name :email]
                     :filter-params [:id :first-name :last-name :email]
                     :actions list-actions
                     :resource-name "User"}
              :config config/service}))

(reg-event-fx
 (:refresh (:events list))
 (fn [cofx _]
   {:dispatch [(:fetch (:events list)) nil]}))

(defn pane []
  (let [user-action (subscribe [:ui :user-action])]
    (fn []
      (let [user-action (or @user-action :create)
            user-components {:show (:reagent-component show)
                             :create (:reagent-component create)
                             :update (:reagent-component update)}]
        [:div.mui-tabs__pane.mui--is-active {:id "users-pane"}
         [:div.pane
          [(:reagent-component list)]
          [(get user-components user-action)]]]))))
