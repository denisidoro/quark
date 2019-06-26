(ns quark.money.string
  (:require #?@(:cljs [[goog.string :as gstr]])))

(defn ^:private bigdec->str
  [x]
  #?(:cljs (gstr/format x "%0.0f")
     :clj (str x)))

(defn bigdec->pretty-reais
  [n]
  (->> n
       bigdec->str
       (str "R$" (char 160))))
