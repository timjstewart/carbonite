(defproject storm/carbonite "1.2.1"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :description "Write Clojure data to and from bytes using Kryo."
  :dependencies  [[com.twitter/kryo "2.04"]
                  [org.clojure/clojure "1.4.0"]]
  :warn-on-reflection true
  :aot :all)
