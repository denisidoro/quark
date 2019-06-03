(ns quark.style.color
  (:require [quark.math.base :as math.base]
            [quark.conversion.data :as data]
            [clojure.string :as str]))

(defn hex->rgb
  [hex]
  (->> (subs hex 1)
       (partition 2)
       (map #(-> (apply str %)
                 str/lower-case
                 (math.base/decode (math.base/alphabet 16))
                 data/str->int))))
