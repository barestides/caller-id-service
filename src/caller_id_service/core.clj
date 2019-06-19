(ns caller-id-service.core
  (:require [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]
            [taoensso.timbre :refer [info]]
            [caller-id-service.server :as server]
            [caller-id-service.fake-db :as fake-db])
  (:gen-class))

(def cli-options
  [;;Just stole straight from the cli-tools page
   ;;https://github.com/clojure/tools.cli
   ["-p" "--port PORT" "Port number"
    :default 80
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]

   ["-s" "--seed SEED-FILE" "Seed file"
    :default "resources/small-seed.txt"
    :parse-fn io/file
    :validate #(.exists (io/file %))]])

(defn -main
  [& args]
  (prn args)
  (let [parsed-options (parse-opts args cli-options)
        {:keys [port seed-file]} (:options parsed-options)]
    (info "Loading seed records")
    (fake-db/load-seed seed-file)
    (info "Seed records loaded")
    (info "Starting webserver")
    (server/start-server port)))
