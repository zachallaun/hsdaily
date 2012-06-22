(ns hsdaily.views.projs
  (:use [noir.core :only [defpage defpartial]])
  (:require [hsdaily.views.common :as com]))

(defpage "/" []
  (com/layout
   [:h1 "Hello HSDaily"]))
