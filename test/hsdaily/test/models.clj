(ns hsdaily.test.models
  (:use [clojure.test]
        [noir.util.test :only [with-noir]]
        [datomic.api :only [q db] :as d]
        [monger.core :only [set-db! get-db connect!]]
        [hsdaily.db :only [conn] :as hsdb])
  (:require [hsdaily.models.proj :as projs]
            [hsdaily.models.user :as users]
            [monger.collection :as mc]))

(deftest project-model

  ;; Connect to clean memory db
  (hsdb/retract-all conn hsdb/mem-uri)

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

      (is (= (count (q '[:find ?e :where [?e :project/name]] (db @conn)))
             1)))

    (testing "batch insert"
      (with-noir (projs/add! [{:name "test proj 2"}
                              {:name "test proj 3"}]))

      (is (= (count (q '[:find ?e :where [?e :project/name]] (db @conn)))
             3)))))

(deftest user-model

  (mc/remove users/user-collection)

  (testing "user model validations"

    (testing "username validation"
      (is (true? (with-noir (users/valid-username? "testusername"))))
      (are [un] (false? (with-noir (users/valid-username? un)))
           "z"
           "reallylongusername"))

    (testing "password validation"
      (is (true? (with-noir (users/valid-password? "password123"))))
      (is (false? (with-noir (users/valid-password? "weak")))))

    (is (true? (with-noir (users/valid? {:username "testusername"
                                         :password "password123"}))))
    (is (false? (with-noir (users/valid? {:username "testusername"
                                          :password "weak"}))))
    (is (false? (with-noir (users/valid? {:username "z"
                                          :password "password123"})))))

  (testing "password encryption"
    (is (not= "password123"
              (:password (users/prep-new {:password "password123"})))))

  (testing "user document insert"
    (with-noir (users/add! {:username "testusername"
                            :password "password123"}))
    (is (= (mc/count users/user-collection)
           1))

    (testing "no duplicate usernames"
      (with-noir (users/add! {:username "testusername"
                              :password "anotherpassword"}))
      (is (= (mc/count users/user-collection)
             1)))))

(defn test-ns-hook []
  (connect!)
  (set-db! (get-db "hsdaily-testing"))
  (project-model)
  (user-model))
