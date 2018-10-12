(ns typography.hoplon
 (:require
  [hoplon.core :as h]))

(h/defelem highlight
 [attributes children]
 (h/span
  ::class "highlight"
  attributes
  children))
