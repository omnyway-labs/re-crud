;; Copyright 2017 Omnyway Inc.

;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at

;; http://www.apache.org/licenses/LICENSE-2.0

;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(defproject org.omnyway/re-crud "0.1.10"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.2"]
                 [cljs-ajax "0.5.8"]
                 [camel-snake-kebab "0.4.0"]
                 [cljsjs/reactable "0.14.1-0"]]

  :profiles {:dev {:dependencies [[http-kit "2.2.0"]
                                  [bidi "2.0.13"]
                                  [ring/ring-core "1.5.0"]
                                  [ring/ring-devel "1.5.0"]
                                  [ring/ring-json "0.4.0"]]
                   :source-paths ["test/clj"]}}
  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-doo "0.1.7"]]

  :min-lein-version "2.5.3"
  :doo {:paths {:phantom "phantomjs --web-security=false"}}

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
                    :closure-defines {goog.DEBUG false}}}
    {:id "test"
     :source-paths ["src/cljs" "test/cljs"]
     :compiler     {:output-dir      ".cljsbuild/re-crud-test"
                    :output-to       "re-crud-test.js"
                    :main            re-crud.test-runner
                    :pretty-print    true}}]})
