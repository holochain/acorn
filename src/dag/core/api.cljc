(ns dag.core.api
 (:require
  [clojure.test :refer [deftest is]]
  dag.core.data))

(defn items->elements
 [& {:keys [id-fn targets-fn items label-fn]}]
 {:pre [id-fn targets-fn]}
 (let [
       item->elements
       (fn [item]
        (let [id (id-fn item)]
         (flatten
          [
           {:data
            {:id id
             :label (label-fn item)}}
           (map
            (fn [target]
             {:data
              {:id (str id target)
               :source (str id)
               :target (str target)}})
            (targets-fn item))])))]
  (flatten
   (map
    item->elements
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
