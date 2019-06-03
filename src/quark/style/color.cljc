(ns quark.style.color
  (:require [quark.math.base :as math.base]
            [quark.conversion.data :as data]))

(defn hex->rgb
  [hex]
  (->> (subs hex 1)
       (partition 2)
       (map #(-> (apply str %)
                 (math.base/decode (math.base/alphabet 16))
                 data/str->int))))
