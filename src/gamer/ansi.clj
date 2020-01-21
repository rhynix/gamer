(ns gamer.ansi
  (:require [clojure.string
             :refer [join replace]
             :rename {replace replace-str}]))

(def ^:private ansi-codes
  {:reset "[0m"
   :red "[31m"
   :green "[32m"})

(def ^:private move-line-up
  "\u001b[1A")

(def ^:private clear-line
  "\u001b[2K")

(def ^:private simple-ansi-regex
  #"(?i)\u001b\[\d+[a-z]")

(defn- ansi-code [ansi]
  (str \u001b (get ansi-codes ansi)))

(defn- with-ansi [ansi]
  #(str (ansi-code ansi) % (ansi-code :reset)))

(defn strip [s]
  (replace-str s simple-ansi-regex ""))

(defn clear-prev-lines [n]
  (join (concat clear-line (repeat n (str move-line-up clear-line)))))

(def red
  (with-ansi :red))

(def green
  (with-ansi :green))
