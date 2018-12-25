(ns quark.navigation.core-test
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]
            [quark.aux.checkers :as ch]
            [quark.navigation.core :as nav]))

(t/deftest as-map-recursive
  (t/are [input output]
         (= output (nav/as-map-recursive input))
    {1 :a 3 :b}                   {1 :a 3 :b}
    {:a {:b [:c :d]}}             {:a {:b {0 :c 1 :d}}}
    {:a {:b #{:c :d}}}            {:a {:b {0 :c 1 :d}}}
    {:a {:b '(:c :d)}}            {:a {:b {0 :c 1 :d}}}
    {:a {:b '({:x :y} :d)}}       {:a {:b {0 {:x :y} 1 :d}}}))

(t/deftest paths
  (t/are [input output]
         (ch/coll= output (nav/paths input))
    {:a 1}                  '([:a])
    {:a 1 :b 2}             '([:a] [:b])
    {:a 1 :b {:c :d}}       '([:a] [:b] [:b :c])
    {:a #{:c :d}}           '([:a] [:a 0] [:a 1])
    {:a #{{:c :d}}}         '([:a] [:a 0] [:a 0 :c])
    [:a :b :c]              '([0] [1] [2])
    [#{{:a '(:b)}}]         '([0] [0 0] [0 0 :a] [0 0 :a 0])))

(t/deftest navigate
  (t/are [m path output]
         (= output (nav/navigate m path))
    {1 :a 3 :b}                   [1]              :a
    {:a {:b [:c :d]}}             [:a :b 1]        :d
    {:a {:b #{:c :d}}}            [:a :b 1]        :d
    {:a {:b '(:c :d)}}            [:a :b 1]        :d
    {:a {:b '({:x :y} :d)}}       [:a :b 0 :x]     :y
    [#{{:a '(:b)}}]               [0 0 :a 0]       :b))
