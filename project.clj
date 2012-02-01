(def shared '[[com.googlecode/kryo "1.04"]])

(defproject cascalog/carbonite "1.1.1"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :description "Write Clojure data to and from bytes using Kryo."
  :dev-dependencies [[lein-multi "1.1.0-SNAPSHOT"]]
  :dependencies      ~(conj shared '[org.clojure/clojure "1.3.0"])
  :multi-deps {"1.2" ~(conj shared '[org.clojure/clojure "1.2.1"])
               "1.4" ~(conj shared '[org.clojure/clojure "1.4.0-alpha3"])}
  :warn-on-reflection true)
