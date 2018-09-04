(ns re-crud.components.views
  (:refer-clojure :exclude [list update])
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [re-crud.util :as util]
            [re-crud.components.sub-components :as sub]))

(defn spinner []
  [:p "Loading..."])

(defn is-active-filter [x]
  (-> x :is-active boolean))

(defn list [id view]
  (let [resources-info (subscribe [:crud-components id :resource-info])]
    (fn [id view]
      (let [filter-active? (:filter-active? view)
            filter-fn (if filter-active?
                        (or (:filter-fn view) is-active-filter)
                        (constantly true))
            filtered-resources (filter filter-fn @resources-info)]
        (if @resources-info
          [sub/table view filtered-resources]
          [(or (:spinner view) spinner)])))))

(defn show [id view]
  (let [resource-info (subscribe [:crud-components id :resource-info])]
    (fn []
      [:div.crud-show
       (if-not (some? @resource-info)
         [(or (:spinner view) spinner)]
         [:ul.crud-prop-list
          (doall
           (for [[prop-name prop-value] @resource-info]
             ^{:key (util/rand-key)}
             [:li.crud-prop-item
              [:span.crud-prop-name prop-name]
              [:span.space " : "]
              [:span.crud-prop-value (str prop-value)]]))])])))

(defn create [id form perform-event view config]
  (let [operation (subscribe [:crud-service-configs (:service-name config) :operations (:operation-id form)])
        form-params (subscribe [:crud-components id :form-params])]
    (fn []
      [:div.crud-create
       [sub/form id view @operation @form-params perform-event]])))

(defn update [id form perform-event view config]
  (let [operation (subscribe [:crud-service-configs (:service-name config) :operations (:operation-id form)])
        form-params (subscribe [:crud-components id :form-params])]
    (fn []
      [:div.crud-update
       [sub/form id view @operation @form-params perform-event]])))
