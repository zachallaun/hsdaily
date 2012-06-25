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

(def auth-query-str (generate-query-string {:client_id (client-id @conn)}))
(def oauth-access-url (str auth-url "?" auth-query-str))

(def gen-user-url
  "Accepts a github access-token; returns an api url for that user."
  (partial str api-url "user?access_token=") )

;; (defn get-username [token]
;;   (-> (http/get (str "https://"))))

(defn get-token [code]
  (->> (http/post token-url
                  {:form-params {:client_id (client-id @conn)
                                 :client_secret (client-secret @conn)
                                 :code code}})
       (:body)
       (util/query-str->map)
       (:access_token)))

(defn get-username [token]
  (-> (http/get (gen-user-url token))
      (:body)
      (json/decode true)
      :login))
