(ns github.issues
 (:require
  [javelin.core :as j]))

(defn issues=
 []
 (j/with-let [c (j/cell nil)]
  (.then
   (.then
    (js/fetch "https://api.github.com/repos/holochain/acorn-example/issues")
    (fn [response] (.json response)))
   (fn [json]
    (reset! c (js->clj json :keywordize-keys true))))))
