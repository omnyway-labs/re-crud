(ns re-crud.components.sub-components
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [clojure.string :as s]
            [re-crud.util :as util]
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

(defn form [id view operation params on-submit]
  (fn [id view operation params on-submit]
    (let [{:keys [operation-id request-schema summary]} operation
          {:keys [classes hidden-fields]} view
          editable-schema (apply dissoc request-schema hidden-fields)]
      [:div.crud-form {:class (:form classes)}
       [:legend (util/display-name operation-id)]
       [:p.crud-form-operation-summary summary]
       (doall
        (for [param-path (util/paths editable-schema)]
          ^{:key (str id param-path)}
          [form-field id view param-path (get-in params param-path) operation-id]))
       [:button.crud-button {:on-click #(dispatch [on-submit])
                             :class (:button classes)} "Submit"]])))

(defn row [items last-item]
  (doall
   (filter some?
           (conj (vec items) last-item))))

(defn table [view resources-info]
  (fn [{:keys [classes filter-params fields actions] :as view} resources-info]
    (let [make-prop-renderer (fn [p]
                               (if (vector? p)
                                 p
                                 [p str]))
          ps (map make-prop-renderer fields)]
      [:div.crud-table-container
       [nc/table {:class (str "crud-table " (:table classes))
                  :filterable filter-params
                  :sortable true
                  :items-per-page 5
                  :page-button-limit 10
                  :no-data-text "No matching records found."}
        [nc/thead {:class "crud-thead"}
         (row (for [p ps]
                ^{:key (util/rand-key)}
                [nc/th {:class "crud-th" :column (name (first p))}
                 (s/capitalize (name (first p)))])
              (when actions
                ^{:key (util/rand-key)}
                [nc/th {:class "crud-th" :column "actions"} "Actions"]))]
        (doall
         (for [resource resources-info]
           ^{:key (util/rand-key)}
           [nc/tr {:class "crud-trow"}
            (row (for [p ps]
                   ^{:key (util/rand-key)}
                   [nc/td {:class "crud-td" :column (name (first p))}
                    ((second p) (get resource (first p)))])
                 (when actions
                   ^{:key (util/rand-key)}
                   [nc/td {:class "crud-td" :column "actions"}
                    [actions resource]]))]))]])))
