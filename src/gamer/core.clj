(clojure.spec.alpha/check-asserts true)

(ns gamer.core
  (:require [clojure.string :refer [join split-lines]]
            [gamer.ansi :as ansi]
            [clojure.spec.alpha :as spec]
            [gamer.tictactoe]))

(defn- game-get [game-ns what]
  (->> what
       (symbol)
       (ns-resolve game-ns)
       (deref)))

(defn- game-loop [game]
  (loop [state (game-get game :initial-db)
         prev-output-lines 0]
    (let [output ((game-get game :render) state)
          output-lines (count (split-lines output))]
      (print (ansi/clear-prev-lines prev-output-lines))
      (print output)
      (flush)
      (when ((game-get game :continue?) state)
        (as-> (read-line) $
          ((game-get game :update-db) $ state)
          (recur $ output-lines))))))

(defn -main []
  (game-loop 'gamer.tictactoe))
