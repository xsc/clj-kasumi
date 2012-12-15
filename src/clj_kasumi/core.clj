(ns ^{ :doc "One-off Encoding/Decoding functions."
       :author "Yannick Scherer" }
  clj-kasumi.core
  (:use [clj-kasumi.cipher :as kasumi]
        clj-kasumi.helpers))

;; ---------------------------------------------------------------
;; Conversion between 64-bit words and ASCII characters
;; ---------------------------------------------------------------
(defn ascii->int32
  "Convert seq of ASCII (!) characters to seq of 32-bit integers. Four characters
   are always grouped into one integer."
  [string]
  (->>
    (map int string)
    (partition 4 4 nil)
    (map (fn [[i3 i2 i1 i0]]
           (->
             (+
               (bit-shift-left (or i3 0) 24)
               (bit-shift-left (or i2 0) 16)
               (bit-shift-left (or i1 0) 8)
               (or i0 0))
             (mask-bits 32))))))

(defn int32->ascii
  "Convert seq of 32-bit integers to seq of ASCII characters. Each integer
   is interpreted as four 8-bit ASCII characters."
  [sq]
  (->> sq
    (mapcat
      (fn [x]
        (vector
          (bit-and (bit-shift-right x 24) 0xFF)
          (bit-and (bit-shift-right x 16) 0xFF)
          (bit-and (bit-shift-right x 8) 0xFF)
          (bit-and x 0xFF))))
    (take-while #(not (= % 0)))
    (map char)))

(defn ascii->word64
  "Convert seq of ASCII (!) characters to seq of pairs of 32-bit integers."
  [string]
  (->> string
    ascii->int32
    (partition 2 2 [0])))

(defn word64->ascii
  "Convert seq of pairs of 32-bit integers to seq of ASCII characters."
  [sq]
  (->> sq
    flatten
    int32->ascii))

;; ---------------------------------------------------------------
;; One-off Encoding/Decoding functions
;; ---------------------------------------------------------------

(defn encode-word64
  "Encode word (two-element vector of 32-bit integers) using the given key."
  [K word]
  (let [ef (kasumi/new-encoder K)]
    (ef word)))

(defn decode-word64
  "Decode word (two-element vector of 32-bit integers) using the given key."
  [K word]
  (let [df (kasumi/new-decoder K)]
    (df word)))

(defn encode-string
  "Encode String using the given encoder (either a key or a function)."
  [encoder string]
  (let [ef (kasumi/new-encoder encoder)]
    (->> string
      (ascii->word64)
      (map ef)
      (word64->ascii)
      (apply str))))

(defn decode-string
  "Decode String using the given decoder (either a key or a function)."
  [decoder string]
  (let [df (kasumi/new-decoder decoder)]
    (->> string
      (ascii->word64)
      (map df)
      (word64->ascii)
      (apply str))))

;; ---------------------------------------------------------------
;; Key Generation
;; ---------------------------------------------------------------
(defn string->key
  "Will create an 128-bit key from a given string by using the first 16 characters of it
   (starting at the first one again if the string is too short) and creating 16-bit integers
   from each pair."
  [string]
  (->>
    (cond (empty? string) [0 0 0 0 0 0 0 0]
          (>= (count string) 16) (take 16 string)
          :else (->> (constantly (seq string))
                  (repeatedly)
                  (flatten)
                  (take 16)))
    (map int)
    (partition 2)
    (map (fn [[a b]] (+ (* 256 a) b)))))
