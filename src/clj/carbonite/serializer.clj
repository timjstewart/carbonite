(ns carbonite.serializer
  (:require [clojure.string :as s])
  (:import [carbonite ClojureMapSerializer URISerializer UUIDSerializer
            TimestampSerializer SqlDateSerializer SqlTimeSerializer RatioSerializer
            ClojureReaderSerializer PrintDupSerializer StringSeqSerializer
            ClojureVecSerializer ClojureSetSerializer ClojureSeqSerializer
            RegexSerializer]
           [com.esotericsoftware.kryo Kryo Serializer SerializationException]
           [com.esotericsoftware.kryo.serialize StringSerializer
            MapSerializer IntSerializer
            LongSerializer BigDecimalSerializer BigIntegerSerializer
            DateSerializer]
           [java.io ByteArrayInputStream InputStream]
           [java.nio ByteBuffer BufferOverflowException]
           [java.math BigDecimal BigInteger]
           [java.util Date UUID]
           [java.util.regex Pattern]
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

(defn clj-print-dup
  "Use the Clojure pr-str to print an object into the buffer using
   pr-str w/ *print-dup* bound to true."
  [buffer obj]
  (binding [*print-dup* true]
    (clj-print buffer obj)))

(defn clj-read
  "Use the Clojure read-string to read an object from a buffer."
  [buffer]
  (read-string (StringSerializer/get buffer)))

(defn print-collection
  [^Kryo registry buffer coll]
  (IntSerializer/put buffer (count coll) true)
  (doseq [x coll]
    (.writeClassAndObject registry buffer x)))

(defn read-seq
  [^Kryo registry buffer]
  (let [len (IntSerializer/get buffer true)]
    (->> (repeatedly len #(.readClassAndObject registry buffer))
         (apply list))))

(defn mk-collection-reader [init-coll]
  (fn [^Kryo registry buffer]
    (loop [remaining (IntSerializer/get buffer true)
           data      (transient init-coll)]
      (if (zero? remaining)
        (persistent! data)
        (recur (dec remaining)
               (conj! data (.readClassAndObject registry buffer)))))))

(def read-vector (mk-collection-reader []))
(def read-set (mk-collection-reader #{}))

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

(defn write-string-seq [buffer string-seq]
  (StringSerializer/put buffer (s/join string-seq)))

(defn read-string-seq [buffer]
  (seq (StringSerializer/get buffer)))

(def ^{:doc "Define a map of Clojure primitives and their serializers
  to install."}
  clojure-primitives
  (let [prims (array-map
               Keyword (ClojureReaderSerializer.)
               Symbol  (ClojureReaderSerializer.)
               Ratio   (RatioSerializer.)
               Var     (PrintDupSerializer.))]
    (if-let [big-int (try (Class/forName "clojure.lang.BigInt")
                          (catch ClassNotFoundException _))]
      (assoc prims big-int (ClojureReaderSerializer.))
      prims)))

(def java-primitives
  (array-map
   BigDecimal    (BigDecimalSerializer.)
   BigInteger    (BigIntegerSerializer.)
   Date          (DateSerializer.)
   Timestamp     (TimestampSerializer.)
   java.sql.Date (SqlDateSerializer.)
   java.sql.Time (SqlTimeSerializer.)
   java.net.URI  (URISerializer.)
   Pattern       (RegexSerializer.)
   UUID          (UUIDSerializer.)))

(defn clojure-collections [registry]
  (concat
   ;; collections where we can use transients for perf
   [[PersistentVector (ClojureVecSerializer. registry)]
    [PersistentHashSet (ClojureSetSerializer. registry)]
    [MapEntry (ClojureVecSerializer. registry)]]

   ;; list/seq collections
   (map #(vector % (ClojureSeqSerializer. registry))
        [Cons PersistentList$EmptyList PersistentList
         LazySeq IteratorSeq ArraySeq])
   
   ;; other seqs
   [[StringSeq (StringSeqSerializer.)]]
   
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
