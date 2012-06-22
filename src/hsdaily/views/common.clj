(ns hsdaily.views.common
  (:use [noir.core :only [defpartial defpage pre-route]]
        [hiccup.page-helpers :only [include-css html5]])
  (:require [hsdaily.models.proj :as projs]
            [hsdaily.models.user :as users]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.request]))

(def request noir.request/ring-request)

;; (pre-route "/*" {:keys [uri]}
;;            (when-not
;;                (or (users/current-user)
;;                    (= uri "/login")
;;                    (= uri "/register")
;;                    (= uri "/sessions/create")
;;                    (= uri "/users/create")
;;                    (re-find #"^/(css)|(img)|(js)|(favicon)" uri))
;;              (resp/redirect "/login")))

(defpartial layout [& content]
            (html5
              [:head
               [:title "hsdaily"]
               (include-css "/css/reset.css")]
              [:body
               [:div#wrapper
                content]]))
