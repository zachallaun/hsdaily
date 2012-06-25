(ns hsdaily.test.models
  (:use [clojure.test]
        [noir.util.test :only [with-noir]]
        [datomic.api :only [q db] :as d]
        [hsdaily.db :only [conn] :as hsdb])
  (:require [hsdaily.models.proj :as projs]
            [hsdaily.models.user :as users]
            [hsdaily.query :as hsq]))

(deftest project-model

  (testing "project model validations"
    (is (true? (with-noir (projs/valid? {:name "Test Project"}))))
    (is (false? (with-noir (projs/valid? {})))))

  (testing "project document preparation"
    (is (contains? (projs/prep-new {})
                   :project/desc))
    (is (= (:project/desc (projs/prep-new {:desc "test description"}))
           "test description")))

  (testing "project document insertion"

    (testing "single insert"
      (with-noir (projs/add! {:name "test proj 1"}))

      (is (= (count (q hsq/all-with-attr (db @conn) :project/name))
             1)))

    (testing "batch insert"
      (with-noir (projs/add! [{:name "test proj 2"}
                              {:name "test proj 3"}]))

      (is (= (count (q hsq/all-with-attr (db @conn) :project/name))
             3)))))

(deftest user-model

  (testing "user model validations"

    (is (= true false) "Add user tests")))

(defn test-ns-hook []
  ;; Connect to clean memory db
  (hsdb/retract-all conn hsdb/mem-uri)
  (project-model)
  (user-model))
