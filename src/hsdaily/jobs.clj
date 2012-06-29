(ns hsdaily.jobs
  (:use [overtone.at-at]
        [datomic.api :only [db q] :as d]
        [hsdaily.query :only [entities-with-key]]
        [hsdaily.db :only [conn]])
  (:require [hsdaily.github :as gh]
            [hsdaily.models.repo :as repos]))

(def job-pool (mk-pool))

(defn update-tracked-repos []
  (let [tracked-repos (q entities-with-key (db @conn) :repo/tracked true)]))
