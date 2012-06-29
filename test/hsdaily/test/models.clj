(ns hsdaily.test.models
  (:use [clojure.test]
        [noir.util.test :only [with-noir]]
        [datomic.api :only [q db] :as d]
        [hsdaily.db :only [conn] :as hsdb])
  (:require [hsdaily.models.repo :as repos]
            [hsdaily.models.user :as users]
            [hsdaily.query :as hsq]))

(deftest repo-model

  (testing "project model validations"

    (is (= true false) "Add repo tests")))

(deftest user-model

  (testing "user model validations"

    (is (= true false) "Add user tests")))

(defn test-ns-hook []
  ;; Connect to clean memory db
  (hsdb/retract-all conn hsdb/mem-uri)
  (repo-model)
  (user-model))
