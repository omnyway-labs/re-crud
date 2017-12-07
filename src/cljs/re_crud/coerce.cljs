(ns re-crud.coerce
  (:require [re-crud.util :as util]))

(defn empty-value [t]
  (case t
    "string" ""
    "integer" nil
    "array" []
    "boolean" false))

(declare request)

(defn coerce-param [param schema-type]
  (cond (and (map? param)
             (sequential? schema-type))
        (mapv #(request % (first schema-type)) (vals param))

        (= "integer" schema-type)
        (js/parseInt param)

        (= "number" schema-type)
        (js/parseFloat param)

        (= "boolean" schema-type)
        (case (str param)
          "true" true
          "false" false)

        :else
        param))

(defn ->map [path-values]
  (reduce (fn [acc [path v]]
            (assoc-in acc path v))
          {}
          path-values))

(defn request [param schema]
  (->> (for [path (util/paths schema)
             :let [param-value (get-in param path)
                   param-schema (get-in schema path)]
             :when (not (contains? #{nil ""} param-value))]
         [path (coerce-param param-value param-schema)])
       (into {})
       ->map))
