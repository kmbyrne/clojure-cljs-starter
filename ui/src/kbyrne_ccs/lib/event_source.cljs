(ns kbyrne-ccs.lib.event-source
 (:require [oops.core :refer [oset!]]
           [cljs.core.async :as a :refer [<! >! go]]))

(defn obj->clj [obj]
      (if (goog.isObject obj)
        (-> (fn [result key]
                (let [v (goog.object/get obj key)]
                     (if (= "function" (goog/typeOf v))
                       result
                       (assoc result key (obj->clj v)))))
            (reduce {} (.getKeys goog/object obj)))
        obj))

(defn event-source
      [uri]
      (js/EventSource. uri))

(defn create-event-source [url output-chan]
      (prn "create-event-source")
      (let [es (event-source url)]
           (oset! es "onmessage" (fn [event] (go (>! output-chan (obj->clj event)))))))
