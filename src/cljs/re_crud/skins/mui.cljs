(ns re-crud.skins.mui
  (:refer-clojure :exclude [list update])
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [re-crud.components.views :as crud-comp]
            [re-crud.util :as util]))

(defn show [id form view events config]
  (fn []
    [:div.crud-show
     [:div.sub-panel-heading
      [:h2.title (:title view)]
      [:h2 [:i.material-icons.crud-refresh {:on-click #(dispatch [(:refresh events)])} "refresh"]]]
     [crud-comp/show id view]]))

(defn actions [{:keys [ resource-name actions]}]
  (fn [resource]
    [:ul.actions-list
     (doall (for [[action-name {:keys [dispatch-fn enabled?]
                                :or {enabled? (constantly true)} :as action}] actions]
              ^{:key (str resource-name action-name)}
              [:li.action-item [:button {:on-click (dispatch-fn resource)
                                         :class "mui-btn mui-btn--primary mui-btn--small"
                                         :disabled (not (enabled? resource))} action-name]]))]))

(defn list [id form view events config]
  (fn []
    (let [classes {:table "mui-table" :button "mui-btn mui-btn--primary"}]
      [:div.content-sub-panel.mui-panel
       [:div.sub-panel-heading
        [:h2.title (:title view)]
        [:h2 [:i.material-icons.crud-refresh {:on-click #(dispatch [(:refresh events)])} "refresh"]]]
       [crud-comp/list
        id (assoc view :classes classes)]
       (when (:new view)
         [:button.crud-button.mui-btn.mui-btn--primary
          {:on-click #(dispatch [(:new view)])}
          (str "Create New " (util/display-name (:resource-name view)))])])))

(defn create [id form view events config]
  (fn []
    (let [classes {:form-field "mui-textfield"
                   :button "mui-btn mui-btn--primary"}]
      [:div.content-sub-panel.mui-panel
       [crud-comp/create id form (:perform events) (assoc view :classes classes) config]])))

(defn update [id form view events config]
  (fn []
    (let [classes {:form-field "mui-textfield"
                   :button "mui-btn mui-btn--primary"}]
      [:div.content-sub-panel.mui-panel
       [crud-comp/update id form (:perform events) (assoc view :classes classes) config]])))

(defn delete [{:keys [resource service-name operation-id fetch-operation params on-success]}]
  (let [ids (util/resource-ids resource)]
    (fn [{:keys [resource service-name operation-id fetch-operation params on-success]}]
      [:div.crud-delete
       [:h3 (str "Delete " (util/display-name resource) "?")]
       [crud-comp/show {:id (:show ids)} service-name fetch-operation {:params params}]
       [:button.crud-button.mui-btn.mui-btn--danger {:on-click #(dispatch [:crud-http-request (:delete ids) operation-id params service-name on-success])} "Yes"]
       [:button.crud-button.mui-btn.mui-btn--primary {:on-click #(dispatch (on-success params))} "No"]])))
