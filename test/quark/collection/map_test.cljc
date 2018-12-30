(ns quark.collection.map-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.collection.map :as coll.map]))

(t/deftest map-keys
  (t/are [input output]
    (= output (coll.map/map-keys inc input))
    {1 :a 3 :b}     {2 :a 4 :b}
    {1 :a 2 :b}     {2 :a 3 :b}))
