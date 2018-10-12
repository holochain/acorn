(ns typography.styles
 (:require
  typography.data
  typography.core
  [garden.units :as u]
  color.data)
 (:refer-clojure :exclude [+ - * /]))

(def ^:screen reset
 [:*
  {:box-sizing :border-box}])

(def ^:screen typeset
 (let [heading (wheel.font.core/font->family-str (first font.data/fonts))
       body heading]
  [
   (typography.core/typeset
    heading
    body)]))

(def ^:screen highlight
 [
  [:.highlight
   {:background-color color.data/highlight
    :font-weight 400}]
  [:body :html :p
   {:color color.data/text}]])
