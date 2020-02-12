(ns ccs.core
  (:gen-class)
  (:require [cheshire.core :as ch]
            [nrepl.server :as nrepl]
            [taoensso.timbre :as timbre]
            [partsbin.core :refer [start stop] :as partsbin]
            [environ.core :refer [env]]
            [clj-time.local :as l]
            [clj-time.format :as f]
            [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout tap go-loop sliding-buffer]])
  (:use [yada.yada :as yada]
        [hiccup.core]))

(def state-output-channel (a/chan 2))
(def multi (a/mult state-output-channel))
(defonce state (atom {:foo "bar"
                      :count 1}))

(defn update-state [val]
  (swap! state assoc :foo val))


(defn get-state []
  (yada/resource
    {:methods {:get
               {:produces "text/event-stream"
                :response (fn [ctx]
                            (let [response (:response ctx)
                                  response-with-access (assoc-in response [:headers] {"Access-Control-Allow-Origin" "*"})
                                  final-response (assoc-in response-with-access [:body] (a/tap multi (a/chan 2)))]
                              (go (>! state-output-channel (ch/generate-string @state)))
                              final-response))}}
     :access-control {:allow-origin "localhost"
                      :allow-credentials true
                      :expose-headers #{"X-Custom"}
                      :allow-methods #{:get :post}
                      :allow-headers ["Api-Key"]}}))

(defn get-version-page []
  (yada/resource
    {:methods {:get
               {:produces "application/json"
                :response (fn [ctx]
                            (let [response (:response ctx)
                                  response-with-access (assoc-in response [:headers] {"Access-Control-Allow-Origin" "*"})
                                  final-response (assoc-in response-with-access [:body] "Version Manager Server -- Version 0.0.0")]
                              final-response))}}}))



(defn run-resource []
      (yada/resource
        {:methods {:get
                   {:produces "application/json"
                    :response (fn [ctx]
                                (let [response (:response ctx)
                                      response-with-access (assoc-in response [:headers] {"Access-Control-Allow-Origin" "*"})
                                      param (get-in ctx [:parameters :query "param"])
                                      result (update-state param)]
                                     (assoc-in response-with-access [:body] result)))}}}))


(defn new-api []
  ["/api"
   [
    ["/version" (get-version-page)]
    ["/state" (get-state)]
    ["/run" (run-resource)]]])

(defonce my-server (atom nil))

(defn create-web-server [port]
  (yada/listener
    (new-api)
    {:port port}))

(defn start-server [port]
  (prn "start-server")
  (reset! my-server (create-web-server port)))


(defn stop-server []
  (prn "stop-server")
  (if @my-server
    (do ((:close @my-server))
        (reset! my-server {}))))

(defn restart-server []
  (stop-server)
  (start-server 8080))

(defn -main []
  (let [nrepl-port 3001
        nrepl-host (env :nrepl-host "0.0.0.0")
        production? (#{"true" true} (env :is-production false))
        nrepl-server (nrepl/start-server :bind nrepl-host :port nrepl-port)]
    (when server (prn (str "nrepl port started on port " nrepl-port "."))))
  (restart-server)
  (add-watch state :watcher (fn [key atom old-state new-state]
                              (go
                                (>! state-output-channel (ch/generate-string @state))))))



