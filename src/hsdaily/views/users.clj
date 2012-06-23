(ns hsdaily.views.users
  (:use [noir.core :only [defpage defpartial]]
        [hsdaily.views.common :only [request]]
        hiccup.page
        hiccup.form
        [hiccup.element :only [link-to]])
  (:require [hsdaily.models.user :as users]
            [hsdaily.views.common :as com]
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
   (make-form-to "login" login-fields)
   [:div [:p "or " (link-to "/register" "Register")]]))

(defpage "/register" []
  (com/layout
   (make-form-to "register" registration-fields)))
