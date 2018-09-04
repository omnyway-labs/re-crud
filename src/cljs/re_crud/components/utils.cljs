(ns re-crud.components.utils
  (:require [re-frame.core :refer [reg-event-fx reg-event-db]]))

(defn create-fx [f]
  (let [event-name (keyword (str (random-uuid)))]
    (reg-event-fx event-name (fn [_ [_ & args]]
                               (apply f args)
                               {}))
    event-name))

(defn user-input-path [id]
  [:crud-components id :ui :user-input])

(defn state-path [id]
  [:crud-components id :resource-info])

(defn resource-path [id & ks]
  (concat (state-path id) ks))

(defn update-form-params-fx [id f]
  (let [event-name (keyword (str (random-uuid)))]
    (reg-event-db event-name (fn [db _]
                               (update-in db (user-input-path id) f)))
    event-name))


(defn paths
  "Return the paths of the leaves in the map"
  [m]
  (when m
    (letfn [(key-paths [prefix m]
              (if (map? m)
                (apply concat (mapv (fn [[k v]]
                                     (key-paths (conj prefix k) v)) m))
                [prefix]))]
      (key-paths [] m))))

(defn get-priority [m ks]
  (let [p (get-in m ks)]
    (cond
      (integer? p)
      p

      (map? p)
      ;; get the minimum value of priorities in any sub-tree
      (apply min
             (map (fn [k]
                    (get-priority m (conj ks k)))
                  (keys p)))
      (nil? p)
      (.-MAX_SAFE_INTEGER js/Number))))

(defn priority-map [priorities]
  (reduce (fn [m [ks v]]
            (assoc-in m ks v))
          {}
          priorities))

(defn key-path-comparator [path key-paths]
  (let [priorities (into {} (map vector key-paths (range)))
        pm (priority-map priorities)]
    (fn [x y]
      (compare [(get-priority pm (conj path x)) x]
               [(get-priority pm (conj path y)) y]))))

(defn sort-fields
  ([key-paths m]
   (if (empty? key-paths)
     m
     (sort-fields key-paths [] m)))
  ([key-paths path m]
   (cond (map? m)
         (into (sorted-map-by (key-path-comparator path key-paths))
               (map (fn [[k v]]
                      [k (sort-fields key-paths (conj path k) v)])
                    m))

         (coll? m)
         (map #(sort-fields key-paths path %) m)

         :else
         m)))
