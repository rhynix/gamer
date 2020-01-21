(ns gamer.ansi-test
  (:require [clojure.test :refer [deftest is]]
            [gamer.ansi :as ansi]))

(deftest red-returns-ansi-red-with-reset
  (is (= "\u001b[31mString\u001b[0m" (ansi/red "String"))))

(deftest green-returns-ansi-green-with-reset
  (is (= "\u001b[32mString\u001b[0m" (ansi/green "String"))))

(deftest clear-prev-lines-zero-clears-single-line
  (is (= "\u001b[2K" (ansi/clear-prev-lines 0))))

(deftest clear-prev-lines-multiple-clears-x-plus-one-lines
  (is (= "\u001b[2K\u001b[1A\u001b[2K\u001b[1A\u001b[2K"
         (ansi/clear-prev-lines 2))))

(deftest strip-ansi-removes-ansi-escape-seqs
  (is (= "String" (ansi/strip (ansi/red "String")))))
