(ns quark.spec.schema-test
  #?(:cljs (:require-macros [quark.spec.schema]))
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.spec.schema :as s]))

(s/defn foo :- any?
  [a b :- string?]
  (+ a b))

(t/deftest defn
  (t/is (= 4 (foo 1 3))))
