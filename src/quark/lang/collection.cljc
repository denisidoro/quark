(ns quark.lang.collection
  (:require [clojure.pprint :refer [print-table]]
            [clojure.walk :as walk]
            [quark.conversion.data :as conversion]))

(defn find-first [pred coll] (first (filter pred coll)))

(defn filter-keys [fun coll] (into {} (filter (fn [[k v]] (fun k)) coll)))
(defn filter-vals [fun coll] (into {} (filter (fn [[k v]] (fun v)) coll)))

(defn map-keys [f m] (into {} (for [[k v] m] [(f k) v])))
(defn map-vals [f m] (into {} (for [[k v] m] [k (f v)])))

(defn keep-at-least [i seq]
  (let [c                    (count seq)
        will-have-in-the-end (min c i)]
    (drop (- c will-have-in-the-end) seq)))

(defn throw-exception
  [^String message]
  (throw (#?(:clj IllegalArgumentException.
             :cljs js/Error.)
           message)))

(defn assoc-if
  "Assoc[iate] only truthy values."
  ([m k v]
   (-> m (cond-> v (assoc k v))))
  ([m k v & kvs]
   (let [ret (assoc-if m k v)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw-exception "assoc-if expects even number of arguments after map/vector, found odd number"))
       ret))))

(defn assoc-some
  "Assoc[iate] if the value is not nil."
  ([m k v]
   (if (nil? v) m (assoc m k v)))
  ([m k v & kvs]
   (let [ret (assoc-some m k v)]
     (if kvs
       (if (next kvs)
         (recur ret (first kvs) (second kvs) (nnext kvs))
         (throw-exception "assoc-some expects even number of arguments after map/vector, found odd number"))
       ret))))

(defn assoc-in-if [m ks v]
  "Associates a truthy value in a nested associative structure"
  (-> m (cond-> v (assoc-in ks v))))

(defn assoc-in-some [m ks v]
  "Associates a value in a nested associative structure,
  if the value is not nil"
  (if (nil? v) m (assoc-in m ks v)))

(defn dissoc-in [m key-vec]
  (let [firsts (vec (butlast key-vec))
        node   (dissoc (get-in m firsts) (last key-vec))]
    (assoc-in-if m firsts node)))

(defn dissoc-if [m k pred]
  (cond-> m (pred (get m k)) (dissoc k)))

(defn dissoc-in-if [m ks pred]
  (cond-> m (pred (get-in m ks)) (dissoc-in ks)))

(defn single-result! [coll]
  (let [c (count coll)]
    (cond
      (= c 1) (first coll)
      (zero? c) (throw (ex-info "Invalid Input"
                                {:type    :invalid-input
                                 :details {:reason "Empty collection"
                                           :count  c}}))
      :else (throw (ex-info "Invalid Input"
                            {:type    :invalid-input
                             :details {:reason (str "Collection has " c " elements")
                                       :count  c}})))))

(defn index-by [f coll]
  "Returns a map with elements indexed by an index function. Throws exception if value is not unique for index."
  (->> coll
       (group-by f)
       (map-vals single-result!)))

(defn iopmap [f coll]
  "Like pmap, but appropriate for blocking IO tasks (e.g. network calls). As opposed to pmap, it's not semi-lazy."
  (->> coll
       (mapv #(future (f %)))
       (mapv deref)))

(defn- contains-in? [m ks]
  (not= ::absent (get-in m ks ::absent)))

(defn update-in-if [m ks f & args]
  (if (contains-in? m ks)
    (apply (partial update-in m ks f) args)
    m))

(defn conj-array-map [amap kv]
  (apply array-map (concat (apply concat (seq amap)) kv)))

(defn cons-array-map [amap kv]
  (apply array-map (concat kv (apply concat (seq amap)))))

(defn- match-key [k [group-key _]] (= k group-key))

(defn- add [k v groups]
  (let [existing-group (find-first #(match-key k %) groups)]
    (if existing-group
      (let [[k existing-elts] existing-group
            extended-group [k (conj existing-elts v)]]
        (replace {existing-group extended-group} groups))
      (conj groups [k [v]]))))

(defn vec-group-by [f input]
  (reduce (fn [acc x] (add (f x) x acc)) [] input))

(defn vec-remove
  [v pos]
  (into (subvec v 0 pos) (subvec v (inc pos))))

(defn max-by
  "Returns the x for which (k x), *not necessarily a number*, is greatest, according to `compare`."
  [k coll]
  (reduce
    (fn [x y]
      (if (pos? (compare (k x) (k y)))
        x
        y))
    coll))

(defn min-by
  "Returns the x for which (k x), *not necessarily a number*, is greatest, according to `compare`."
  [k coll]
  (reduce
    (fn [x y]
      (if (neg? (compare (k x) (k y)))
        x
        y))
    coll))

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

(defn in-range? [content from to]
  (<= from (count content) to))

(defn vecs->map [vecs]
  (into {}
        (map (fn [[key value]]
               [key (if (sequential? value) (vecs->map value) value)])
             vecs)))

(defn depth-map-keys [func m]
  "Apply `func` to all keys from `m`"
  (let [f (fn [[k v]] (if (keyword? k) [(func k) v] [k v]))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn indices [f coll] (keep-indexed #(when (f %2) %1) coll))

(defn find-first [pred coll] (first (filter pred coll)))

(defn deep-merge
  "Recursively merges maps.
   If the first parameter is a keyword it tells the strategy to
   use when merging non-map collections. Options are
   - :replace, the default, the last value is used
   - :into, if the value in every map is a collection they are concatenated
     using into. Thus the type of (first) value is maintained."
  {:arglists '([strategy & values] [values])}
  [& values]
  (let [[values strategy] (if (keyword? (first values))
                            [(rest values) (first values)]
                            [values :replace])]
    (cond (every? map? values)
          (apply merge-with (partial deep-merge strategy) values)
          (and (= strategy :into) (every? coll? values)) (reduce into values)
          :else (last values))))

(defn safe-get
  [coll key & args]
  (if (coll? key) (apply get-in coll (vec key) args) (apply get coll key args)))

(defn pull-key
  "Pull some key"
  [x key]
  (-> (dissoc x key)
      (merge (get x key))))

(defn namespaced-keys
  "Set the namespace of all map keys (non recursive)."
  [e ns]
  (map-keys #(namespaced % ns) e))

(defn pull-namespaced-key
  "Pull some key, updating the namespaces of it"
  [x key ns]
  (-> (dissoc x key)
      (merge (namespaced-keys (get x key) ns))))

(defn namespaced-in [x path ns]
  (update-in-if x path #(namespaced % ns)))

(defn namespaced-keys-in [x path ns]
  (update-in-if x path #(namespaced-keys % ns)))

(defn like
  [type-coll coll]
  (cond
    (vector? type-coll) (vec coll)
    (set? type-coll) (into (empty type-coll) coll)
    :else coll))

(defn ^:private transform-keeping-type
  [fn & args]
  (like (last args) (apply fn args)))

(def mapt (partial transform-keeping-type map))
(def keept (partial transform-keeping-type keep))
(def removet (partial transform-keeping-type remove))
(def filtert (partial transform-keeping-type filter))

(defn into!
  "Transient version of clojure.core/into"
  [to from]
  (reduce conj! to from))

(defn update!
  "Transient version of clojure.core/update"
  [m k f x]
  (assoc! m k (f (get m k) x)))

(defn seek
  [pred s]
  (some #(when (pred %) %) s))

(defn leaf
  [m]
  (if (map? m)
    (recur ((-> m keys first) m))
    m))
