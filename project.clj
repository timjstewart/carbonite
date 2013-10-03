(defproject com.pearson.gambit/carbonite "1.6.0"
  :description "Write Clojure data to and from bytes using Kryo.  This particular version exists because it uses kryo vesion 2.21 which is compatible with Titan"
  :min-lein-version "2.0.0"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :dependencies  [[com.esotericsoftware.kryo/kryo "2.21"]
                  [org.clojure/clojure "1.4.0"]]
  :global-vars { *warn-on-reflection* true }
  :aot :all
  :repositories [["pearson-nexus-releases", "https://nexus.pearsoncmg.com/nexus/content/repositories/releases"],
                 ["pearson-nexus-snapshots", "https://nexus.pearsoncmg.com/nexus/content/repositories/snapshots"]])

