(ns hsdaily.models.commit
  (:use [hsdaily.db :only [conn]]
        [datomic.api :only [q db] :as d]
        [clojure.instant :only [read-instant-date]])
  (:require [hsdaily.github :as gh]))

(defn prep-commit [repo-id {:keys [message tree committer]}]
  (let [sha (:sha tree)
        date (read-instant-date (:date committer))]
    {:db/id (d/tempid :db.part/user)
     :commit/sha sha
     :commit/ts date
     :commit/message message
     :commit/repo repo-id}))

(defn insert!
  "Inserts a set of commits belonging to a repo, as designated by repo-id"
  [repo-id commits]
  @(d/transact @conn (map (partial prep-commit repo-id) commits)))
