(ns quark.crypto.core)

(defn offset
  [off text]
  (->> text
       (map int)
       (map #(+ off %))
       (map char)
       (apply str)))
