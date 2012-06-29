(ns hsdaily.models.repo
  (:use [hsdaily.db :only [conn]]
        [hsdaily.util :only [seq-if-not]]
        [datomic.api :only [q db] :as d])
  (:require [noir.validation :as v]
            [hsdaily.models.user :as users]))

(defn prep-repo [{:keys [full_name html_url name owner description]}]
  {:db/id (d/tempid :db.part/user)
   :repo/name name
   :repo/full-name full_name
   :repo/url (java.net.URI. html_url)
   :repo/owner (users/username->id (:login owner))
   :repo/desc description})

(defn insert! [repos]
  (let [repos (seq-if-not repos)]
    @(d/transact @conn (map prep-repo repos))))
