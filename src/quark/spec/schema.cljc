(ns quark.spec.schema
  (:refer-clojure :exclude [defn])
  (:require [quark.spec.impl :as impl]
            #?@(:clj  [[clojure.spec.alpha :as s]]
                :cljs [[cljs.spec.alpha :as s]])))

(def Int integer?)
(def Num number?)
(def Str string?)
(def Keyword keyword?)
(def Any any?)
(def Bool boolean?)

(def Char #?(:clj Character :cljs Str))

(defmacro defn
  [& args]
  (apply impl/sdefn args))

(defmacro either
  [& schemas]
  (let [args (impl/either-args schemas)]
    `(or ~@args)))

(defmacro maybe
  [& schemas]
  `(nilable ~@schemas))

(def pred
  identity)

(defmacro constrained
  [& schemas]
  `(s/and ~@schemas))

(defn protocol
  [p]
  #(satisfies? p %))
