(ns quark.math.base)

(def ^:private full-alphabet "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-/?!@#$%&*()[]+{}<>|~^")

(defn alphabet
  [length]
  (subs full-alphabet 0 length))

(defn decode
  [s alphabet]
  (reduce
    (fn [val c]
      (+ (* (count alphabet) val) (.indexOf alphabet (str c))))
    0
    (str s)))

(defn encode
  [i alphabet]
  (reduce
    #(str (nth alphabet (last %2)) %)
    ""
    (take-while
      #(not= [0 0] %)
      (rest
        (iterate
          (fn [[i _]]
            (let [x (count alphabet)]
              [(quot i x) (mod i x)]))
          [i 0])))))
