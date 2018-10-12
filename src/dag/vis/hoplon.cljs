(ns dag.vis.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  dag.core.api
  cytoscape.lib))

(defn vis
 []
 (j/with-let [el (h/div
                  :css {:height "100px"
                        :width "100px"})]
  (let [elements (dag.core.api/items->elements
                  dag.core.data/example-items
                  :id-fn :id
                  :targets-fn :targets)]
   (h/with-dom el
    (js/cytoscape
     (clj->js
      {:container el
       :elements elements}))))))
