;; based on https://eddmann.com/posts/binary-search-trees-in-clojure/
(ns quark.tree.core
  (:refer-clojure :exclude [remove min max contains? count]))

(defprotocol Node
  (neighbors [this])
  (count [this])
  (contains? [this value])
  (value [this]))
