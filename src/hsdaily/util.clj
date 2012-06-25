(ns hsdaily.util
  (:use [clojure.string :only [join]]))

;; Use clj-http.client/generate-query-string instead
;; (defn map->query-string [m]
;;   (join "&" (for [[k v] m]
;;               (str (name k) "=" (java.net.URLEncoder/encode v)))))

(defn query-str->map [qs]
  (into {} (for [[_ k v] (re-seq #"([^&=]+)=([^&]+)" qs)]
    [(keyword k) v])))
