(ns quark.spec.schema-test
  #?(:cljs (:require-macros [quark.spec.schema]))
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.spec.schema :as s]))

(s/defn foo :- any?
  [a b :- string?]
  (+ a b))

(t/is (= 4 (foo 1 3)))

(t/deftest should-run
  (t/testing "this should run"
    (t/is (= 4 3))))
