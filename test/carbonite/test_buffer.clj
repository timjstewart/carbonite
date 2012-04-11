(ns carbonite.test-buffer
  (:use [clojure.test]
        [carbonite.api]
        [carbonite.buffer])
  (:import [com.esotericsoftware.kryo Kryo KryoException]
           [java.nio ByteBuffer]))

(defn cleared-registry []
  (doto (default-registry)
    (clear-context)))

(deftest test-context
  (let [k (Kryo.)]
    (put-to-context k :foo)
    (is (= :foo (get-from-context k)))))

(deftest test-ensure-buffer
  (let [kryo (doto (Kryo.)
               (clear-context))
        ^ByteBuffer buff (ensure-buffer kryo)]
    (is (not (nil? buff)))
    (is (= *initial-buffer* (.capacity buff)))
    (is (= buff (get-from-context kryo)))))

;; TODO: Fix write-with-cached-buffer.
#_(deftest test-write-with-cached-buffer
  (testing "exception breaking max"
    (binding [*max-buffer* 16]
      (is (thrown-with-msg?
            KryoException
            #"Buffer limit exceeded serializing object of type: clojure.lang.LazySeq"
            (write-with-cached-buffer (cleared-registry)
              (ByteBuffer/allocate 1)
              (range 20))))))

  (testing "buffer gets bigger"
    (binding [*keep-buffer* 1024
              *max-buffer* 1024]
      (let [[^bytes bytes ^ByteBuffer buffer] (write-with-cached-buffer
                                                (cleared-registry)
                                                (ByteBuffer/allocate 1)
                                                (range 20))]
        (is (= (alength bytes) (.position buffer)))
        (is (= 64 (.capacity buffer))))))

  (testing "dont return buffer bigger than *keep-buffer*"
    (binding [*keep-buffer* 16
              *max-buffer* 1024]
      (let [[^bytes bytes buffer] (write-with-cached-buffer
                                    (cleared-registry)
                                    (ByteBuffer/allocate 1)
                                    (range 20))]
        (is (= 42 (alength bytes)))
        (is (nil? buffer))))))

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
