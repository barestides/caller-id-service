(ns caller-id-service.server
  (:require [bidi.bidi :as bidi]
            [bidi.ring :as bidi-ring]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [cheshire.core :refer [generate-string]]
            [org.httpkit.server :as httpkit]
            [caller-id-service.fake-db :as fake-db]
            [caller-id-service.util :as util]))

(defn validate
  "Returns nil if there are no validation errors. Otherwise returns the HTTP response map
  for the first error."
  [val pred-resp-pairs]
  (if (empty? pred-resp-pairs)
    nil
    (let [[pred resp] (first pred-resp-pairs)]
      (if (pred val)
        (recur val (rest pred-resp-pairs))
        resp))))

(defn bad-request-response
  [msg]
  {:status 400
   :body msg})

(defn query-number
  [req]
  (let [number (get-in req [:params :number])
        error (validate number [[util/e-164?
                                 (bad-request-response "Number not in E.164 Format")]])]
    (or error
        (let [records (fake-db/records-for-number number)]
          {:status 200
           :body (generate-string records)}))))

(defn add-record
  [req]
  (let [record (:params req)
        error (validate record
                        [[(comp not-empty :name)
                          (bad-request-response "Name cannot be blank")]
                         [(comp not-empty :number)
                          (bad-request-response "Number cannot be blank")]
                         [(comp not-empty :context)
                          (bad-request-response "Context cannot be blank")]
                         [(comp util/e-164? :number)
                          (bad-request-response "Number not in E.164 format")]
                         [(fn [{:keys [context number]}]
                            (not (fake-db/context-number-pair-exists? context number)))
                          (bad-request-response
                           "Record with supplied number and context already exists.")]])]
    (or error
        (do (fake-db/add-record! record)
            {:status 200
             :body "Record added"}))))

(def routes
  ["/" {"query" query-number
        "number" add-record}])

(defonce server (atom nil))

(defn start-server
  [port]
  (let [handler (bidi-ring/make-handler routes)
        app (-> handler
                wrap-keyword-params
                wrap-params)]
    (reset! server (httpkit/run-server app {:port port}))))

(defn stop-server
  []
  (@server))
