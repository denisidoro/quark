(ns quark.lang.collection-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.lang.collection :as coll]))

(t/deftest map-keys
  (t/are [input output]
    (= output (coll/map-keys inc input))
    {1 :a 3 :b}     {2 :a 4 :b}
    {1 :a 2 :b}     {2 :a 3 :b}))
