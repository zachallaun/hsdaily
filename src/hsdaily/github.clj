(ns hsdaily.github
  (:use [datomic.api :only [q db] :as d]
        [hsdaily.query :only [val-at-key]]))

(defn client-id
  "Accepts a datomic connection; returns the value at :github/client-id."
  [conn]
  (ffirst (q val-at-key (db conn) :github/client-id)))

(defn client-secret
  "Accepts a datomic connection; returns the value at :github/client-secret"
  [conn]
  (ffirst (q val-at-key (db conn) :github/client-secret)))
