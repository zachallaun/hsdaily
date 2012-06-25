(ns hsdaily.util
  (:use [clojure.string :only [join]]))

(defn map->query-string [m]
  (join "&" (for [[k v] m]
              (str (name k) "=" (java.net.URLEncoder/encode v)))))
