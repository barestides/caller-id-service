(ns caller-id-service.util
  (:require [clojure.pprint :as pprint]))


(defn only-digits
  [string]
  (as-> string $
    (filter #(Character/isDigit %) $)
    (apply str $)))
