(ns quark.navigation.core
  (:require [clojure.walk :as walk]))

(defn as-map
  [x]
  (if (map? x)
    x
    (->> x
         vec
         (map-indexed (fn [index item] [index item]))
         (into {}))))

(defn as-map-recursive
  [x]
  (walk/prewalk
    #(if (and (not (map-entry? %))
              (or (vector? %)
                  (set? %)
                  (list? %)))
       (as-map %)
       %)
    x))

(defn navigate
  [m path]
  (loop [m' m
         [x & xs] path]
    (if-not x
      m'
      (let [m''    (if-not (map? m')
                     (as-map m')
                     m')
            next-m (get m'' x)]
        (recur next-m xs)))))

(def ^:private conj*
  (fnil conj []))

(defn ^:private path-seq*
  [form path]
  (condp #(%1 %2) form

    (some-fn list? set? vector?)
    (->> (map-indexed
           (fn [idx item]
             (path-seq* item (conj* path idx)))
           form)
         (mapcat identity))

    map?
    (->> (map
           (fn [[k v]]
             (path-seq* v (conj* path k)))
           form)
         (mapcat identity))

    [[form path]]))

(defn path-seq
  [form]
  (->> (path-seq* form nil)
       (map #(let [[form path] %]
               {:path path :form form}))
       dedupe))

(defn explode*
  [path]
  (loop [p   path
         acc [path]]
      (let [p' (pop p)]
        (if (seq p')
          (recur p' (conj acc p'))
          acc))))

(defn paths
  [form]
  (->> form
       path-seq
       (map :path)
       (mapcat explode*)
       set
       vec))
