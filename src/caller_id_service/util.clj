(ns caller-id-service.util
  (:require [clojure.pprint :as pprint]))

(defn only-digits
  [string]
  (as-> string $
    (filter #(Character/isDigit %) $)
    (apply str $)))

(defn e-164?
  "Must start with a +
   Country codes can be 1-3 digits, and phone numbers are 10 digits so valid numbers can be 11-13
   digits in length.
   Could be improved by checking against a list of all valid country codes."
  [number]
  (re-find #"^\+[0-9]{11,13}$" number))

(defn spy
  "Nifty debugging function. Should probably use an actual debugger one day..."
  [x]
  (pprint/pprint x)
  x)
