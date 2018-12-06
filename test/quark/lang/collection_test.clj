(ns quark.lang.collection-test
  (:require [midje.sweet :refer :all]
            [quark.lang.collection :as coll]))

(defn ^:private same?
  [expected]
  (fn [actual]
    (and (= expected actual)
         (= (type expected) (type actual)))))

(tabular
  (fact "mapt"
    (coll/mapt inc ?in) => (same? ?out))
  ?in           ?out
  []            []
  [1 2 3]       [2 3 4]
  #{1 2 3}      #{2 3 4}
  '(1 2 3)      '(2 3 4))

(tabular
  (fact "filtert"
    (coll/filtert odd? ?in) => (same? ?out))
  ?in           ?out
  []            []
  [1 2 3]       [1 3]
  #{1 2 3}      #{1 3}
  '(1 2 3)      '(1 3))

(tabular
  (fact "removet"
    (coll/removet even? ?in) => (same? ?out))
  ?in           ?out
  []            []
  [1 2 3]       [1 3]
  #{1 2 3}      #{1 3}
  '(1 2 3)      '(1 3))

(tabular
  (fact "keept"
    (coll/keept odd? ?in) => (same? ?out))
  ?in           ?out
  []            []
  [1 2 3]       [true false true]
  #{1 2 3}      #{false true}
  '(1 2 3)      '(true false true))
