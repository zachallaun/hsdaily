(ns hsdaily.server
  (:require [noir.server :as server]
            [monger.core :as mg]))

(mg/connect!)
(mg/set-db! (mg/get-db "hsdaily-mongo"))

(server/load-views "src/hsdaily/views/")

(defn -main [& m]
  (let [mode (keyword (or (first m) :dev))
        port (Integer. (get (System/getenv) "PORT" "8080"))]
    (server/start port {:mode mode
                        :ns 'hsdaily})))
