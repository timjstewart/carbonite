(defproject storm/carbonite "1.3.0"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :description "Write Clojure data to and from bytes using Kryo."
  :dependencies  [[com.esotericsoftware.kryo/kryo "2.12"]
                  [org.clojure/clojure "1.4.0"]]
  :warn-on-reflection true
  :aot :all)
