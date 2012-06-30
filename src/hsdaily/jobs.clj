(ns hsdaily.jobs
  (:use [overtone.at-at :only [mk-pool]]
        [hsdaily.util :only [every]]
        [datomic.api :only [db q] :as d]
        [hsdaily.query :only [entities-with-key]]
        [hsdaily.db :only [conn]])
  (:require [hsdaily.github :as gh]
            [hsdaily.models.commit :as commits]))

(def job-pool (mk-pool))

(def tracked-repos #(q '[:find ?id ?name ?un
                         :where
                         [?id :repo/tracked true]
                         [?id :repo/name ?name]
                         [?id :repo/owner ?user]
                         [?user :user/username ?un]] (db @conn)))

(defn poll-tracked
  "Polls github for commits on tracked repos."
  []
  (map (fn [[id name un]]
         (commits/insert! id (gh/get-flat-repo-commits un name)))
       (tracked-repos)))

(every [1 :hour] poll-tracked job-pool :desc "Poll tracked repos for new commits")
