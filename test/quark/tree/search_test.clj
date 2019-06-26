(ns quark.tree.search-test
  (:require [clojure.test :as t]
            [quark.tree.bst :as bst]
            [quark.tree.search :as search]))

(def node
  (bst/from-vec [1
                 [2 [4 [6 nil nil] nil]]
                 [3 [5 [7 nil nil] [8 nil nil]]]]))

(t/deftest breadth-first-search
  (t/is (= [1 2 3 4 5 6 7 8] (search/breadth-first-search node))))

(t/deftest breadth-first-search
  (t/is (= [1 2 3 4 5 6 7 8] (search/breadth-first-search node))))
