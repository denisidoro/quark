(ns quark.macro.lib)

(defmacro h1 [props & children]
  `[:h1 ~props ~@children])

(defmacro h2 [props & children]
  `[:h2 ~props ~@children])
