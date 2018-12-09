(ns quark.spec.schema-test
  (:require [#?(:clj  clojure.test
                :cljs cljs.test) :as t]
            [quark.spec.schema :as s]))

(s/defn foo
  [a :- s/Int
   b :- s/Int]
  (+ a b))

(t/deftest should-run
  (t/testing "this should run"
    (t/is (= 4 (macroexpand-1 '(s/defn foo
                                 [a :- s/Int
                                  b :- s/Int]
                                 (+ a b)))))))
