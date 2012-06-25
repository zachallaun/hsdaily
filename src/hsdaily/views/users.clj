(ns hsdaily.views.users
  (:use [noir.core :only [defpage defpartial]]
        [hsdaily.views.common :only [request]]
        hiccup.page
        hiccup.form
        [hiccup.element :only [link-to]])
  (:require [hsdaily.models.user :as users]
            [hsdaily.views.common :as com]
            [hsdaily.github :as gh]
            [noir.session :as session]
            [noir.response :as resp]
            [noir.validation :as v]))

(defpartial login-fields []
  (text-field {:placeholder "username"} :username)
  (password-field {:placeholder "password"} :password))

(defpartial registration-fields []
  (login-fields)
  (email-field {:placeholder "email"} :email))

(defpartial make-form-to [loc fields]
  (form-to [:post (str "/" loc)]
    (fields)
    (submit-button loc)))

(defpage "/login" []
  (com/layout
   [:div.hero-unit
    [:h1 "Stay in the loop."]
    [:p "Publish projects, discuss ideas, and follow fellow hacker schoolers with a 4-times weekly digest."]
    [:a.btn-auth.btn-github.large {:href gh/oauth-access-url}
     "Sign in with " [:strong "Github"]]]))

(defpage "/register" []
  (com/layout
   (make-form-to "register" registration-fields)))

(defpage "/oauth" {:keys [code]}
  (session/flash-put! :username (gh/get-username (gh/get-token code)))
  (resp/redirect "/test"))

(defpage "/test" []
  (com/layout
   [:hi "Hello, " (session/flash-get :username)]))
