(ns quark.conversion.data-test
  #_(:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.conversion.data :as n]))

#_(t/deftest should-run
  (t/is (= (n/edn-str->edn "{:a 42}") {:a 42})))
