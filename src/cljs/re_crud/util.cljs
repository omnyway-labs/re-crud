(ns re-crud.util
  (:require [clojure.string :as s]
            [camel-snake-kebab.core :as csk]))

(defn rand-key []
  (str "key-" (random-uuid)))

(defn target-value [object]
  (-> object .-target .-value))

(defn display-name [x]
  (-> x
      name
      csk/->kebab-case
      (s/replace "-" " ")
      s/capitalize))

(defn paths
  "Return the paths of the leaves in the map"
  [m]
  (when m
    (letfn [(key-paths [prefix m]
              (if (map? m)
                (into {} (map (fn [[k v]] (key-paths (conj prefix k) v)) m))
                {prefix m}))]
      (keys (key-paths [] m)))))

(defn next-props-fn [f]
  (fn [this js-next-props]
    (f this (rest (js->clj js-next-props)))))

(defn resource-ids [resource]
  (let [resource-str (name resource)]
    {:list   (str "list-" resource-str)
     :create (str "create-" resource-str)
     :show   (str "show-" resource-str)
     :update (str "update-" resource-str)
     :delete (str "delete-" resource-str)}))


(defn map-vals [f kv-map]
  (->> kv-map
       (map (fn [[k v]] [k (f v)]))
       (into {})))

(defn ->vector [x]
  (if-not (vector? x)
    (vector x)
    x))
