(ns caller-id-service.server-test
  (:require [clojure.test :refer :all]
            [org.httpkit.client :as client]
            [caller-id-service.util :as util]
            [caller-id-service.server :refer :all]
            [caller-id-service.fake-db :as fake-db]))

(deftest add-and-query-test
  (reset! fake-db/numbers-db [])
  (start-server 8080)
  (let [dummy-record {:name "Braden Arestides"
                      :number "+19372279478"
                      :context "phone"}
        make-add-req (fn [record]
                       @(client/post "http://localhost:8080/number" {:query-params record}))
        add-response (make-add-req dummy-record)]
    (is (= 200 (:status add-response)))
    (let [duplicate-add (make-add-req dummy-record)]
      (is (= 400 (:status duplicate-add))))
    (let [missing-data-add (make-add-req (assoc dummy-record :name ""))]
      (is (= 400 (:status missing-data-add)))))
  (let [response @(client/get "http://localhost:8080/query?number=%2B19372279478" {:as :text})
        {:keys [body status]} response]
    (is (= 200 status))
    (is (= body
           "[{\"name\":\"Braden Arestides\",\"number\":\"+19372279478\",\"context\":\"phone\"}]")))
  (stop-server))
