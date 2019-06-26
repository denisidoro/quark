(ns quark.string.core
  (:require [clojure.walk :as walk]
            [clojure.string :as str]))

(defn ^:private replace-char
  ;; Replaces the from character with the to character in s, which can be a
  ;; String or a Keyword
  ;; Does nothing if s is a keyword that is in the exception set
  [s from to exceptions]
  (if (contains? exceptions s) s (keyword (str/replace (name s) from to))))

(def underscore->dash-exceptions #{})

(defn ^:private replace-char-gen
  ;; Will replace dashes with underscores or underscores with dashes for the
  ;; keywords in a map
  ;; Ignores String values in a map (both keys and values)
  ([from to] (replace-char-gen from to #{}))
  ([from to exceptions]
   #(if (keyword? %) (replace-char % from to exceptions) %)))

(defn dash->underscore
  [json-doc]
  (walk/postwalk (replace-char-gen \- \_) json-doc))

(defn underscore->dash
  [json-doc]
  (walk/postwalk (replace-char-gen \_ \- underscore->dash-exceptions) json-doc))

(def readers {})

(defn any->str
  [s]
  (cond (string? s) s
        (keyword? s) (name s)
        :else (str s)))

;; http://www.matt-reid.co.uk/blog_post.php?id=69
(defn normalize
  [txt]
  (-> txt
      (java.text.Normalizer/normalize java.text.Normalizer$Form/NFD)
      (str/replace #"\p{InCombiningDiacriticalMarks}+" "")))
