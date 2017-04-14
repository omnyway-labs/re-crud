(defproject re-crud "0.1.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.2"]
                 [cljs-ajax "0.5.8"]
                 [camel-snake-kebab "0.4.0"]
                 [cljsjs/reactable "0.14.1-0"]]

  :plugins [[lein-cljsbuild "1.1.4"]]
  :hooks [leiningen.cljsbuild]
  :min-lein-version "2.5.3"

  :source-paths ["src/cljs"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild
  {:builds
   [{:id           "min"
     :source-paths ["src/cljs"]
     :compiler     {:output-dir      ".cljsbuild/re-crud"
                    :output-to       "public/re-crud.js"
                    :optimizations   :whitespace
                    :pretty-print    true
                    :closure-defines {goog.DEBUG false}}}]})
