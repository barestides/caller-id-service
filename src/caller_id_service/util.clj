(ns caller-id-service.util
  (:require [clojure.pprint :as pprint]))

(defn only-digits
  [string]
  (as-> string $
    (filter #(Character/isDigit %) $)
    (apply str $)))

(defn spy
  "Nifty debugging function. Should probably use an actual debugger one day..."
  [x]
  (pprint/pprint x)
  x)
