(ns hsdaily.models.repo
  (:use [hsdaily.db :only [conn]]
        [datomic.api :only [q db] :as d])
  (:require [hsdaily.models.user :as users]
            [hsdaily.github :as gh]))

(defn prep-repo [{:keys [full_name html_url name owner description]}]
  {:db/id (d/tempid :db.part/user)
   :repo/name name
   :repo/full-name full_name
   :repo/url (java.net.URI. html_url)
   :repo/owner (users/username->id (:login owner))
   :repo/desc description})

(defn insert! [repos]
  @(d/transact @conn (map prep-repo repos)))

(defn insert-user-repos! [user]
  (insert! (gh/get-user-repos (:user/username user))))

(def tracked-repos #(q '[:find ?id ?name ?un
                         :where
                         [?id :repo/tracked true]
                         [?id :repo/name ?name]
                         [?id :repo/owner ?user]
                         [?user :user/username ?un]] (db @conn)))
