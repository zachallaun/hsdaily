(ns hsdaily.views.projs
  (:use [noir.core :only [defpage defpartial]]
        [hiccup.element :only [image]])
  (:require [hsdaily.views.common :as com]
            [hsdaily.models.user :as users]
            [hsdaily.github :as gh]
            [noir.session :as session]))

(defpage "/" []
  (com/layout
   (if-let [user (users/current-user)]
     (let [repos (gh/get-user-repos (:user/username user))]
       [:div
        [:div.row
         [:div.span6
          (image (users/avatar-url user))
          [:div.row
           [:div.span4
            [:h4 (:user/username user)]]]]]
        [:table.table.table-striped
         [:thead [:tr [:th "Repo Name"]]]
         [:tbody
          (for [repo repos]
            [:tr [:td (:name repo)]])]]])
     [:h1 "Hello, No-man"])))
