(ns hsdaily.db
  (:require [datomic.api :as d]))

(def dev-uri "datomic:dev://localhost:4334/hsdaily")
(def mem-uri "datomic:mem://hsdaily")

(def schema-tx (read-string (slurp "./hsdaily-schema.dtm")))
;; (def dev-data (read-string (slurp "./hsdaily-dev-data.dtm")))

(defn make-db-and-conn [uri]
  (d/create-database uri)
  (let [c (d/connect uri)]
    @(d/transact c schema-tx)
    c))

(defn retract-all [conn uri]
  (d/delete-database uri)
  (reset! conn (make-db-and-conn uri)))

(def conn (atom (make-db-and-conn dev-uri)))
