(ns ^{ :doc "Bitwise Operation Helpers"
       :author "Yannick Scherer" }
  clj-kasumi.helpers)

(defn mask-bits
  "Ensure that the given integer has at most n bits."
  [x n]
  (if (<= n 0) 
    0
    (->
      (reduce (fn [i _] (* i 2)) 1 (range n))
      dec
      (bit-and x))))

(defn upper-bits
  "Get upper n bits of an l-bit integer."
  [x l n]
  (->
    (bit-shift-right x (- l n))
    (mask-bits n)))

(defn lower-bits
  "Get lower n bits of an l-bit integer."
  [x l n]
  (mask-bits x n))

(defn combine-bits
  "Combine the given bitstrings to an l-bit integer where n is the number of 
   bits to use for y."
  [x y l n]
  (->
    (bit-or
      (bit-shift-left x n)
      (mask-bits y n))
    (mask-bits l)))

(defn rol16
  "Circular left-shift on 16-bit integer."
  [x n]
  (let [n (mod n 16)]
    (if (= n 0)
      x
      (->
        (bit-or
          (bit-shift-left x n)
          (bit-shift-right x (- 16 n)))
        (mask-bits 16)))))

