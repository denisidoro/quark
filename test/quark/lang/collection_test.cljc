(ns quark.lang.collection-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.aux.run :as r]))

(t/deftest should-run
  (t/is (= 1 3)))
