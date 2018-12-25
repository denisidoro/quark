(ns quark.style.color-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.style.color :as color]))

(t/deftest hex->rgb
  (t/are [input output]
         (= output (color/hex->rgb input))
    "#000000"     [0 0 0]
    "#FFFFFF"     [255 255 255]))
