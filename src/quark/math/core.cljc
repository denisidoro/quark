(ns quark.math.core)

(defn pow
  [x n]
  #?(:cljs (.pow js/Math x n)
     :clj  (Math/pow x n)))

(defn digits
  [n]
  (loop [result (list)
         n      n]
    (if (pos? n)
      (recur (conj result (rem n 10))
             (quot n 10))
      result)))
