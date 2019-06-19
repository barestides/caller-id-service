(ns caller-id-service.fake-db
  (:require [clojure.string :as string]
            [caller-id-service.util :as util]))

;;defonce so it doesn't get reset every time we compile this ns when developing
(defonce numbers-db (atom []))

(defn mapify-seed-record
  "Convert the caller id record in the CSV seed to a clojure map."
  [record]
  (let [[number context name] (string/split record #",")]
    ;;If the seed number starts with a +, assume it already is in E.164 format
    ;;Otherwise, strip punctuation from the number
    {:number (if (re-find #"^\+" number)
               number
               (util/only-digits number))
     :context context
     :name name}))

(defn load-seed
  "Copy records stored in a CSV file to an in-memory DB."
  [seed-file]
  (let [string-records (string/split-lines (slurp seed-file))
        records (mapv mapify-seed-record string-records)]
    (reset! numbers-db records)))

(defn context-number-pair-exists?
  [search-context search-number]
  (not-empty (filter (fn [{:keys [context number]}]
                       (and (= context search-context)
                            (= number search-number)))
                     @numbers-db)))

(defn records-for-number
  [number]
  (let [with-country-code (filter #(= (:number %) number) @numbers-db)]
    (if (empty? with-country-code)
      ;;Not all numbers in the DB have country codes, So we should search for the number without
      ;;the country code if there are no records for the number with the code.
      ;;something about why not to lump them together
      (filter #(= (:number %) (apply str (take-last 10 number))) @numbers-db)
      with-country-code)))

(defn add-record!
  [record]
  (swap! numbers-db conj record))
