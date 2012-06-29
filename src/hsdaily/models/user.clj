(ns hsdaily.models.user
  (:use [hsdaily.db :only [conn]]
        [datomic.api :only [q db] :as d])
  (:require [hsdaily.github :as gh]
            [hsdaily.query :as hsq]
            [noir.session :as session]))

(def token->id (partial hsq/q-first-id-with-key @conn :user/auth-token))
(def username->id (partial hsq/q-first-id-with-key @conn :user/username))

(defn prep-user [{:keys [login email auth-token avatar_url] :as user}]
  {:db/id (d/tempid :db.part/user)
   :user/username login
   :user/email email
   :user/auth-token auth-token
   :user/avatar-url (java.net.URI. avatar_url)})

(defn current-user []
  (when-let [token (session/get :auth-token)]
    (d/entity (db @conn) (token->id token))))

(defn small-avatar-url [user]
  (str (.toString (:user/avatar-url user)) "&s=50"))

(defn insert! [user]
  @(d/transact @conn [(prep-user user)]))

(defn make-or-update-user!
  "Accepts a temporary github oauth code, creates a user, and returns a datomic entity."
  [temp-code]
  (let [token (gh/get-token temp-code)
        user (gh/get-user token)]
    (insert! (assoc user :auth-token token))
    (d/entity (db @conn) (username->id (:login user)))))
