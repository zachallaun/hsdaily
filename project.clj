(defproject hsdaily/hsdaily "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.2.1"
                  :exclusions [org.clojure/clojure]]
                 [com.datomic/datomic "0.1.3164"]]
  :main hsdaily.server
  :description "FIXME: write this!")
