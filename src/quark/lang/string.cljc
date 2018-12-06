(ns quark.lang.string
  (:require [clojure.walk :as walk]
            [clojure.string :as str]
            [quark.spec.schema :as s]))

(def ^:private string-or-keyword (s/either s/Keyword s/Str))

(s/defn ^:private replace-char :- s/Keyword
  ;; Replaces the from character with the to character in s, which can be a
  ;; String or a Keyword
  ;; Does nothing if s is a keyword that is in the exception set
  [s :- string-or-keyword from :- s/Char to :- s/Char exceptions :-
   #{s/Keyword}]
  (if (contains? exceptions s) s (keyword (str/replace (name s) from to))))

(def underscore->dash-exceptions #{})

(s/defn ^:private replace-char-gen :- (s/pred fn?)
  ;; Will replace dashes with underscores or underscores with dashes for the
  ;; keywords in a map
  ;; Ignores String values in a map (both keys and values)
  ([from :- s/Char to :- s/Char] (replace-char-gen from to #{}))
  ([from :- s/Char to :- s/Char exceptions :- #{s/Keyword}]
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
