(ns hsdaily.models.repo
  (:use [hsdaily.db :only [conn]]
        [datomic.api :only [q db] :as d])
  (:require [noir.validation :as v]))

;; (defn valid? [{:keys [name]}]
;;   (v/rule (v/has-value? name)
;;           [:title "Please enter a name for this project"])
;;   (not (v/errors?)))

;; (defn prep-new [{:keys [desc] :as proj}]
;;   (let [{:keys [name desc]} (-> proj
;;                                 (assoc :desc (or desc "A new project.")))]
;;     {:db/id (d/tempid :db.part/user)
;;      :project/name name
;;      :project/desc desc}))

;; (defmulti add!
;;   "Allow single or batch insert."
;;   vector?)

;; (defmethod add! true [projs]
;;   (d/transact @conn (map prep-new (filter valid? projs))))

;; (defmethod add! false [proj]
;;   (when (valid? proj)
;;     (d/transact @conn [(prep-new proj)])))
