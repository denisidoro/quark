(ns quark.dev.debug)

(print "DEBUG DEV NAMESPACE IMPORTED")

(defn tap
  [& args]
  (println "------------<br>") (print args) (println "<br>------------<br>") (last args))

(def no-tap
  identity)
