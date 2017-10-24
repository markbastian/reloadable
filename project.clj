(defproject reloadable "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [cheshire "5.8.0"]
                 [quil "2.6.0"]
                 [reagent "0.5.1"]
                 [org.clojure/core.async "0.2.374"
                  :exclusions [org.clojure/tools.reader]]]

  :plugins [[lein-figwheel "0.5.2"]
            [lein-cljsbuild "1.1.3" :exclusions [[org.clojure/clojure]]]]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :jar-exclusions [#"\.swp|\.swo|\.DS_Store"]
  :profiles {:uberjar {:aot :all}
             :dev {:plugins [[lein-cljsbuild "1.1.3"]
                             [org.clojure/clojurescript "1.8.51"]]
                   :dependencies [[com.cemerick/piggieback "0.2.1"]
                                  [figwheel-sidecar "0.5.2"]]
                   :repl-options {:nrepl-middleware
                                  [cemerick.piggieback/wrap-cljs-repl]}}
             :cljs {:plugins [[lein-cljsbuild "1.1.2"]] }}

  :source-paths ["src/main/clj" "src/main/cljc"]
  :java-source-paths ["src/main/java"]

  :clj {:builds [{ :source-paths ["src/main/clj" "src/main/cljc" "test"] }]}

  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src/main/cljs" "src/main/cljc"]

                        ;; If no code is to be run, set :figwheel true for continued automagical reloading
                        :figwheel {:on-jsload "tetris.core/on-js-reload"}

                        :compiler {:main tetris.core
                                   :asset-path "js/compiled/out"
                                   :output-to "resources/public/js/compiled/tetris.js"
                                   :output-dir "resources/public/js/compiled/out"
                                   :source-map-timestamp true}}
                       ;; This next build is an compressed minified build for
                       ;; production. You can build this with:
                       ;; lein cljsbuild once min
                       {:id "min"
                        :source-paths ["src/main/cljs" "src/main/cljc"]
                        :compiler {:output-to "resources/public/js/compiled/tetris.js"
                                   :main tetris.core
                                   :optimizations :advanced
                                   :pretty-print false}}]}

  :figwheel {:css-dirs ["resources/public/css"] })

