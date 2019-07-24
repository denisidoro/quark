(ns quark.graph.protocols.pathom
  (:require [quark.system.options :as options]
            [schema.core :as s]))

(defprotocol Pathom
  (query [component eql] [component eql options]))

(s/defschema IPathom
  (s/protocol Pathom))

(defmethod options/transform :pathom
  [_ vs]
  {:resolvers
   (->> vs
        (keep :resolvers)
        (reduce into []))

   :deps
   (->> vs
        (keep :deps)
        (reduce into [])
        set
        vec)})

