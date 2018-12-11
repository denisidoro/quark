(ns quark.aux.run
  (:require [#?(:clj clojure.test :cljs cljs.test) :as t]))

(defmacro fact
  ([actual comparison expected]
    `(fact nil ~actual ~comparison ~expected))
  ([doc actual comparison expected]
   (if (ifn? expected)
     `(t/is (~expected ~actual) ~doc)
     `(t/is (= ~actual ~expected) ~doc))))
