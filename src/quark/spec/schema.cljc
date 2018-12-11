(ns quark.spec.schema
  (:refer-clojure :exclude [defn Keyword])
  (:require [quark.spec.defn :as defn]
            [quark.spec.impl :as impl]
            [clojure.core :as core]))

(def Int integer?)
(def Num number?)
(def Str string?)
(def Keyword keyword?)
(def Any any?)
(def Bool boolean?)

(def Char #?(:clj Character :cljs Str))

(defmacro defn
  [& args]
  (binding [defn/*cljs?* (-> &env :ns some?)]
    (apply defn/defn-spec-helper args)))

(defmacro either
  [& schemas]
  (let [args (impl/either-args schemas)]
    (binding [defn/*cljs?* (-> &env :ns some?)]
      `(or ~@args))))

(defmacro maybe
  [& schemas]
  `(nilable ~@schemas))

(def pred
  identity)

(defmacro constrained
  [& schemas]
  (binding [defn/*cljs?* (-> &env :ns some?)]
    `(s/and ~@schemas)))

(core/defn protocol
  [p]
  #(satisfies? p %))
