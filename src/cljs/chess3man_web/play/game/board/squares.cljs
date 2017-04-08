(ns chess3man-web.play.game.board.squares
  (:require [schema.core :as s]
            [reagent.core :as r]
            [clj3manchess.engine.pos :as p :refer [Pos Rank File rank file]]))
;;                                        ;[clj3manchess.engine.pos :refer [rank]]))
(def all-ranks-and-files
  (->> (range 6)
       (map (fn [rank] (->> (range 24)
                            (map (fn [file] [rank,file])))))
       (apply concat)
       (into [])))

(defonce pi (aget js/Math "PI"))

(defonce clicked (r/atom nil))

(defn sin [x] (.sin js/Math x))

(defn cos [x] (.cos js/Math x))

(s/defn file-angle :- s/Num [file :- File] (/ (* file pi) 12))

(s/defn path-id :- s/String [pos :- Pos] (str "r" (rank pos) "f" (file pos)))

(defn segment-starting-coor [file radius] (map #(* radius %) (let [a (file-angle file)] [(sin a) (cos a)])))

(defn paths-data [radius] (take 24 (iterate (fn [given] (let [now (inc (first given))]
                                                                     [now (last given)
                                                                      (segment-starting-coor (inc now) radius)]))
                                                       [0 (segment-starting-coor 0 radius)
                                                        (segment-starting-coor 1 radius)])))

(defn paths-data-strings [radius] (map (fn [x] [(first x)
                                                           (str "M" (first (second x)) "," (second (second x))
                                                                " A" radius "," radius " 0 0 0 "
                                                                (first (last x)) "," (second (last x)))])
                                                  (paths-data radius)))

(defn paths-rank [radius rank stroke-width pos-to-color]
  (map (fn [x] [:path {:d (second x) :fill "none"
                       :id (path-id [rank (first x)])
                       :key (path-id [rank (first x)])
                       :stroke-width stroke-width
                       :stroke @(pos-to-color [rank (first x)])
                       :on-click (fn [] (swap! clicked #(if (= [rank (first x)] %) nil [rank (first x)])) )}])
       (paths-data-strings radius)))

(defn paths
  [center-radius outer-radius pos-to-color]
  (let [inter-radius (- outer-radius center-radius)
        stroke-width (/ inter-radius 6)
        half-width (/ stroke-width 2)]
    (vec (map #(paths-rank (+ center-radius half-width (* stroke-width %)) (- 5 %)
                           stroke-width pos-to-color) (range 6)))))
