(ns quark.conversion.data
  (:require [quark.string.core :as string]
            #?@(:clj  [[cheshire.core :as json]
                       [clojure.edn :as edn]]
                :cljs [[cljs.reader :as reader]])))

(defn default-data-reader
  [reader & values]
  [(str "#" reader) (apply str values)])

(def default-options
  {:default default-data-reader})

(def read-str
  #?(:cljs reader/read-string
     :clj  edn/read-string))

(defn edn-str->edn
  ([edn-str] (edn-str->edn default-options edn-str))
  ([options edn-str]
   (read-str options edn-str)))

(defn str->uuid
  [id-str]
  (read-str (str "#uuid \"" id-str "\"")))

(def any->str
  string/any->str)

(defn any->edn-str
  [value]
  (with-out-str (pr value)))

(defn edn->json
  [data]
  #?(:cljs (.stringify js/JSON (clj->js data))
     :clj  (-> data
               string/dash->underscore
               json/generate-string)))

#?(:cljs
   (defn js->edn [js] (js->clj js :keywordize-keys true)))

(defn json->edn
  [json]
  (when-not (= json "undefined")
    #?(:cljs (try (js->edn (.parse js/JSON json)) (catch js/Error _ nil))
       :clj  (-> json
                 string/underscore->dash
                 (json/parse-string true)))))

(defn str->int
  [x]
  #?(:clj  (-> x bigint int)
     :cljs (js/parseInt x)))
