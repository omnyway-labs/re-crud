(ns re-crud-example.view.todo
  (:refer-clojure :exclude [list update])
  (:require [re-frame.core :as re-frame :refer [reg-event-fx dispatch subscribe]]
            [re-crud.components :as crud]
            [re-crud.components.utils :as u]
            [re-crud.skins.mui :as mui]
            [re-crud-example.view.user :as user]
            [re-crud-example.config :as config]))

(declare list)

(defn todo-params [todo]
  {:todo-id (:id todo)
   :user-id (:user_id todo)})

(def user-list-actions
  (mui/actions {:resource-name :merchant
                :actions {"Todos" {:dispatch-fn (fn [user]
                                                   (fn [& _]
                                                     (dispatch [:ui :todo-user (:id user)])
                                                     (dispatch [:crud-load-component list {:fetch {:user-id (:id user)}}])))}}}))

(def user-selector-list
  (crud/list {:id :todo.user.list
              :fetch {:operation-id "listUsers"}
              :view {:title "Users"
                     :skin :mui
                     :fields [:first_name :last_name]
                     :actions user-list-actions
                     :resource-name "User"}
              :config config/service}))

(reg-event-fx
 (:refresh (:events user-selector-list))
 (fn [cofx _]
   {:dispatch [(:fetch (:events user-selector-list)) nil]}))

(def show
  (crud/show {:id :todo.show
              :fetch {:operation-id "getTodo"}
              :config config/service
              :view {:title "Todo Info"
                     :skin :mui
                     :resource-name "Todo"}}))

(def after-create-update-event
  (u/create-fx
   (fn [todo]
     (dispatch [:crud-load-component show {:fetch (todo-params todo)}])
     (dispatch [:ui :todo-action :show])
     (dispatch [(:refresh (:events list))]))))

(def create
  (crud/create {:id :todo.create
                :form {:operation-id "createTodo"}
                :perform {:operation-id "createTodo"
                          :after after-create-update-event}
                :view {:skin :mui}
                :config config/service}))

(def update
  (crud/update {:id      :todo.update
                :fetch   {:operation-id "getTodo"}
                :form    {:operation-id "updateTodo"}
                :perform {:operation-id "updateTodo"
                          :after after-create-update-event}
                :view    {:skin :mui
                          :resource-name "Todo"}
                :config config/service}))

(reg-event-fx
 :new-todo
 (fn [{:keys [db]} _]
   (let [todo-user (get-in db [:ui :todo-user])]
     {:dispatch-n [[:ui :todo-action :create]
                   [:crud-load-component create {:form {:user-id todo-user}}]]})))

(def delete
  (crud/delete {:id :todo.delete
                :perform {:operation-id "deleteTodo"
                          :after (u/create-fx (fn [_]
                                                (dispatch [:new-todo])
                                                (dispatch [(:refresh (:events list))])))}
                :config config/service}))


(def list-actions
  (mui/actions {:resource-name :merchant
                :actions {"View" {:dispatch-fn (fn [todo]
                                                 (fn [& _]
                                                   (dispatch [:ui :todo-action :show])
                                                   (dispatch [:crud-load-component show {:fetch (todo-params todo)}])))}
                          "Edit" {:dispatch-fn (fn [todo]
                                                 (fn [& _]
                                                   (dispatch [:ui :todo-action :update])
                                                   (dispatch [:crud-load-component update {:fetch (todo-params todo)
                                                                                           :form (todo-params todo)}])))}
                          "Delete" {:dispatch-fn (fn [todo]
                                                   #(dispatch [(:perform (:events delete)) (todo-params todo)]))}}}))

(def list
  (crud/list {:id :todo.list
              :fetch {:operation-id "listTodos"}
              :view {:new :new-todo
                     :title "Todos"
                     :skin :mui
                     :fields [:id :title :notes :tags]
                     :filter-params [:id :title :notes :tags]
                     :actions list-actions
                     :resource-name "Todo"}
              :config {:service-name "re-crud-example"}}))


(reg-event-fx
 (:refresh (:events list))
 (fn [{:keys [db]} _]
   (let [todo-user (get-in db [:ui :todo-user])]
     {:dispatch [(:fetch (:events list)) {:user-id todo-user}]})))


(defn pane []
  (let [todo-action (subscribe [:ui :todo-action])
        todo-user (subscribe [:ui :todo-user])]
    (fn []
      (let [todo-action (or @todo-action :nil)
            todo-components {:show (:reagent-component show)
                             :create (:reagent-component create)
                             :update (:reagent-component update)}
            active-component (get todo-components todo-action)]
        [:div.mui-tabs__pane {:id "todos-pane"}
         [:div.pane-vertical
          [(:reagent-component user-selector-list)]
          (when @todo-user
            [:div.pane
             [(:reagent-component list)]
             (when active-component
               [active-component])])]]))))
