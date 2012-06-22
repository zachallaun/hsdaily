(ns hsdaily.test.models
  (:use [clojure.test]
        [noir.util.test :only [with-noir]]
        [monger.core :only [set-db! get-db connect!]])
  (:require [hsdaily.models.proj :as projs]
            [monger.collection :as mc]))

(deftest project-model

  (testing "project model validations"
    (is (true? (with-noir (projs/valid? {:name "Test Project"}))))
    (is (false? (with-noir (projs/valid? {})))))

  (testing "project document preparation"
    (is (contains? (projs/prep-new {})
                   :desc))
    (is (= (:desc (projs/prep-new {:desc "test description"}))
           "test description")))

  (testing "project document insertion"
    (mc/remove projs/proj-collection)

    (testing "single insertion"
      (with-noir (projs/add! {:name "test proj 1"}))

      (is (= (mc/count projs/proj-collection)
             1)))

    (testing "batch insertion"
      (with-noir (projs/add! [{:name "test proj 2"}
                              {:name "test proj 3"}]))

      (is (= (mc/count projs/proj-collection)
             3)))))

(defn test-ns-hook []
  (connect!)
  (set-db! (get-db "hsdaily-testing"))
  (project-model))
