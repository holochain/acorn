(ns dag.vis.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  dag.core.api
  cytoscape.lib
  color.data
  github.issues
  cytoscape-dagre.lib))

(defn with-cytoscape!
 [el elements= options]
 (.log js/console (clj->js options))
 (let [ready? (j/cell false)
       cy (js/cytoscape (clj->js (merge {:container el} options)))]
  (h/with-dom el (reset! ready? true))
  (j/formula-of [ready? elements=]
   (when (and ready? (seq elements=))
    (.log js/console (.elements cy))
    (prn elements=)
    (.remove (.elements cy))
    (.add cy (clj->js elements=))
    (.run (.layout cy (clj->js {:name :dagre})))))
  el))

(defn vis
 []
 (let [
       id (comp str :number)
       label (comp str :title)
       targets (fn [s]
                (map
                 #(clojure.string/replace % "child of #" "")
                 (re-seq #"child of #[0-9]+" (str s))))
       items->elements (partial dag.core.api/items->elements :id-fn id :targets-fn targets :label-fn label)
       issues= (github.issues/issues=)
       elements= (j/cell= (items->elements :items issues=))]

  (with-cytoscape!
   (h/div
    :css
    {:height "800px"
     :width "800px"})
   elements=
   {:layout {:name :dagre}
    :style
    [
     {:selector :node
      :style {:background-color color.data/background
              :content "data(label)"
              :color color.data/highlight}}
     {:selector :edge
      :style {:line-color color.data/background
              :source-arrow-shape "triangle"
              :curve-style :bezier
              :source-arrow-color color.data/background}}]})))
