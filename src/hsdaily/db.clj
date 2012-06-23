(ns hsdaily.db
  (:require [datomic.api :as d]))

;; Development database
(def uri "datomic:dev://localhost:4334/hsdaily")
(d/create-database uri)

(def conn (d/connect uri))

(def schema-tx (read-string (slurp "./hsdaily-schema.dtm")))

(d/transact conn schema-tx)
