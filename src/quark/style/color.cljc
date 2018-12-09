(ns quark.style.color
  (:require [quark.math.core :as math]))

(defn hex->rgb
  [hex]
  (->> (subs hex 1)
       (partition 2)
       (map #(-> (apply str %)
                 (math/to-radix 16 10)))))
