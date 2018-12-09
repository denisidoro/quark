(ns quark.macro.lib-test
(:require [quark.macro.lib :refer (h1 h2)]
          [#?(:clj clojure.test :cljs cljs.test) :as t]))

(t/deftest should-run
  (t/is (= (h1 {:b 1} "hello") {:a 42})))

