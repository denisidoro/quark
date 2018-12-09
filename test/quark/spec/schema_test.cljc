(ns quark.spec.schema-test
  #?(:cljs (:require-macros [quark.spec.schema]))
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.spec.schema :as s]))

(s/defn foo any?
  [a any? b any?]
  (+ a b))

(t/deftest should-run
  (t/testing "this should run"
    (t/is (= 4 3))))
