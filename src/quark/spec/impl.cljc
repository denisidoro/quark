(ns quark.spec.impl
  (:require #?@(:clj [[clojure.spec.alpha :as s]]
                :cljs [[cljs.spec.alpha :as s]])
            [quark.spec.defn :as s.defn]))

(s/def ::string string?)

(defn arg->keyword
  [arg arg-type arg-count]
  (if (= :sym arg-type) (keyword arg) (keyword (str "arg-" arg-count))))

(defn extract-cat-arguments
  ([conformed-args] (extract-cat-arguments conformed-args -1 false []))
  ([[[arg-type arg] & other-args] arg-count waiting-for-spec? arg-vector]
   (case arg-type
     nil (if waiting-for-spec? (conj arg-vector 'any?) arg-vector)
     :spec (recur other-args arg-count false (conj arg-vector (:spec arg)))
     (:sym :map :seq)
     (if waiting-for-spec?
       (recur other-args
              (inc arg-count)
              true
              (conj arg-vector 'any? (arg->keyword arg arg-type arg-count)))
       (recur other-args
              (inc arg-count)
              true
              (conj arg-vector (arg->keyword arg arg-type arg-count))))
     (recur other-args (inc arg-count) false (conj arg-vector arg)))))

(defn update-conf
  [{[arity] :bs, :as conf} b-update-fn]
  (case arity
    :arity-1 (update-in conf [:bs 1] b-update-fn)
    :arity-n (update-in conf [:bs 1 :bodies] #(map b-update-fn %))))

(defn remove-schema-arguments-from-body
  [body]
  (update-in body
             [:args :args]
             (fn [args]
               (remove #(-> %
                            first
                            #{:spec})
                       args))))

(defn build-cat
  [body]
  (let [args-specs (-> body
                       (get-in [:args :args])
                       extract-cat-arguments)
        varargs? (-> body
                     (get-in [:args :varargs :form])
                     seq)
        varargs-specs (if varargs? [:kwargs 'any?] [])]
    (concat args-specs varargs-specs)))

(defn build-multi-arity-args
  [cats]
  (apply concat
         (map-indexed (fn [index cat]
                        `(~(keyword (str "arity-" index))
                          (s/cat ~@cat)))
                      cats)))

(def ^:private ^:const defn-args :quark.spec.defn/defn-args)

(defn sdefn
  [& args]
  {:style/indent 1}
  (let [{:keys [name], :as conf} (s/conform defn-args args)
        arity (get-in conf [:bs 0])
        bodies (case arity
                 :arity-1 [(get-in conf [:bs 1])]
                 :arity-n (get-in conf [:bs 1 :bodies]))
        cats (map build-cat bodies)
        multi-arity? (> (count cats) 1)
        fdef-args (if multi-arity?
                    `(s/alt ~@(build-multi-arity-args cats))
                    `(s/cat ~@(first cats)))
        return-spec (-> conf
                        :spec
                        :spec
                        (or 'any?))
        new-conf (dissoc (update-conf conf remove-schema-arguments-from-body)
                         :spec)
        new-args (s/unform defn-args new-conf)]
    `(do ~(cons `clojure.core/defn new-args)
         (s/fdef ~name
           :args ~fdef-args
           :ret ~return-spec))))

(defn either-args
  [schemas]
  (->> schemas
       (map-indexed (fn [i s] [(keyword (str "s" i)) s]))
       flatten))
