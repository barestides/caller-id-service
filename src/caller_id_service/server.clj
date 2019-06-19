(ns caller-id-service.server
  (:require [bidi.bidi :as bidi]
            [bidi.ring :as bidi-ring]
            [ring.middleware.params :refer [wrap-params]]
            [cheshire.core :refer [generate-string]]
            [org.httpkit.server :as httpkit]
            [caller-id-service.fake-db :as fake-db]
            [caller-id-service.util :as util]))

(defn query-number
  [req]
  (let [number (get (:params req) "number")
        records (fake-db/records-for-number number)]
    {:status 200
     :body (generate-string records)}))

(defn add-record
  [req]
  )

(def routes
  ["/" {"query" query-number
        "number" add-record}])

(defonce server (atom nil))

(defn start-server
  [port]
  (let [handler (bidi-ring/make-handler routes)
        app (wrap-params handler)]
    (reset! server (httpkit/run-server app {:port port}))))

(defn stop-server
  []
  (@server))
