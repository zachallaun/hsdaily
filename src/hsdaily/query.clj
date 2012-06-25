(ns hsdaily.query
  (:use [datomic.api :only [q db] :as d]))

(def all-with-attr '[:find ?e :in $ ?a :where [?e ?a]])
(def val-at-key '[:find ?val :in $ ?key :where [_ ?key ?val]])
