(ns quark.math.core)

(defn pow
  [x n]
  #?(:cljs (.pow js/Math x n)
     :clj (Math/pow x n)))

(defn linear-interpolation
  [x0 y0 x1 y1 x2]
  (+ y1 (* (/ (- y1 y0) (- x1 x0)) (- x2 x1))))

#?(:clj
   (defn ^:private to-str [^Integer n ^Integer to-base] (.toString n to-base))
   :cljs (defn ^:private to-str [n to-base] (.toString n to-base)))

(defn from-radix
  [value radix]
  #?(:cljs (js/parseInt value radix)
     :clj (BigInteger. value radix)))

(defn to-radix
  ([value to-base]
   (-> value
       str
       (from-radix 10)
       (to-str to-base)))
  ([value from-base to-base]
   (-> value
       (from-radix from-base)
       (to-radix to-base))))
