(ns carbonite.JavaBridge
  (:use carbonite.api)
  (:gen-class :main false
              :methods
              [^{:static true} [defaultRegistry
                                [] com.esotericsoftware.kryo.Kryo]
               ^{:static true} [enhanceRegistry
                                [com.esotericsoftware.kryo.Kryo] void]]))

(defn -defaultRegistry []
  (default-registry))

(defn -enhanceRegistry [registry]
  (default-registry registry))
