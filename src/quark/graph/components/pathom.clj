(ns quark.graph.components.pathom
  (:require [clojure.core.async :as async]
            [com.stuartsierra.component :as component]
            [com.wsscode.pathom.connect :as pc]
            [com.wsscode.pathom.core :as p]
            [quark.graph.protocols.pathom :as p.pathom])
  (:import (java.io Writer)))

(def ^:private ^:const ^String component-name
  "<Pathom>")

(defn ^:private build-parser
  [{:keys [resolvers] :as this}]
  (p/parallel-parser
   {::p/env     {::p/reader               [p/map-reader
                                           pc/parallel-reader
                                           pc/open-ident-reader
                                           p/env-placeholder-reader]
                 ::p/placeholder-prefixes #{">"}
                 :components              this}
    ::p/mutate  pc/mutate-async
    ::p/plugins [(pc/connect-plugin {::pc/register resolvers})
                 p/error-handler-plugin
                 p/trace-plugin]}))

(defrecord Pathom [resolvers]

  component/Lifecycle
  (start
    [this]
    (assoc this :parser (build-parser this)))

  (stop
    [this]
    (dissoc this :parser))

  p.pathom/Pathom
  (query
    [{:keys [parser]} eql]
    (async/<!!
     (parser {} eql)))

  Object
  (toString
    [_]
    component-name))

(defmethod print-method Pathom
  [_ ^Writer w]
  (.write w component-name))

(defn new-pathom
  [resolvers]
  (map->Pathom {:resolvers  resolvers}))
