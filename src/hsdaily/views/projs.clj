(ns hsdaily.views.projs
  (:use [noir.core :only [defpage defpartial]])
  (:require [hsdaily.views.common :as com]
            [hsdaily.models.user :as users]
            [noir.session :as session]))

(defpage "/" []
  (com/layout
   [:h1 "Hello, " (or (:user/username (users/current-user))
                      "No-man")]))
