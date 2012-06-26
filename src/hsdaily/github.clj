(ns hsdaily.github
  (:use [datomic.api :only [q db] :as d]
        [hsdaily.db :only [conn]]
        [hsdaily.query :only [val-at-key]]
        [clj-http.client :only [generate-query-string] :as http]
        [clj-yaml.core :as yaml])
  (:require [cheshire.core :as json]
            [hsdaily.util :as util]))

(def gh-keys (yaml/parse-string (slurp "./ghclient.yaml")))

;; Github oauth/api urls
(def auth-url "https://github.com/login/oauth/authorize")
(def token-url "https://github.com/login/oauth/access_token")
(def api-url "https://api.github.com/")

(def oauth-access-url (str auth-url "?" (generate-query-string
                                         {:client_id (:client-id gh-keys)})))

(def auth-user-api (partial str api-url "user?access_token="))
(def users-api (partial str api-url "users/"))
(def repos-api (partial str api-url "repos/"))

(defn repos-of-user
  "/users/:user/repos"
  [user]
  (users-api user "/repos"))

(defn item-at-repo
  [item]
  (fn [u r]
    (repos-api u "/" r item)))

(def branches-of-repo (item-at-repo "/branches"))
(def commits-on-repo (item-at-repo "/commits"))

(defn get-token [code]
  (->> (http/post token-url
                  {:form-params {:client_id (:client-id gh-keys)
                                 :client_secret (:client-secret gh-keys)
                                 :code code}})
       (:body)
       (util/query-str->map)
       (:access_token)))

(defn fetch-and-decode-body
  [url]
  (-> (http/get url)
      (:body)
      (json/decode true)))

;; Functions accept arguments of api-url generator
(def get-user (comp fetch-and-decode-body auth-user-api))
(def get-repos-of-user (comp fetch-and-decode-body repos-of-user))
(def get-branches-of-repo (comp fetch-and-decode-body branches-of-repo))

(defn get-commits-of-branch
  [user repo branch-sha]
  (let [commits (-> (http/get (commits-on-repo user repo)
                              {:query-params {:sha branch-sha}})
                    (:body)
                    (json/decode true))]
    (map :commit commits)))

(defn get-commits-of-repo
  "Returns a seq of maps of the form {:branch \"branch-name\" :commits [...]}"
  [user repo]
  (let [branches (get-branches-of-repo user repo)]
    (map (fn [branch]
           {:branch (:name branch)
            :commits (get-commits-of-branch user repo
                                            (get-in branch [:commit :sha]))})
         branches)))
