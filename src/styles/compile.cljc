(ns styles.compile
 (:require
  garden.core
  clojure.tools.namespace.find))

(defn find-garden-ns
 [& dirs]
 (->>
  (map
   (comp
    clojure.tools.namespace.find/find-namespaces-in-dir
    clojure.java.io/file)
   dirs)
  flatten))

(defn nss->syms
 [nss]
 (flatten
  (for [n nss]
   (for [s (-> n ns-publics keys)]
    (ns-resolve n s)))))

(defn screen
 []
 (let [garden-ns (find-garden-ns "src")
       style-syms (fn [syms] (filter #(-> % meta :screen) syms))]
  (apply require garden-ns)
  (->> (nss->syms garden-ns)
   style-syms
   (map deref))))
