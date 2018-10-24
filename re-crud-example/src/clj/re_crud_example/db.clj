(ns re-crud-example.db)

(defonce DB
  (atom {}))

(defn get-users []
  (or (vals (:users @DB))
      []))

(defn get-user [id]
  (get-in @DB [:users id]))

(defn add-user [user-params]
  (let [existing-users (get-users)
        new-id (inc (apply max (conj (map :id existing-users) 0)))
        new-user (assoc user-params :id new-id)]
    (swap! DB assoc-in [:users new-id] new-user)
    new-user))

(defn update-user [id user-params]
  (when-let [user (get-user id)]
    (let [updated-user (merge user user-params)]
      (swap! DB assoc-in [:users id] updated-user)
      updated-user)))

(defn delete-user [user-id]
  (when-let [user (get-user user-id)]
    (swap! DB update-in [:users] dissoc user-id)
    (swap! DB update-in [:todos] dissoc user-id)
    :ok))

(defn get-todos [user-id]
  (or (vals (get-in @DB [:todos user-id]))
      []))

(defn get-todo [user-id todo-id]
  (get-in @DB [:todos user-id todo-id]))

(defn add-todo [user-id todo-params]
  (let [existing-todos (get-todos user-id)
        new-id (inc (apply max (conj (map :id existing-todos) 0)))
        new-todo (assoc todo-params
                        :id new-id
                        :user-id user-id)]
    (swap! DB assoc-in [:todos user-id new-id] new-todo)
    new-todo))

(defn update-todo [user-id todo-id todo-params]
  (when-let [todo (get-todo user-id todo-id)]
    (let [updated-todo (merge todo todo-params)]
      (swap! DB assoc-in [:todos user-id todo-id] updated-todo)
      updated-todo)))

(defn delete-todo [user-id todo-id]
  (when-let [todo (get-todo user-id todo-id)]
    (swap! DB update-in [:todos user-id] dissoc todo-id)
    :ok))
