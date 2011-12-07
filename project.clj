(defproject cascalog/carbonite "1.0.3"
  :source-path "src/clj"
  :java-source-path "src/jvm"
  :description "Write Clojure data to and from bytes using Kryo."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.googlecode/kryo "1.04"]]
  :dev-dependencies [[swank-clojure "1.3.3"]]
  :warn-on-reflection true)
