(ns hsdaily.util
  (:use [clojure.string :only [join]]))

(defn query-str->map [qs]
  (into {} (for [[_ k v] (re-seq #"([^&=]+)=([^&]+)" qs)]
    [(keyword k) v])))

(defn seq-if-not
  "If an item is not sequential, returns a sequential collection containing
only that item."
  [item]
  (if (sequential? item) item '(item)))
