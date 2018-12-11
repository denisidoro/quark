(ns quark.conversion.data-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.conversion.data :as n]))

(t/deftest edn-str->edn
  (t/is (= (n/edn-str->edn "{:a 42}") {:a 42})))
