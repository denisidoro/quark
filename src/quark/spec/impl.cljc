(ns quark.spec.impl)

(defn either-args
  [schemas]
  (->> schemas
       (map-indexed (fn [i s] [(keyword (str "s" i)) s]))
       flatten))
