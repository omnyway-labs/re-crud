(ns re-crud.core-test
  (:require [cljs.test :refer-macros [deftest is testing run-tests async]]
            [re-frame.registrar :as registrar]
            [re-frame.core :as re-frame]
            [re-crud.core :as crud]
            [re-crud.components :as comp]
            [re-crud.components.utils :as u]))

(defonce things-to-test
  (atom #{:create-components
          :after-show}))

(defn init []
  (crud/init
   {"test-service" {:service-host "https://re-crud-example.herokuapp.com"
                    :swagger-url "https://re-crud-example.herokuapp.com/api/swagger.json"
                    :dispatch-on-ready [:on-ready]}}))

(defn create-components [{:keys [db]} _]
  (let [show (comp/show {:id     :user.show
                         :fetch  {:operation-id "getUser"
                                  :after (u/create-fx (fn [_] (re-frame/dispatch [:assert! :fetch])))}
                         :config {:service-name "test-service"}})]
    {:dispatch-n [[:crud-load-component show {:fetch {:user-id 23}}]
                  [:assert! :create-components]]}))

(defn assert! [{:keys [db]} [_ thing-to-test]]
  (swap! things-to-test disj thing-to-test)
  (case thing-to-test
    :create-components (is (some? (registrar/get-handler :event :crud-fetch-user.show)))
    :fetch             (is (= (set (keys (get-in db (u/resource-path :user.show))))
                              #{:id :first_name :last_name :email :created_at :updated_at :url})))
  (if (empty? @things-to-test)
    {:done []}
    {}))

(defn register-events [done]
  (re-frame/reg-event-fx :assert! assert!)
  (re-frame/reg-event-fx :create-components create-components)
  (re-frame/reg-event-fx :on-ready (fn [_ _] {:dispatch [:create-components]}))
  (re-frame/reg-fx :done (fn [_] (done))))

(deftest event-handlers
  (async done
         (register-events done)
         (init)))
