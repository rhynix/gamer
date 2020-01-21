(ns gamer.tictactoe-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.string :refer [split join trim]]
            [gamer.ansi :as ansi]
            [gamer.tictactoe :as game :refer [continue? update-db render]]))

(defn multiple-lines [string]
  (as-> string $
    (split $ #",")
    (join "\n" $)))

(def ^:private empty-board
  [[nil nil nil]
   [nil nil nil]
   [nil nil nil]])

(def ^:private mid-game-board
  [[nil :x nil]
   [nil nil :o]
   [:x nil nil]])

(def ^:private won-board
  [[:x :x :x]
   [:o :o nil]
   [nil nil nil]])

(def ^:private draw-board
  [[:x :o :x]
   [:x :o :x]
   [:o :x :o]])

(defn- db-with-board [board]
  {::game/board board ::game/player :x})

(defn- db-with-board-and-player [board player]
  {::game/board board ::game/player player})

(deftest continue?-returns-true-for-empty-board
  (is (continue? (db-with-board empty-board))))

(deftest continue?-returns-true-for-mid-game-board
  (is (continue? (db-with-board mid-game-board))))

(deftest continue?-returns-false-for-won-board
  (is (not (continue? (db-with-board won-board)))))

(deftest contonue?-returns-false-for-draw-board
  (is (not (continue? (db-with-board draw-board)))))

(deftest update-db-sets-value-for-player
  (is (= [[nil :x nil]
          [nil nil nil]
          [nil nil nil]]
         (->> (db-with-board-and-player empty-board :x)
              (update-db "2")
              (::game/board)))))

(deftest update-db-switches-player
  (is (= :o
         (->> (db-with-board-and-player empty-board :x)
              (update-db "2")
              (::game/player)))))

(deftest render-empty-board
  (is (=
       (multiple-lines "1 2 3,4 5 6,7 8 9,X moves next:")
       (trim (ansi/strip
               (render (db-with-board-and-player empty-board :x)))))))

(deftest render-mid-game-board
  (is (=
       (multiple-lines "1 X 3,4 5 O,X 8 9,O moves next:")
       (trim (ansi/strip
               (render (db-with-board-and-player mid-game-board :o)))))))

(deftest render-won-board
  (is (=
       (multiple-lines "X X X,O O 6,7 8 9,X has won!")
       (trim (ansi/strip
               (render (db-with-board-and-player won-board :o)))))))

(deftest render-draw-board
  (is (=
       (multiple-lines "X O X,X O X,O X O,Draw!")
       (trim (ansi/strip
               (render (db-with-board draw-board)))))))
