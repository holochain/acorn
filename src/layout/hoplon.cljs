(ns layout.hoplon
 (:require
  [hoplon.core :as h]
  [garden.units :as u]))

(h/defelem centered
 [attributes children]
 (h/div
  attributes
  children
  :css {:margin "0 auto"
        :max-width "500px"}))

(defn spacer:section
 []
 (h/div
  :css {:height "50px"
        :width "50px"}))
