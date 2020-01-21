(ns gamer.tictactoe
  (:require [clojure.spec.alpha :as spec]
            [clojure.string :refer [join]]
            [gamer.ansi :as ansi]))

(def ^:private players #{:x :o})

(def ^:private winning-positions
  [[[0 0] [0 1] [0 2]]
   [[1 0] [1 1] [1 2]]
   [[2 0] [2 1] [2 2]]
   [[0 0] [1 0] [2 0]]
   [[0 1] [1 1] [2 1]]
   [[0 2] [1 2] [2 2]]
   [[0 0] [1 1] [2 2]]
   [[0 2] [1 1] [2 0]]])

(spec/def ::player players)
(spec/def ::board
  (spec/and
    (spec/every
      (spec/and
        (spec/every (spec/nilable ::value))
        #(= (count %) 3)))
    #(= (count %) 3)))
(spec/def ::db
  (spec/keys :req [::board ::player]))

(def ^:private initial-board
  (spec/assert
    ::board
    (as-> nil $
      (vec (repeat 3 $))
      (vec (repeat 3 $)))))

(def initial-db
  (spec/assert
    ::db
    {::board initial-board
     ::player :x}))

(defn- get-positions [board positions]
  (map #(get-in board %) positions))

(defn- has-won? [board player]
  (as-> winning-positions $
    (map #(get-positions board %) $)
    (filter #(every? (partial = player) %) $)
    (> (count $) 0)))

(defn- winning-player [board]
  (->> players
       (filter #(has-won? board %))
       (first)))

(defn- board-full? [board]
  (not-any? nil? (flatten board)))

(defn continue? [{:keys [::board]}]
  (and
    (not (board-full? board))
    (nil? (winning-player board))))

(defn- str->pos [string]
  (let [idx (spec/assert
              (spec/int-in 0 9)
              (dec (. Integer parseInt string)))]
    [(quot idx 3) (mod idx 3)]))

(defn- update-board [board pos player]
  (spec/assert nil? (get-in board pos))
  (assoc-in board pos player))

(defn- next-player [player]
  (case player
    :x :o
    :o :x))

(defn update-db [input {:keys [::board ::player]}]
  {::board (as-> input $
            (str->pos $)
            (update-board board $ player))
   ::player (next-player player)})

(defn- render-player [value]
  (case value
    :x (ansi/green "X")
    :o (ansi/red "O")))

(defn- render-pos [[y x]]
  (str (+ (* 3 y)
          (inc x))))

(defn- render-board-pos [board pos]
  (if-let [value (get-in board pos)]
    (render-player value)
    (render-pos pos)))

(defn render-board [board]
  (->>
    (for [y (range 0 3)]
      (->>
        (for [x (range 0 3)] (render-board-pos board [y x]))
        (join " ")))
    (join "\n")))

(defn render [{:keys [::board ::player]}]
  (format
    "%s\n%s"
    (render-board board)
    (let [winner (winning-player board)]
      (cond
        winner (format "%s has won!\n" (render-player winner))
        (board-full? board) "Draw!\n"
        :else (format "%s moves next: " (render-player player))))))
