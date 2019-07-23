(ns quark.http.protocols.http-client
  (:require [quark.system.options :as options]))

(defprotocol HttpClient
  "Protocol for making HTTP requests (outbound)"
  (req! [component req-map] [component defaults req-map] "Make a request, optionally overriding the default request map"))

(defmethod options/transform :http
  [_ vs]
  {:bookmarks
   (->> vs
        (keep :bookmarks)
        (reduce merge {}))})
