(ns hsdaily.views.common
  (:use [noir.core :only [defpartial defpage pre-route]]
        [hiccup.page :only [include-css html5]]
        [hiccup.element :only [link-to]])
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
    (include-css "/css/bootstrap.css")
    (include-css "/css/auth-buttons.css")]
   [:body
    [:div.container
     [:div.row
      [:div.span4 [:h1 "Hacker School Daily"] [:h6 "digests for hacker schoolers"]]
      [:div.span1.offset7
       (if (users/current-user)
         (link-to "/logout" "Logout")
         (link-to "/login" "Login"))]]
     content]]))
