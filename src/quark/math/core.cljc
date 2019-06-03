(ns quark.math.core)

(defn pow
  [x n]
  #?(:cljs (.pow js/Math x n)
     :clj (Math/pow x n)))
