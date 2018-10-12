(def project 'thedavidmeister/nearseed)
(def version "0.0.1")
(def description "")
(def github-url "https://github.com/thedavidmeister/nearseed")

(set-env!
 :source-paths #{"src"}
 :resource-paths #{"src"}
 :dependencies
 '[; scaffolding...
   [org.clojure/clojure "1.10.0-alpha8"]
   [org.clojure/clojurescript "1.10.339"]
   [hoplon/hoplon "7.3.0-SNAPSHOT"]
   [hoplon/javelin "3.9.0"]
   [pandeiro/boot-http "0.8.3"]
   [adzerk/boot-reload "0.5.1"]
   [adzerk/boot-cljs "2.1.4"]
   [tailrecursion/boot-jetty  "0.1.3"]
   [thedavidmeister/boot-github-pages "0.1.0-SNAPSHOT"]
   [adzerk/bootlaces "0.1.13"]
   [com.taoensso/timbre "4.10.0"]
   [samestep/boot-refresh "0.1.0"]

   ; transitive deps...
   [doo "0.1.9"]

   ; [adzerk/boot-test "1.2.0" :scope "test"]
   [crisptrutski/boot-cljs-test "0.3.5-SNAPSHOT" :scope "test"]

   ; everything else...
   [thedavidmeister/hoplon-elem-lib "0.2.0"]
   [thedavidmeister/wheel "0.3.6"]
   [garden "1.3.6"]])

(task-options!
 pom {:project project
      :version version
      :description description
      :url github-url
      :scm {:url github-url}})

(require
 '[adzerk.boot-cljs :refer [cljs]]
 '[hoplon.boot-hoplon :refer [hoplon prerender]]
 '[tailrecursion.boot-jetty :refer [serve]]
 '[thedavidmeister.boot-github-pages :refer [github-pages]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[adzerk.bootlaces :refer :all]
 'garden.core
 'styles.compile
 '[adzerk.boot-reload :refer [reload]]
 '[samestep.boot-refresh :refer [refresh]])

(bootlaces! version)

(def compiler-options
 {:foreign-libs
  [
   ; dagre
   {:file "lib/dagre/0.8.2/dagre.js"
    :file-min "lib/dagre/0.8.2/dagre.min.js"
    :provides ["dagre.lib"]}
   ; cytoscape
   {:file "lib/cytoscape/3.2.17/cytoscape.js"
    :file-min "lib/cytoscape/3.2.17/cytoscape.min.js"
    :provides ["cytoscape.lib"]}]})
;
; (def test-cljs-compiler-options
;  (partial cljs-compiler-options "test-runner" {:load-tests true
;                                                :process-shim false}))

; Adapted from https://github.com/martinklepsch/boot-garden
; Dramatically faster ~15x than the approach with pods which seems to cause CLJS
; to fully recompile or something...
(deftask garden
 "Wraps the garden task provided by boot-garden"
 [p pretty-print? bool "Pretty print the CSS output."]
 (with-pre-wrap fileset
  (let [output-path "main.css"
        css-var 'styles.compile
        tmp (tmp-dir!)
        out (clojure.java.io/file tmp output-path)]
   (info "Compiling %s...\n" (.getName out))
   (clojure.java.io/make-parents out)
   (garden.core/css
    {:output-to (.getPath out)
     :pretty-print pretty-print?
     :vendors ["webkit" "moz"]
     :auto-prefix #{:transform :box-sizing :opacity :appearance}}
    (styles.compile/screen))
   (-> fileset (add-resource tmp) commit!))))

(deftask front-dev
 "Build for local development."
 []
 (comp
  (watch)
  (speak)
  (hoplon)
  (refresh)
  (reload)
  (garden)
  (cljs :compiler-options compiler-options)
  (serve :port 8000)))

(deftask build
 []
 (comp
  (hoplon)
  (garden)
  (cljs
   :optimizations :advanced
   :compiler-options compiler-options)))

(deftask deploy-gh-pages
 []
 (comp
  (build)
  (target
   :dir #{"gh-pages"})
  (github-pages)))

(deftask deploy
 []
 (comp
  (deploy-gh-pages)))

(deftask repl-server
 []
 (comp
  (watch)
  (refresh)
  (repl :server true)))

(deftask repl-client
 []
 (repl :client true))

(deftask tests-cljs
 "Run all the CLJS tests"
 [w watch? bool "Watches the filesystem and reruns tests when changes are made."
  o optimizations OPTIMIZATIONS str "Sets the optimizations level for cljs"]
 ; Run the JS tests
 (comp
  (if watch?
   (comp
    (watch)
    (speak :theme "woodblock"))
   identity)
  (test-cljs
   :exit? (not watch?)
   :js-env :phantom
   :optimizations (or (keyword optimizations) :none)
   :cljs-opts
   (merge
    compiler-options
    {:load-tests true})
     ; :process-shim false})
   :namespaces [#".*"]
   :exclusions [#"styles.compile"])))
