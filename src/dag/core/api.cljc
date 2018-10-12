(ns dag.core.api
 (:require
  [cljs.test :refer-macros [deftest is]]))

(defn items->elements
 [items & {:keys [id-fn targets-fn]}]
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
            {:data (str id target)
             :source id
             :target target})
           targets)]))]
  (flatten
   (map
    (comp id-targets->elements item->id-targets)
    items))))

; TESTS
