(ns typography.core
 (:refer-clojure :exclude [+ - * /])
 (:require
  [garden.core :refer [css]]
  [garden.units :as u]
  [garden.selectors :as s]
  [garden.arithmetic :refer [+ - * /]]
  garden.stylesheet
  wheel.font.core
  font.data
  typography.data)
 #?(:cljs (:require-macros [garden.def :refer [defstyles]])))
#?(:clj (require '[garden.def :refer [defstyles]]))

; Adopted from https://github.com/facjure/mesh/blob/master/src/mesh/typography.cljc

(defn whole-number? [n]
 #?(:clj (= n (Math/floor n))
    :cljs (= n (.floor js/Math n))))

(defn modular-scale-fn [base ratio]
  {:pre [(number? base) (number? ratio)]}
  (let [[up down] #?(:clj (if (ratio? ratio)
                              (if (< (denominator ratio)
                                     (numerator ratio))
                                  [* /]
                                  [/ *])
                              (if (< 1 ratio)
                                  [* /]
                                  [/ *]))
                     :cljs (if (< 1 ratio)
                               [* /]
                               [/ *]))

        f (float ratio)
        us (iterate #(up % f) base)
        ds (iterate #(down % f) base)]
    (memoize
     (fn ms [n]
       (cond
         (< 0 n) (if (whole-number? n)
                   (nth us n)
                   (let [m (Math/floor (float n))
                         [a b] [(ms m) (ms (inc m))]]
                     (+ a (* (Math/abs (- a b))
                             (- n m)))))
         (< n 0) (if (whole-number? n)
                   (nth ds (Math/abs n))
                   (let [m (Math/floor (float n))
                         [a b] [(ms m) (ms (dec m))]]
                     (+ a (* (Math/abs (- a b))
                             (- n m)))))
         :else base)))))

(defn at-font-face [& {:as kwargs}]
 (let [kwargs (->> (select-keys kwargs [:family :weight :style :embedded-opentype :truetype :eot :woff :svg])
                   (remove (comp nil? second))
                   (into {}))
       font-attrs (select-keys kwargs [:family :weight :style])
       srcs (select-keys kwargs [:embedded-opentype :truetype :woff :svg])
       url (garden.stylesheet/cssfn :url)
       format (garden.stylesheet/cssfn :format)]
  ["@font-face"
   {:font font-attrs}
   (when-not (empty? srcs)
     {:src (for [[fmt uri] srcs]
            [(url uri) (format (garden.util/wrap-quotes (name fmt)))])})]))

(defn font [family size weight kerning leading & options]
 {:font-family family
  :font-size (u/px size)
  :font-weight weight
  :letter-spacing (u/px kerning)
  :line-height (u/px leading)
  :text-align "left"
  :text-transform (get options :text-transform "none")})

(defn typeset [heading body]
 ; uses 1 for base size due to rem units
 (let [scale (modular-scale-fn typography.data/base-font-size typography.data/scale-ratio)]
  [[:body :p (font body (scale 0) 400 0 (scale 1))]
   [:h1 (font heading (scale 3) 700 0 (scale 0))]
   [:h2 (font heading (scale 2) 700 0 (scale 0))]
   [:h3 (font body (scale 1) 700 0 (scale 1))]
   [:h4 (font body (scale 0) 700 0 (scale 0))]
   [:h5 :h6 (font body (scale -1) 700 0 (scale 0))]]))

(def modular-scale
 (let [f (modular-scale-fn typography.data/base-font-size typography.data/scale-ratio)]
  (fn [n]
   (u/rem (f n)))))
