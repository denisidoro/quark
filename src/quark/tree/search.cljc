;; based on http://dnaeon.github.io/graphs-and-clojure/
(ns quark.tree.search
  (:require [quark.tree.core :as tree]))

(defn ^:private visited?
  "Predicate which returns true if the node v has been visited already, false otherwise."
  [v coll]
  (some #(= % v) coll))

(defn depth-first-search
  "Traverses a graph in Depth First Search (DFS)"
  [node]
  (->> (loop [stack   (vector node)                         ;; Use a stack to store nodes we need to explore
              visited []]                                   ;; A vector to store the sequence of visited nodes
         (if (empty? stack)                                 ;; Base case - return visited nodes if the stack is empty
           visited
           (let [v           (peek stack)
                 neighbors   (if v (tree/neighbors v) [])
                 not-visited (remove #(visited? % visited) neighbors)
                 new-stack   (into (pop stack) not-visited)]
             (if (visited? v visited)
               (recur new-stack visited)
               (recur new-stack (conj visited v))))))
       (keep #(some-> % tree/value))))

(defn breadth-first-search
  "Traverses a graph in Breadth First Search (BFS)."
  [node]
  (->> (loop [queue   (conj clojure.lang.PersistentQueue/EMPTY node) ;; Use a queue to store the nodes we need to explore
              visited []]                                   ;; A vector to store the sequence of visited nodes
         (if (empty? queue)
           visited                                          ;; Base case - return visited nodes if the queue is empty
           (let [v           (peek queue)
                 neighbors   (if v (tree/neighbors v) [])
                 not-visited (remove #(visited? % visited) neighbors)
                 new-queue   (apply conj (pop queue) not-visited)]
             (if (visited? v visited)
               (recur new-queue visited)
               (recur new-queue (conj visited v))))))
       (keep #(some-> % tree/value))))
