(defproject ccs.server "0.1.0-SNAPSHOT"
  :description "Clojure server boilerplate"
  :url "https://github.com/kmbyrne/clojure-cljs-starter"
  :license {:name "Eclipse Public License v 2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.async "0.3.443"]
                 [clj-http "3.10.0"]
                 [cheshire "5.8.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [http-kit "2.3.0"]
                 [hiccup "1.0.5"]
                 [clj-jgit "1.0.0-beta2"]
                 [compojure "1.6.1"]
                 [nrepl "0.6.0"]
                 [environ "1.1.0"]
                 [ring "1.7.1"]
                 [ring-cors "0.1.13"]
                 [yada "1.3.0-alpha13"]
                 [markbastian/partsbin "0.1.2"]]
  :repl-options {:init-ns ccs.core}
  :main ^:skip-aot ccs.core
  :profiles {:uberjar {:aot :all}})






