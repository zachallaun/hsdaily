(ns hsdaily.jobs
  (:use [overtone.at-at :only [mk-pool]]
        [hsdaily.util :only [every]])
  (:require [hsdaily.github :as gh]
            [hsdaily.models.repo :as repos]
            [hsdaily.models.commit :as commits]))

(def job-pool (mk-pool))

(defn poll-tracked
  "Polls github for commits on tracked repos."
  []
  (map (fn [[id name un]]
         (commits/insert! id (gh/get-flat-repo-commits un name)))
       (repos/tracked-repos)))

(every [1 :hour] poll-tracked job-pool)
