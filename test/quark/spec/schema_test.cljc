(ns quark.spec.schema-test
  (:require [#?(:clj  clojure.test
                :cljs cljs.test) :as t]
            [quark.spec.schema :as s]))

(t/deftest should-run
  (t/testing "this should run"
    (t/is (s/Keyword :foo))))
