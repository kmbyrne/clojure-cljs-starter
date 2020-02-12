(ns kbyrne-ccs.app
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent.core :as r]
            [cljs-http.client :as http]
            [cljs.core.async :as async :refer [<! >! chan go-loop]]
            [clojure.string :as str]
            [kbyrne-ccs.lib.event-source :refer [create-event-source]]))

(def state (r/atom {"current-page" "versions"}))

(def event-chan (chan))

(def es (create-event-source "//localhost:8080/api/state" event-chan))

(def continue? true)

(go-loop []
         (let [event (<! event-chan)]
              (swap! state assoc-in ["app-state"] (js->clj (.parse js/JSON (get event "data")))))
         (if continue?
           (recur)
           (.close es)))

(defn head []
      [:h1 "Hello World"])


(defn body []
      [:div {:class "pure-g"}
       [:div {:class "pure-u-2-5"}
        "Hello"]
       [:div {:class "pure-u-3-5"}
        @state]])


(defn app []
      [:div
       [:link {:rel         "stylesheet"
               :href        "https://unpkg.com/purecss@1.0.1/build/pure-min.css"
               :integrity   "sha384-oAOxQR6DkCoMliIh8yFnu25d7Eq/PHS21PClpwjOTeU2jRSq11vu66rf90/cZr47"
               :crossOrigin "anonymous"}]
       (head)
       (body)])


(defn init []
      (r/render
            [app]
            (.getElementById js/document "root")))

