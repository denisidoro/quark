;; based on https://eddmann.com/posts/binary-search-trees-in-clojure/
(ns quark.tree.bst
  (:refer-clojure :exclude [remove min max contains? count])
  (:require [quark.tree.core :as tree])
  (:import (java.io Writer)))

(defn ^:private as-string
  [el left right]
  (str (filterv identity [el left right])))

(defn min
  [{:keys [el left]}]
  (if left
    (recur left)
    el))

(defn max
  [{:keys [el right]}]
  (if right
    (recur right)
    el))

(defn ^:private contains?
  [{:keys [el left right] :as tree} value]
  (cond
    (nil? tree) false
    (< value el) (recur left value)
    (> value el) (recur right value)
    :else true))

(defn ^:private count
  [{:keys [left right] :as tree}]
  (if tree
    (+ 1 (count left) (count right))
    0))

(defn height
  ([tree] (height tree 0))
  ([tree count]
   (if tree
     (clojure.core/max
       (height (:left tree) (inc count))
       (height (:right tree) (inc count)))
     count)))

(defn ^:private value
  [node]
  (:el node))

(defn ^:private children
  [{:keys [left right]}]
  [left right])

(defn bst?
  ([tree] (bst? tree Integer/MIN_VALUE Integer/MAX_VALUE))
  ([{:keys [el left right] :as tree} min max]
   (cond
     (nil? tree) true
     (or (< el min) (> el max)) false
     :else (and (bst? left min (dec el))
                (bst? right (inc el) max)))))

(defrecord Node [el left right]

  tree/Node
  (count [this] (count this))
  (neighbors [this] (children this))
  (value [this] (value this))
  (contains? [this value] (contains? this value))

  Object
  (toString [_]
    (as-string el left right)))

(defmethod print-method Node
  [{:keys [el left right]}
   ^Writer w]
  (.write w ^String (as-string el left right)))

(defn insert
  [{:keys [el left right] :as tree} value]
  (cond
    (nil? tree) (Node. value nil nil)
    (< value el) (Node. el (insert left value) right)
    (> value el) (Node. el left (insert right value))
    :else tree))

(defn remove
  [{:keys [el left right] :as tree} value]
  (cond
    (nil? tree) nil
    (< value el) (Node. el (remove left value) right)
    (> value el) (Node. el left (remove right value))
    (nil? left) right
    (nil? right) left
    :else (let [min-value (clojure.core/min right)]
            (Node. min-value left (remove right min-value)))))

(defn from-vec
  [[v left right]]
  (Node.
    v
    (some-> left from-vec)
    (some-> right from-vec)))
