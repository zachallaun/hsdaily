(ns hsdaily.query
  (:use [datomic.api :only [q db] :as d]))

(def all-with-attr '[:find ?e :in $ ?a :where [?e ?a]])
(def val-at-key '[:find ?val :in $ ?key :where [_ ?key ?val]])
(def entities-with-key '[:find ?e
                         :in $ ?key ?val
                         :where [?e ?key ?val]])

(defn q-first-id-with-key [conn key val]
  (let [entity (first (q entities-with-key (db conn) key val))]
    (when (not (nil? entity))
      (first entity))))
