(defproject ring-example "0.1.0-SNAPSHOT"
  :description "Reitit-http with pedestal & swagger"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [io.pedestal/pedestal.service "0.5.4"]
                 [io.pedestal/pedestal.jetty "0.5.4"]
                 [metosin/reitit "0.2.2-20180908.080335-1"]]
  :repl-options {:init-ns example.server})
