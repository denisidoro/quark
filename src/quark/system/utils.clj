(ns quark.system.utils
  (:require [com.stuartsierra.component :as component]
            [quark.system.options :as options])
  (:import [clojure.lang ExceptionInfo]))

(def system* (atom nil))

(defn ^:private quiet-start
  [system]
  (try
    (component/start system)
    (catch ExceptionInfo e
      (throw (or (.getCause e) e)))))

(defn ^:private start-system!
  [system]
  (reset! system* (quiet-start system)))

(defn get-component
  [component-name]
  (some-> system*
          deref
          (get component-name)))

(defn get-component!
  [component-name]
  (or (get-component component-name)
      (throw (ex-info "Component not found"
                      {:from      ::get-component!
                       :component component-name
                       :reason    "Unknown component"}))))

(defn stop-components!
  []
  (component/stop system*)
  (reset! system* nil))

(defn clear-components!
  []
  (reset! system* nil))

(defn stop-system!
  []
  (stop-components!)
  (shutdown-agents))

(defn ^:private best-environment
  [m env]
  (or (and env (get m env))
      (get m :base)))

(defn create-and-start-system!
  [{:keys [modules environment] :as options}]
  (let [reduced-options (options/build modules)
        options+ (-> options (merge reduced-options) (dissoc :modules :system))]
    (->> modules
         (keep #(-> % :system :builder-map (best-environment environment)))
         (map (fn [builder] (builder options+)))
         (reduce merge)
         start-system!)))

(defn ensure-system-up!
  [options]
  (or @system*
      (create-and-start-system! options)))

(defn stop-system!
  []
  (and @system*
       (stop-components!)))

(defn restart-system!
  [options]
  (stop-system!)
  (ensure-system-up! options))

