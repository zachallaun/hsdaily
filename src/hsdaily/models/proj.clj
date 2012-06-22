(ns hsdaily.models.proj
  (:require [monger.collection :as mc]
            [noir.validation :as v]))

(def proj-collection "projects")

(defn valid? [{:keys [name]}]
  (v/rule (v/has-value? name)
          [:title "Please enter a name for this project"])
  (not (v/errors?)))

(defn prep-new [{:keys [desc] :as proj}]
  (-> proj
      (assoc :desc (or desc "A new project."))))

(defmulti add!
  "Allow single or batch insert."
  vector?)

(defmethod add! true [projs]
  (mc/insert-batch proj-collection (map prep-new (filter valid? projs))))

(defmethod add! false [proj]
  (when (valid? proj)
    (mc/insert proj-collection (prep-new proj))))
