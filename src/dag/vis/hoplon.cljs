(ns dag.vis.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  dag.core.api
  cytoscape.lib
  color.data))

(defn with-cytoscape!
 [el options]
 (.log js/console (clj->js options))
 (h/with-dom el
  (js/cytoscape (clj->js (merge {:container el} options))))
 el)

(defn vis
 []
 (let [items->elements (partial dag.core.api/items->elements :id-fn :id :targets-fn :targets)
       elements (items->elements :items dag.core.data/example-items)]
  (with-cytoscape!
   (h/div
    :css
    {:height "100px"
     :width "100px"})
   {:elements elements
    :style
    [
     {:selector :node
      :style {:background-color color.data/background}}
     {:selector :edge
      :style {:line-color color.data/background}}]})))
