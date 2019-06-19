(ns caller-id-service.core
  (:require [clojure.string :as string]
            [clojure.java.io :as io]
            [caller-id-service.server :as server]
            [caller-id-service.util :as util]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(defonce numbers-db (atom #{}))

(defn mapify-seed-record
  [record]
  (let [[number context name] (string/split record #",")]
    ;;if the seed number starts with a +, assume it already is in E.164 format
    ;;otherwise, strip punctuation from the number
    {:number (if (re-find #"^\+" number)
               number
               (util/only-digits number))
     :context context
     :name name}))

(defn load-seed
  [seed-file]
  (let [string-records (string/split-lines (slurp seed-file))
        records (set (map mapify-seed-record string-records))]
    (reset! numbers-db records)))

(def cli-options
  [;;Just stole straight from the cli-tools page
   ;;https://github.com/clojure/tools.cli
   ["-p" "--port PORT" "Port number"
    :default 80
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]

   ["-s" "--seed SEED-FILE" "Seed file"
    :default "resources/small-seed.txt"
    :validate #(.exists (io/file %))]])

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [options (parse-opts args cli-options)
        port (get-in options [:options :port])]))
