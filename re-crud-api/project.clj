 (defproject re-crud-api "0.1.0-SNAPSHOT"
   :description "FIXME: write description"
   :dependencies [[org.clojure/clojure "1.8.0"]
                  [metosin/compojure-api "1.1.11"]
                  [cheshire "5.8.1"]]
   :ring {:handler re-crud-api.handler/app
          :nrepl {:start? true}}
   :uberjar-name "server.jar"
   :profiles {:dev {:dependencies [[javax.servlet/javax.servlet-api "3.1.0"]]
                    :plugins [[lein-ring "0.12.0"]]}})
