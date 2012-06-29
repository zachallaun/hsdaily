(ns hsdaily.views.users
  (:use [noir.core :only [defpage defpartial]]
        [hsdaily.views.common :only [request]]
        hiccup.page
        hiccup.form
        [hiccup.element :only [link-to]])
  (:require [hsdaily.models.user :as users]
            [hsdaily.models.repo :as repos]
            [hsdaily.views.common :as com]
            [hsdaily.github :as gh]
            [noir.session :as session]
            [noir.response :as resp]))

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
   (if (users/current-user)
     (resp/redirect "/")
     [:div.hero-unit
      [:h1 "Stay in the loop."]
      [:p "Publish projects, discuss ideas, and follow fellow hacker schoolers with a 4-times weekly digest."]
      [:a.btn-auth.btn-github.large {:href gh/oauth-access-url}
       "Sign in with " [:strong "Github"]]])))

(defpage "/logout" []
  (session/remove! :auth-token)
  (resp/redirect "/"))

(defpage "/register" []
  (com/layout
   (make-form-to "register" registration-fields)))

(defpage "/oauth" {:keys [code]}
  (let [user (users/make-or-update-user! code)]
    (session/put! :auth-token (:user/auth-token user))
    (repos/insert-user-repos! user)
    (resp/redirect "/")))
