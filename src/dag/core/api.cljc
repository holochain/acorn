(ns dag.core.api
 (:require
  [clojure.test :refer [deftest is]]
  dag.core.data))

(defn items->elements
 [& {:keys [id-fn targets-fn items]}]
 {:pre [id-fn targets-fn]}
 (let [item->id-targets
       (fn [item]
        ((juxt id-fn targets-fn) item))

       id-targets->elements
       (fn [[id targets]]
        (flatten
         [
          {:data {:id id}}
          (map
           (fn [target]
            {:data
             {:id (str id target)
              :source (str id)
              :target (str target)}})
           targets)]))]
  (flatten
   (map
    (comp id-targets->elements item->id-targets)
    items))))

; TESTS

(deftest ??items->elements
  (is
   (=
    dag.core.data/example-elements
    (items->elements
     :items dag.core.data/example-items
     :id-fn :id
     :targets-fn :targets))))
