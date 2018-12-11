(ns quark.spec.schema
  (:refer-clojure :exclude [defn])
  (:require [quark.spec.impl :as impl]))

(defmacro defn
  [& args]
  (binding [impl/*cljs?* (-> &env :ns some?)]
    (apply impl/defn-spec-helper args)))

