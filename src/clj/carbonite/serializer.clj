(ns carbonite.serializer
  (:require [clojure.string :as s])
  (:import [carbonite ClojureMapSerializer URISerializer UUIDSerializer
            TimestampSerializer SqlDateSerializer SqlTimeSerializer RatioSerializer
            ClojureReaderSerializer]
           [com.esotericsoftware.kryo Kryo Serializer SerializationException]
           [com.esotericsoftware.kryo.serialize StringSerializer
            MapSerializer IntSerializer
            LongSerializer BigDecimalSerializer BigIntegerSerializer
            DateSerializer]
           [java.io ByteArrayInputStream InputStream]
           [java.nio ByteBuffer BufferOverflowException]
           [java.math BigDecimal BigInteger]
           [java.net URI]
           [java.util Date UUID]
           [java.sql Time Timestamp]
           [clojure.lang Keyword Symbol PersistentArrayMap
            PersistentHashMap MapEntry PersistentStructMap 
            PersistentVector PersistentHashSet Ratio ArraySeq
            Cons PersistentList PersistentList$EmptyList Var
            ArraySeq$ArraySeq_int LazySeq IteratorSeq StringSeq]))

(defn clj-print
  "Use the Clojure pr-str to print an object into the buffer using pr-str."
  [buffer obj]
  (StringSerializer/put buffer (pr-str obj)))

(defn clj-read
  "Use the Clojure read-string to read an object from a buffer."
  [buffer]
  (read-string (StringSerializer/get buffer)))

(def ^{:doc "Define a serializer that utilizes the Clojure pr-str and
  read-string functions to serialize/deserialize instances relying
  solely on the printer/reader. Binds *print-dup* to true on read."}
  clojure-print-dup-serializer
  (proxy [Serializer] []  
    (writeObjectData [buffer obj]
      (binding [*print-dup* true]
        (clj-print buffer obj)))
    (readObjectData [buffer type] (clj-read buffer))))

(defn clojure-coll-serializer
  "Create a collection Serializer that conj's to an initial collection."
  [^Kryo registry init-coll]
  (proxy [Serializer] []
    (writeObjectData [buffer v]
      (IntSerializer/put buffer (count v) true)
      (doseq [x v] (.writeClassAndObject registry buffer x)))
    (readObjectData [buffer type]
      (doall
       (loop [remaining (IntSerializer/get buffer true)
              data (transient init-coll)]
         (if (zero? remaining)
           (persistent! data)
           (recur (dec remaining)
                  (conj! data (.readClassAndObject registry buffer)))))))))

(defn clojure-seq-serializer
  "Create a sequence Serializer that will apply the constructor function on
   deserialization."
  [^Kryo registry constructor-fn]
  (proxy [Serializer] []
    (writeObjectData [buffer s]
      (IntSerializer/put buffer (count s) true)
      (doseq [x s] (.writeClassAndObject registry buffer x)))
    (readObjectData [buffer type]
      (let [len (IntSerializer/get buffer true)]
        (apply constructor-fn
               (repeatedly len #(.readClassAndObject registry buffer)))))))

(defn write-map
  "Write an associative data structure to Kryo's buffer. Write entry count as
   an int, then serialize alternating key/value pairs."
  [^Kryo registry ^ByteBuffer buffer m]
  (IntSerializer/put buffer (count m) true)
  (doseq [[k v] m]
    (.writeClassAndObject registry buffer k)
    (.writeClassAndObject registry buffer v)))

(defn read-map
  "Read a map from Kryo's buffer.  Read entry count, then deserialize alternating
   key/value pairs.  Transients are used for performance."
  [^Kryo registry ^ByteBuffer buffer]
  (doall
   (loop [remaining (IntSerializer/get buffer true)
          data (transient {})]
     (if (zero? remaining)
       (persistent! data)
       (recur (dec remaining)
              (assoc! data
                      (.readClassAndObject registry buffer)
                      (.readClassAndObject registry buffer)))))))

(def stringseq-serializer
  (proxy [Serializer] []
    (writeObjectData [buffer stringseq] (StringSerializer/put buffer (s/join stringseq)))
    (readObjectData [buffer type] (seq (StringSerializer/get buffer)))))

(def ^{:doc "Define a map of Clojure primitives and their serializers
  to install."}
  clojure-primitives
  (let [prims (array-map
               Keyword (ClojureReaderSerializer.)
               Symbol (ClojureReaderSerializer.)
               Ratio (RatioSerializer.)
               Var clojure-print-dup-serializer)]
    (if-let [big-int (try (Class/forName "clojure.lang.BigInt")
                          (catch ClassNotFoundException _))]
      (assoc prims big-int (ClojureReaderSerializer.))
      prims)))

(def java-primitives
  (array-map
   BigDecimal (BigDecimalSerializer.)
   BigInteger (BigIntegerSerializer.)
   Date       (DateSerializer.)
   Timestamp  (TimestampSerializer.)
   java.sql.Date (SqlDateSerializer.)
   java.sql.Time (SqlTimeSerializer.)
   URI  (URISerializer.)
   UUID (UUIDSerializer.)))

(defn clojure-collections
  [registry]
  (concat
   ;; collections where we can use transients for perf
   [[PersistentVector (clojure-coll-serializer registry [])]
    [PersistentHashSet (clojure-coll-serializer registry #{})]
    [MapEntry (clojure-coll-serializer registry [])]]

   ;; list/seq collections
   (map #(vector % (clojure-seq-serializer registry list))
        [Cons PersistentList$EmptyList PersistentList
         LazySeq IteratorSeq ArraySeq])
   
   ;; other seqs
   [[StringSeq stringseq-serializer]]
   
   ;; maps - use transients for perf
   (map #(vector % (ClojureMapSerializer. registry))
        [PersistentArrayMap PersistentHashMap PersistentStructMap])))

;; Copyright 2011 Revelytix, Inc.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;; 
;;     http://www.apache.org/licenses/LICENSE-2.0
;; 
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.
