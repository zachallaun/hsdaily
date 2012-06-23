(ns hsdaily.models.user
  (:use [hsdaily.db :only [conn]]
        [datomic.api :only [q db] :as d])
  (:require [noir.validation :as v]
            [noir.util.crypt :as crypt]))

(defn valid-username? [username]
  (v/rule (not (first (q '[:find ?e
                           :in $ ?un
                           :where [?e :user/username ?un]]
                         (db @conn)
                         username)))
          [:username "That username already exists"])
  (v/rule (v/min-length? username 2)
          [:username "Usernames must be 2 characters"])
  (v/rule (v/max-length? username 15)
          [:username "Usernames must be less than 15 characters"])
  (not (v/errors? :username)))

(defn valid-password? [password]
  (v/rule (v/min-length? password 6)
          [:password "Passwords must be at least 6 characters"])
  (not (v/errors? :password)))

(defn valid? [{:keys [username password]}]
  (and (valid-username? username)
       (valid-password? password)))

(defn prep-new [{:keys [password] :as user}]
  (let [{:keys [username password]} (-> user
                                        (assoc :password (crypt/encrypt password)))]
    {:db/id (d/tempid :db.part/user)
     :user/username username
     :user/password password}))

(defn add! [user]
  (when (valid? user)
    (d/transact @conn [(prep-new user)])))

(def username->pass '[:find ?pass
                      :in $ ?un
                      :where
                      [?e :user/username ?un]
                      [?e :user/password ?pass]])

(defn valid-login? [{:keys [username password]}]
  (crypt/compare password
                 (ffirst (q username->pass (db @conn) username))))
