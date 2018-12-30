(ns quark.conversion.data-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.conversion.data :as n]))

(t/deftest edn-str->edn
  (t/are [input output]
    (= (n/edn-str->edn input) output)
    "{:a 42}"                                          {:a 42}
    "{:a {:b :c}}"                                     {:a {:b :c}}
    "{:a {:b [1 2]}}"                                  {:a {:b [1 2]}}
    "{:ns1/a :ns2/b}"                                  {:ns1/a :ns2/b}
    "{:a #inst \"2018-12-30T03:40:24.829-00:00\"}"     {:a #inst "2018-12-30T03:40:24.829-00:00"}))

(t/deftest json->edn
  (t/are [input output]
    (= (n/json->edn input) output)
    "{\"a\": 42}"            {:a 42}))

