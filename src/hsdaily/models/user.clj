(ns hsdaily.models.user
  (:require [monger.collection :as mc]
            [noir.validation :as v]
            [noir.util.crypt :as crypt]))

(def user-collection "users")

(defn valid-username? [username]
  (v/rule (not (mc/find-one user-collection {:username username}))
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
  (-> user
      (assoc :password (crypt/encrypt password))))

(defn add! [user]
  (when (valid? user)
    (mc/insert user-collection (prep-new user))))
