(ns quark.math.interpolation)

(defn linear-interpolation
  [x0 y0 x1 y1 x2]
  (+ y1 (* (/ (- y1 y0)
              (- x1 x0))
           (- x2 x1))))
