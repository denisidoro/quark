(ns quark.math.base-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.math.base :as math.base]))

(def alphabet10
  (math.base/alphabet 10))

(def alphabet16
  (math.base/alphabet 16))

(def alphabet62
  (math.base/alphabet 62))

(t/deftest alphabet
  (t/is (= "0123456789" alphabet10))
  (t/is (= "0123456789abcdef" alphabet16)))

(t/deftest decode
  (t/are [s alphabet output]
    (= (math.base/decode s alphabet) output)
    9    alphabet10  9
    "9"  alphabet10  9
    10   alphabet10  10
    15   alphabet10  15
    "a"  alphabet16  10
    15   alphabet16  21
    "f"  alphabet62  15
    "Z"  alphabet62  61
    "Z"  alphabet62  61
    "ZZ" alphabet62  (+ 61 (* 61 62))))

(t/deftest encode
  (t/are [i alphabet output]
    (= (math.base/encode i alphabet) output)
    9    alphabet10  "9"
    10   alphabet10  "10"
    15   alphabet10  "15"
    15   alphabet16  "f"))
