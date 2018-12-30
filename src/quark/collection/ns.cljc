(ns quark.collection.ns
  (:require [quark.conversion.data :as conversion]
            [quark.collection.map :as coll.map]))

(defn namespaced [m ns]
  (let [ns-str (conversion/any->str ns)]
    (clojure.walk/postwalk
      (fn [x]
        (if (simple-keyword? x)
          (keyword (str ns-str "/" (name x)))
          x)) m)))

(defn unnamespaced [m]
  (clojure.walk/postwalk
    (fn [x]
      (if (keyword? x)
        (keyword (name x))
        x)) m))

(defn namespaced-keys
  "Set the namespace of all map keys (non recursive)."
  [e ns]
  (coll.map/map-keys #(namespaced % ns) e))

(defn pull-namespaced-key
  "Pull some key, updating the namespaces of it"
  [x key ns]
  (-> (dissoc x key)
      (merge (namespaced-keys (get x key) ns))))

(defn namespaced-in [x path ns]
  (coll.map/update-in-if x path #(namespaced % ns)))

(defn namespaced-keys-in [x path ns]
  (coll.map/update-in-if x path #(namespaced-keys % ns)))

(defn pull-namespaced-key
  "Pull some key, updating the namespaces of it"
  [x key ns]
  (-> (dissoc x key)
      (merge (namespaced-keys (get x key) ns))))
