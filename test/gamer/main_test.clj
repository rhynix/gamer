(ns gamer.main-test
  (:require [clojure.test :refer [run-tests]]
            [gamer.core-test]))

(defn -main []
  (run-tests 'gamer.core-test))
