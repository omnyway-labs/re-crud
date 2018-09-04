(ns re-crud.components.sub-components
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [clojure.string :as s]
            [re-crud.util :as util]
            [re-crud.components.utils :as cutils]
            [re-crud.native-components :as nc]))

(defn display-param-name [param-path]
  (s/join "-" (map util/display-name param-path)))

(defn set-param [id param-path param-value]
  (dispatch (vec (concat [:crud-components id :ui :user-input] param-path [param-value]))))

(defn select-field [id classes param-path value options]
  (let [on-change #(set-param id param-path (util/target-value %))]
    [:div {:class (:select-field classes)}
     [:select {:id param-path
               :class "crud-form-input"
               :on-change on-change
               :value value}
      (doall
       (for [option (cons "Select" options)]
         ^{:key (util/rand-key)}[:option option]))]]))

(defn bool-str [ui-value param-value]
  (or (when (some? ui-value)
        (str ui-value))

      (if (some? param-value)
        (str param-value)
        "false")))

(defn vectorize-keys [m]
  (->> m
       (map (fn [[k v]] [(util/->vector k) v]))
       (into {})))

(defn form-field [id view param-path param-schema param-value operation-id]
  (let [ui-param-value (subscribe (vec (concat [:crud-components id :ui :user-input] param-path)))
        classes (:classes view)
        fields (vectorize-keys (:fields view))]
    (fn [id view param-path param-schema param-value operation-id]
      [:div.crud-form-field {:class (:form-field classes)}
       [:label.crud-form-label
        {:for param-path :class "control-label"}
        (display-param-name param-path)]
       (cond (some? (get fields param-path))
             [(get fields param-path) id classes param-path @ui-param-value param-schema param-value]

             (set? param-schema)
             [select-field id classes param-path
              (or @ui-param-value param-value "Select")
              param-schema]

             (= "boolean" param-schema)
             [select-field id classes param-path
              (bool-str @ui-param-value param-value)
              ["true" "false"]]

             :else
             [:input.crud-form-input
              {:id param-path
               :class (:input classes)
               :value @ui-param-value
               :on-change #(set-param id param-path (util/target-value %))}])])))

(defn data-attrs [resource data-fn]
  (->> (for [[attr-name attr-value] (data-fn resource)]
         [(keyword (str "data-" (name attr-name))) attr-value])
       (into {})))

(defn default-prop-renderer [m]
  {:field (:field m)
   :column-name (name (:field m))
   :display-fn str
   :header (s/capitalize (name (:field m)))
   :data-fn identity})

(defn make-prop-renderer [p]
  (cond
    (map? p)
    (merge (default-prop-renderer p) p)

    (vector? p)
    {:field (first p)
     :column-name (name (first p))
     :display-fn (second p)
     :header (s/capitalize (name (first p)))
     :data-fn identity}

    (or (keyword? p)
        (string? p))
    {:field p
     :column-name (name p)
     :header (s/capitalize (name p))
     :data-fn identity
     :display-fn str}))

(defn form [id view operation params on-submit]
  (fn [id view operation params on-submit]
    (let [{:keys [operation-id request-schema summary]} operation
          {:keys [classes hidden-fields fields-order]} view
          editable-schema (apply dissoc request-schema hidden-fields)]
      [:div.crud-form {:class (:form classes)}
       [:legend (util/display-name operation-id)]
       [:p.crud-form-operation-summary summary]
       (doall
        (for [param-path (cutils/paths (cutils/sort-fields fields-order editable-schema))]
          ^{:key (str id param-path)}
          [form-field id view param-path (get-in editable-schema param-path) (get-in params param-path) operation-id]))
       [:button.crud-button {:on-click #(dispatch [on-submit])
                             :class (:button classes)} "Submit"]])))

(defn row [items last-item]
  (doall
   (filter some?
           (conj (vec items) last-item))))

(defn table [view resources-info]
  (let [filter-text (if (:filter-text-sub view)
                    (subscribe (:filter-text-sub view))
                    (atom nil))]
    (fn [{:keys [classes filter-params fields actions filter-text-sub] :as view} resources-info]
      (let [ps (map make-prop-renderer fields)]
        [:div.crud-table-container
         [nc/table (merge {:class (str "crud-table " (:table classes))
                           :filterable filter-params
                           :sortable true
                           :hide-filter-input (:hide-filter-input view)
                           :items-per-page (or (:items-per-page view) 20)
                           :page-button-limit 10
                           :no-data-text "No matching records found."
                           :on-page-change (or (:on-page-change view) #())}
                          (when @filter-text {:filter-by @filter-text}))
          [nc/thead {:class "crud-thead"}
           (row (for [p ps]
                  ^{:key (util/rand-key)}
                  [nc/th {:class "crud-th" :column (:column-name p)}
                   (:header p)])
                (when actions
                  ^{:key (util/rand-key)}
                  [nc/th {:class "crud-th" :column "actions"} "Actions"]))]
          (doall
           (for [resource resources-info]
             ^{:key (util/rand-key)}
             [nc/tr {:class "crud-trow"}
              (row (for [p ps]
                     ^{:key (util/rand-key)}
                     [nc/td (merge {:class (str "crud-td " "td-"(:column-name p)) :column (:column-name p)}
                                   (data-attrs resource (:data-fn p)))
                      ((:display-fn p) (get resource (:field p)))])
                   (when actions
                     ^{:key (util/rand-key)}
                     [nc/td {:class "crud-td" :column "actions"}
                      [actions resource]]))]))]]))))
