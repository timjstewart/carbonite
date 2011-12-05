(ns carbonite.JavaBridge
  (:use [carbonite api serializer])
  (:gen-class :main false
              :methods
              [^{:static true} [defaultRegistry
                                [] com.esotericsoftware.kryo.Kryo]
               ^{:static true} [enhanceRegistry
                                [com.esotericsoftware.kryo.Kryo] void]]))

(defn -defaultRegistry []
  (default-registry))

(defn -enhanceRegistry [registry]
  (doto registry
    (register-serializers clojure-primitives)
    (register-serializers (clojure-collections registry))))
