(defproject caller-id-service "0.1.0"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [http-kit "2.3.0"]
                 [bidi "2.1.6"]
                 [cheshire "5.8.1"]
                 [ring/ring-core "1.7.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/tools.cli "0.4.2"]]
  :main ^:skip-aot caller-id-service.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
