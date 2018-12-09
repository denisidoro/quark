(ns quark.conversion.data
  (:require [quark.lang.string :as string]
            #?@(:clj  [[cheshire.core :as json]
                       [clojure.edn :as edn]]
                :cljs [[cljs.tools.reader :as reader]])))

(def readers {})

(def read-str
  #?(:cljs reader/read-string
     :clj  edn/read-string))

(defn edn-str->edn
  [edn-str]
  (read-str {:readers readers} edn-str))

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
