(ns github.issues
 (:require
  [javelin.core :as j]
  [hoplon.core :as h]))

(defn issues=
 []
 (j/with-let [c (j/cell nil)]
  (let [f (fn f []
           (let [headers (doto (js/Headers.)
                          (.append "pragma" "no-cache")
                          (.append "cache-control" "no-cache"))
                 init (clj->js
                       {:method "GET"})]
                        ; github does not allow no-cache headers
                        ; :headers headers})]
            (.then
             (.then
              (js/fetch "https://api.github.com/repos/holochain/acorn-example/issues" init)
              (fn [response] (.json response)))
             (fn [json]
              (reset! c (js->clj json :keywordize-keys true))
              (h/with-timeout 10000 (f))))))]
   (f))))
