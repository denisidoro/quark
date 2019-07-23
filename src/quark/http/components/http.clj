(ns quark.http.components.http
  (:require [clojure.string :as str]
            [com.stuartsierra.component :as component]
            [quark.config.protocols.config :as p.config]
            [quark.http.logic.serialization :as serialization]
            [quark.http.protocols.http-client :as http-client]
            [quark.conversion.data :as conversion])
  (:import (java.io Writer)))

(def ^:private ^:const ^String component-name
  "<Http>")

(defn ^:private interpolate-text
  [text replacements]
  (reduce (fn [u [k v]] (str/replace u (str k) (conversion/any->str v))) text replacements))

(defn ^:private sanitized-req-map
  [{:keys [url] :as req-map}
   bookmarks]
  (let [{:keys [replace] :as req-map'}
        (if (keyword? url)
          (merge (get bookmarks url)
                 (dissoc req-map :url))
          req-map)
        url' (interpolate-text (:url req-map') replace)]
    (-> req-map'
        (assoc :url url')
        (dissoc :replace))))

(defn- render-body
  "If we have a payload, use the serialize function from the request map to
   convert it to external"
  [{:keys [payload serialize] :as req-map}]
  (if payload (assoc req-map :body (serialize payload)) req-map))

(defn render-req [default-req-map req-map]
  (-> (merge default-req-map req-map)                       ; allow defaults to be overridden
      render-body))                                         ; serialize the request body

(defn- request-sync!
  "add the response details to the request map"
  [req-map http-impl]
  (assoc req-map :response (http-client/req! http-impl req-map)))

(defn- parse-body
  "If we have a body, use the deserialize function from the request map to
   convert it back to internal"
  [{:keys [deserialize response] :as resp-map}]
  (cond
    (-> response :body nil?)
    resp-map

    (-> response :body string? not)
    (update-in resp-map [:response :body] (comp deserialize slurp))

    (:body response)
    (update-in resp-map [:response :body] deserialize)

    :else
    resp-map))

(defn handle-response [resp-map]
  (-> resp-map
      parse-body                                            ; parse the body and return the response
      :response                                             ; unwrap the response only from the resp-map
      (select-keys [:status :body :headers])))              ; drop excess http implementation keys

(defn do-req-resp!
  [{:keys [cache?] :as req-map}
   {:keys [http-impl cache*]}]
  (let [hash (.hashCode req-map)
        cached-response (when cache?
                          (some-> cache* deref (get hash)))
        with-response (if cached-response
                        (assoc req-map :response cached-response)
                        (request-sync! req-map http-impl))]
    (when (and cache? (not cached-response))
      (swap! cache* assoc hash (:response with-response)))
    (handle-response with-response)))                                     ; parse and return the response

(defrecord Http [defaults config http-impl]
  ;; There are two arities of the req! protocol method to allow for more specific DSLs
  ;; Component starts with a default request map
  ;; This default map can be overridden on a per-request basis

  http-client/HttpClient
  (req! [this req-map]
    (http-client/req! this defaults req-map))
  (req! [this default-req-map req-map]
    (let [bookmarks (p.config/get! config [:http :bookmarks])
          request (->> (sanitized-req-map req-map bookmarks)
                       (render-req default-req-map))]
      (do-req-resp! request this)))

  component/Lifecycle
  (start
    [this]
    (assoc this :cache* (atom {})))

  (stop
    [{:keys [cache*] :as this}]
    (reset! cache* nil)
    (dissoc this :cache*))

  Object
  (toString [_] component-name))

(defmethod print-method Http [_ ^Writer w]
  (.write w component-name))

(def json-headers
  {"Content-Type"    "application/json; charset=utf-8"
   "Accept-Encoding" "gzip, deflate"})

(def html-headers
  (merge json-headers
         {"Content-Type" "text/html; charset=utf-8"}))

(def json-defaults
  {:method           :get
   :user-agent       "http-kit / your org"
   :headers          json-headers
   :serialize        serialization/write-json
   :deserialize      serialization/read-json
   :timeout          30000                                  ; 30 second timeout
   :keepalive        120000                                 ; 120 second keepalive
   :follow-redirects false
   :insecure?        true                                   ; TODO: FIX THIS
   :as               :text
   :cache?           false})

(def html-defaults
  (merge json-defaults
         {:headers   html-headers
          :serialize str}))

(defn new-http
  ([] (new-http json-defaults))
  ([defaults] (map->Http {:defaults  defaults})))
