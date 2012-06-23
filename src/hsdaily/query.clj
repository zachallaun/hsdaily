(ns hsdaily.query
  (:use [datomic.api :only [q db] :as d]))

(def all-with-attr '[:find ?e :in $ ?a :where [?e ?a]])
