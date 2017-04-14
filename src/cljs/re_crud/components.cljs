(ns re-crud.components
  (:refer-clojure :exclude [list update])
  (:require [re-crud.skins.mui :as mui]
            [re-crud.components.events :as e]
            [re-crud.components.utils :as u]))

(def skins
  {:mui {:show mui/show
         :list mui/list
         :create mui/create
         :update mui/update}})

(defn reagent-component [id form view events config comp-type]
  (let [component (get-in skins [(:skin view) comp-type])]
    (component id form view events config)))

(defn new [comp-type {:keys [id form fetch perform view config] :as params}]
  (let [events (e/events params)]
    {:id id
     :events events
     :reagent-component (reagent-component id form view events config comp-type)
     :state-path (u/state-path id)}))

(def show (partial new :show))
(def list (partial new :list))
(def create (partial new :create))
(def update (partial new :update))
