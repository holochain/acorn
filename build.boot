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
   [crisptrutski/boot-cljs-test "0.3.5-SNAPSHOT"]
   [adzerk/bootlaces "0.1.13"]
   [com.taoensso/timbre "4.10.0"]
   [samestep/boot-refresh "0.1.0"]

   ; transitive deps...
   [doo "0.1.8"]

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
    :provides ["dagre.lib"]}]})

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
