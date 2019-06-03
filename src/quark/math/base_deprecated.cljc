(ns quark.math.base-deprecated)

#?(:clj  (defn ^:private to-str [^Integer n ^Integer to-base] (Integer/toString n to-base))
   :cljs (defn ^:private to-str [n to-base] (.toString n to-base)))

(defn from-radix
  [value radix]
  #?(:clj  (BigInteger. value radix)
     :cljs (js/parseInt value radix)))

(defn to-radix
  ([value to-base]
   (to-radix value 10 to-base))
  ([value from-base to-base]
   (-> value
       str
       (from-radix from-base)
       (to-str to-base))))
