(ns hsdaily.util
  (:use [clojure.string :only [join]])
  (:require [overtone.at-at :as at]))

(defn query-str->map [qs]
  (into {} (for [[_ k v] (re-seq #"([^&=]+)=([^&]+)" qs)]
    [(keyword k) v])))

;; (defn seq-if-not
;;   "If an item is not sequential, returns a sequential collection containing
;; only that item."
;;   [item]
;;   (if (sequential? item) item [item]))

(defn- decode-unit
  [n unit]
  (let [sec (partial * 1000)
        minute (partial sec 60)
        hour (partial minute 60)
        day (partial hour 24)]
    (case unit
      :milli n
      :sec (sec n)
      :min (minute n)
      :hour (hour n)
      :day (day n))))

(defn- decode-time
  "Converts a vector of the form [1 :hour] to milliseconds.
   Accepts :milli :sec, :min, :hour, :day"
  [[n unit & more :as time]]
  {:pre [(even? (count time))]}
  (loop [[n unit & more :as time] time
         total 0]
    (if (seq more)
      (recur more (+ total (decode-unit n unit)))
      (+ total (decode-unit n unit)))))

(defn every
  "Wraps at-at/every with a nicer language for time:
   (every [1 :hour] #(println \"on the hour\") job-pool)"
  [time f pool]
  (at/every (decode-time time) f pool))
