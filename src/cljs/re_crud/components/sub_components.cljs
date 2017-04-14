(ns re-crud.components.sub-components
  (:require [re-frame.core :as re-frame :refer [subscribe dispatch]]
            [clojure.string :as s]
            [re-crud.util :as util]
            [re-crud.native-components :as nc]))

(defn display-param-name [param-name]
  (s/join "-" (map util/display-name param-name)))

(defn set-param [id param-name param-value]
  (dispatch (vec (concat [:crud-components id :ui :user-input] param-name [param-value]))))

(defn form-field [id classes param-name param-value operation-id]
  (let [ui-param-value (subscribe (vec (concat [:crud-components id :ui :user-input] param-name)))]
    (fn [id classes param-name param-value operation-id]
      [:div.crud-form-field {:class (:form-field classes)}
       [:label.crud-form-label
        {:for param-name :class "control-label"}
        (display-param-name param-name)]
       [:input.crud-form-input
        {:id param-name
         :class (:input classes)
         :value @ui-param-value
         :on-change #(set-param id param-name (util/target-value %))}]])))

(defn form [id view operation params on-submit]
  (fn [id view operation params on-submit]
    (let [{:keys [operation-id request-schema summary]} operation
          {:keys [classes hidden-fields]} view
          editable-schema (apply dissoc request-schema hidden-fields)]
      [:div.crud-form {:class (:form classes)}
       [:legend (util/display-name operation-id)]
       [:p.crud-form-operation-summary summary]
       (doall
        (for [param-name (util/paths editable-schema)]
          ^{:key (str id param-name)}
          [form-field id classes param-name (get-in params param-name) operation-id]))
       [:button.crud-button {:on-click #(dispatch [on-submit])
                             :class (:button classes)} "Submit"]])))

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
         (doall
          (for [p ps]
            ^{:key (util/rand-key)}
            [nc/th {:class "crud-th" :column (name (first p))}
             (s/capitalize (name (first p)))]))
         (when actions
           [nc/th {:class "crud-th" :column "actions"} "Actions"])]
        (doall
         (for [resource resources-info]
           ^{:key (util/rand-key)}
           [nc/tr {:class "crud-trow"}
            (doall
             (for [p ps]
               ^{:key (util/rand-key)}
               [nc/td {:class "crud-td" :column (name (first p))}
                ((second p) (get resource (first p)))]))
            (when actions
              [nc/td {:class "crud-td" :column "actions"}
               [actions resource]])]))]])))
