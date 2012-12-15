# clj-kasumi

This is an implementation of the [KASUMI](http://en.wikipedia.org/wiki/KASUMI) block cipher in pure Clojure. It - this implementation, that is - does neither perform well nor make any guarantees regarding security. 

__Do not use in production code, only for educational purposes!__

## Usage

Include in Leiningen:

```clojure
[clj-kasumi "0.1.0"]
```

KASUMI operates on 64-bit words which are represented as a a vector of two 32-bit integers. A single word can be encrypted/decrypted using `clj-kasumi.core/encode-word64` and `clj-kasumi.core/decode-word64` respectively:

```clojure
(ns test
  (:use [clj-kasumi.core :as kasumi]))

;; a key is an eight-element vector of 16-bit integers
(def K [ 0 1 2 3 4 5 6 7 ])

;; encode/decode word
(kasumi/encode-word64 K [30 32])                 ;; -> [3255589620 1286811847]
(kasumi/decode-word64 K [3255589620 1286811847]) ;; -> [30 32]
```

The functions `clj-kasumi.core/encode-string` and `clj-kasumi.core/decode-string` can be used to encrypt/decrypt complete strings. And finally, the function `clj-kasumi.core/string-&gt;key` creates an eight-element key vector from any given string by converting pairs of characters into 16-bit integers.

## Thanks

Thanks to Dominik Vinan for introducing me to the challenge of implementing this.

## License

Copyright © 2012 Yannick Scherer

Distributed under the Eclipse Public License, the same as Clojure.
