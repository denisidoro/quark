(ns quark.system.options
  (:require [quark.collection.map :as map]))

(defmulti transform (fn [k _] k))

(defmethod transform :default
  [_ vs]
  (last vs))

(defn build
  [options-vec]
  (let [keyset (->> options-vec (mapcat keys) set)]
    (->> keyset
         (map (fn [k] [k (->> options-vec (keep #(get % k)) (transform k))]))
         (into {})
         (map/filter-vals identity))))

