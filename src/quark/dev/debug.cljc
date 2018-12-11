(ns quark.dev.debug)

(print "DEBUG DEV NAMESPACE IMPORTED")

(defn tap
  [& args]
  (print args)
  (last args))

(def no-tap
  identity)

(def language
  #?(:clj  "clj"
     :cljs "cljs"))
