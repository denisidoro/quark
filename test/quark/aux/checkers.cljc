(ns quark.aux.checkers)

(defn coll=
  [x y]
  (if (or (list? x)
          (vector? x))
    (= (set x) (set y))
    (= x y)))
