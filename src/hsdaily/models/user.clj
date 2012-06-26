(ns hsdaily.models.user
  (:use [hsdaily.db :only [conn]]
        [datomic.api :only [q db] :as d])
  (:require [hsdaily.github :as gh]
            [hsdaily.query :as hsq]
            [noir.session :as session]))

(def token->id (partial hsq/q-first-id-with-key @conn :user/auth-token))
(def username->id (partial hsq/q-first-id-with-key @conn :user/username))

(defn prep-user [{:keys [login email auth-token avatar_url] :as user}]
  {:user/username login
   :user/email email
   :user/auth-token auth-token
   :user/avatar-url (java.net.URI. avatar_url)})

(defn prep-new [user]
  (assoc (prep-new user) :db/id (d/tempid :db.part/user)))

(defn add! [user]
  @(d/transact @conn [(prep-new user)]))

(defn update! [user]
  (let [entity (d/entity (db @conn) (username->id (:login user)))
        entity (into {:db/id (:db/id entity)} entity)
        updates (prep-user user)]
    @(d/transact @conn [(merge entity updates)])))

(defn current-user []
  (when-let [token (session/get :auth-token)]
    (d/entity (db @conn) (token->id token))))

(defn avatar-url [user]
  (.toString (:user/avatar-url user)))

(defn make-or-update-user!
  "Accepts a temporary github oauth code, creates a user, and returns a datomic entity."
  [temp-code]
  (let [token (gh/get-token temp-code)
        user (gh/get-user token)]
    (if (nil? (username->id (:login user)))
      (add! (assoc user :auth-token token))
      (update! (assoc user :auth-token token)))
    (d/entity (db @conn) (username->id (:login user)))))
