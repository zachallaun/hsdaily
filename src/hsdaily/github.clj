(ns hsdaily.github
  (:use [datomic.api :only [q db] :as d]
        [hsdaily.db :only [conn]]
        [hsdaily.query :only [val-at-key]]
        [clj-http.client :only [generate-query-string] :as http])
  (:require [cheshire.core :as json]
            [hsdaily.util :as util]))

;; Datomic access funcs
(defn client-id
  "Accepts a datomic connection; returns the value at :github/client-id."
  [conn]
  (ffirst (q val-at-key (db conn) :github/client-id)))

(defn client-secret
  "Accepts a datomic connection; returns the value at :github/client-secret"
  [conn]
  (ffirst (q val-at-key (db conn) :github/client-secret)))

;; Github oauth/api urls
(def auth-url "https://github.com/login/oauth/authorize")
(def token-url "https://github.com/login/oauth/access_token")
(def api-url "https://api.github.com/")

(def oauth-access-url (str auth-url "?" (generate-query-string
                                         {:client_id (client-id @conn)})))

(def gen-user-url
  "Accepts a github access-token; returns an api url for that user."
  (partial str api-url "user?access_token=") )

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
                  {:form-params {:client_id (client-id @conn)
                                 :client_secret (client-secret @conn)
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
(def get-user (comp fetch-and-decode-body gen-user-url))
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
