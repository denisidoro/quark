(ns quark.style.css
  (:require [clojure.string :as str]
            [quark.style.color :as color]))

(defn hex->rgba
  [hex alpha]
  (str "rgba("
       (->> hex
            color/hex->rgb
            (str/join ", "))
       ","
       alpha
       ")"))
