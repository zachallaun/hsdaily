(ns hsdaily.views.projs
  (:require [hsdaily.views.common :as com])
  (:use [noir.core :only [defpage defpartial]]))

(defpage "/" []
  (com/layout
   [:h1 "Hello HSDaily"]))
