(ns quark.collection.seq)

(defn find-first [pred coll] (first (filter pred coll)))

(defn indices [f coll]
  (keep-indexed #(when (f %2) %1) coll))

(defn first-index
  [f coll]
  (first (indices f coll)))

(defn ^:private match-key [k [group-key _]] (= k group-key))

(defn ^:private add [k v groups]
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

(defn in-range? [content from to]
  (<= from (count content) to))

(defn vecs->map [vecs]
  (into {}
        (map (fn [[key value]]
               [key (if (sequential? value) (vecs->map value) value)])
             vecs)))

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

(defn set-by
  [f xs]
  (->> xs
       (sort-by f)
       (partition-by f)
       (map first)
       set))
