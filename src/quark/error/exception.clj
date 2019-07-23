(ns quark.error.exception
  (:require [quark.lang.string :as string]))

(defn make-exception
  "Construct an exception for use with throw using ex-info.
   If a body key is included in details, its will be rendered to underscores.
   If no body key is included, a body key will be added using {:error title} (affects external HTTP responses).
   If a cause key is included in details, it will be used as the cause for the exception and removed from details."
  [title type {:keys [cause] :as details}]
  (let [details-map (update-in details [:body] #(or (string/dash->underscore %) {:error title}))]
    (if cause (ex-info title {:type type :details (dissoc details-map :cause)} cause)
        (ex-info title {:type type :details details-map}))))

(def make-service-unavailable (partial make-exception "Service Unavailable" :service-unavailable))

(defn terminate
  "Throw an internal format exception with a details map"
  [title type details]
  ;; e.g. for details: {:from ::function-name :reason "Because"}
  (throw (make-exception title type details)))

(def bad-request!                   (partial terminate "Bad Request"                   :bad-request))
(def invalid-input!                 (partial terminate "Invalid Input"                 :invalid-input))
(def unauthorized!                  (partial terminate "Unauthorized"                  :unauthorized))
(def payment-required!              (partial terminate "Payment Required"              :payment-required))
(def forbidden!                     (partial terminate "Forbidden"                     :forbidden))
(def not-found!                     (partial terminate "Not Found"                     :not-found))
(def method-not-allowed!            (partial terminate "Method Not Allowed"            :method-not-allowed))
(def not-acceptable!                (partial terminate "Not Acceptable"                :not-acceptable))
(def proxy-authentication-required! (partial terminate "Proxy Authentication Required" :proxy-authentication-required))
(def timeout!                       (partial terminate "Request Timed Out"             :timeout))
(def conflict!                      (partial terminate "Conflict"                      :conflict))
(def gone!                          (partial terminate "Gone"                          :gone))
(def length-required!               (partial terminate "Length Required"               :length-required))
(def precondition-failed!           (partial terminate "Precondition Failed"           :precondition-failed))
(def payload-too-large!             (partial terminate "Payload Too Large"             :payload-too-large))
(def uri-too-long!                  (partial terminate "URI Too Long"                  :uri-too-long))
(def unsupported-media-type!        (partial terminate "Unsupported Media Type"        :unsupported-media-type))
(def range-not-satisfiable!         (partial terminate "Range Not Satisfiable"         :range-not-satisfiable))
(def unprocessable-entity!          (partial terminate "Unprocessable Entity"          :unprocessable-entity))
(def locked!                        (partial terminate "Locked"                        :locked))
(def too-many-requests!             (partial terminate "Too Many Requests"             :too-many-requests))

(def server-error!                  (partial terminate "Server Error"                  :server-error))
(def not-implemented!               (partial terminate "Not Implemented"               :not-implemented))
(def bad-gateway!                   (partial terminate "Bad Gateway"                   :bad-gateway))
(def service-unavailable!           #(throw (make-service-unavailable %)))
(def gateway-timeout!               (partial terminate "Gateway Timeout"               :gateway-timeout))
(def http-version-not-supported!    (partial terminate "HTTP Version Not Supported"    :http-version-not-supported))

(def silent-blacklist!              (partial terminate "OK"                            :silent-blacklist))

(defmacro assert! [expression]
  `(let [result# ~expression]
     (if result#
       result#
       (server-error! {:reason :assertion-failed
                       :assertion '~expression}))))

(defmacro try-type
  "Macro with support for catching (or explicitly not catching) exceptions thrown
  by functions in this namespace (e.g., ex/unauthorized!).  Can function in one of two ways:
  * `(try-type xxx (catch     #{:unauthorized} data (prn data)))`
  * `(try-type xxx (catch-not #{:unauthorized} data (prn data)))`
  Note that the `data` symbol above is bound to the ex-data of the exception, not the exception itself
  Note that these do not compose at the moment - it's one or the other."
  [& body]
  (let [try-body (butlast body)
        [catch types sym & catch-body] (last body)]
    (assert ('#{catch catch-not} catch))
    (assert (set? types))
    (assert (symbol? sym))
    `(try
       ~@try-body
       (~'catch Exception e#
                (~('{catch if catch-not if-not} catch) (some-> e# ex-data :type ~types)
                                                       (let [~sym (ex-data e#)] ~@catch-body)
                                                       (throw e#))))))

(defn cause [e] (when e (or (.getCause ^Throwable e) e)))
