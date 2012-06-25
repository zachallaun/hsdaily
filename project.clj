(defproject hsdaily/hsdaily "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta8"]
                 [com.datomic/datomic "0.1.3164"]
                 [clj-http "0.4.3"]
                 [cheshire "4.0.0"]]
  :main hsdaily.server
  :description "Daily digest of hackerschooler project progress.")
