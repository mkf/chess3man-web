(ns chess3man-web.play.game.board.squares
  (:require [schema.core :as s]
            [reagent.core :as r]
            [clj3manchess.engine.pos :as p :refer [Pos Rank File rank file all-pos]]))
;;                                        ;[clj3manchess.engine.pos :refer [rank]]))
(def all-ranks-and-files all-pos)

(defonce pi (aget js/Math "PI"))

(defonce clicked (r/atom nil))

(defn sin [x] (.sin js/Math x))

(defn cos [x] (.cos js/Math x))

(s/defn file-angle :- s/Num [file :- s/Int] (/ (* file pi) 12))
(s/defn file-angle-center :- s/Num [file :- s/Int] (+ (/ pi 24) (file-angle file)))
(s/defn ranks-radiuses :- {(s/required-key :inter-radius) s/Num
                           (s/required-key :stroke-width) s/Num
                           (s/required-key :half-stroke-width) s/Num
                           (s/required-key :ranks-radiuses) [s/Num]} [center-radius :- s/Num, outer-radius :- s/Num]
  (let [inter-radius (- outer-radius center-radius)
        stroke-width (/ inter-radius 6)
        half-stroke-width (/ stroke-width 2)
        ranks-radiuses (into [] (map #(+ center-radius half-stroke-width (* stroke-width (- 5 %))) (range 6)) )]
    {:center-radius center-radius
     :outer-radius outer-radius
     :inter-radius inter-radius
     :stroke-width stroke-width
     :half-stroke-width half-stroke-width
     :ranks-radiuses ranks-radiuses}))

(s/defn path-id :- s/Str [pos :- Pos] (str "r" (rank pos) "f" (file pos)))

(defn segment-starting-coor [file radius] (map #(* radius %) (let [a (file-angle file)] [(sin a) (cos a)])))
(defn segment-central-coor [file radius] (map #(* radius %) (let [a (file-angle file)
                                                                  a (+ a (/ pi 24))] [(sin a) (cos a)])))

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



(defn whatxy [ranks-radiuses board-pos]
  (let [;;inter-radius (- outer-radius center-radius)
        ;;stroke-width (/ inter-radius 6)
        ;;half-width (/ stroke-width 2)
        rank (first board-pos)
        ;;rank (inc rank)
        file (second board-pos)
        ;;file (dec file)
        ;;radius (+ center-radius half-width (* stroke-width (- 5 rank)))
        ;;angle (file-angle file)
        radius (get (:ranks-radiuses ranks-radiuses) rank)
        xy (segment-central-coor file radius)
        ;;angle (+ angle (/ pi 24))
        x (first xy)
        y (second xy)
        ] {:x x :y y}))

(defn paths
  ;;[center-radius outer-radius pos-to-color]
  [ranks-radiuses pos-to-color]
  ;;(let [inter-radius (- outer-radius center-radius)
  ;;      stroke-width (/ inter-radius 6)
  ;;      half-width (/ stroke-width 2)]
  (let [{:keys [half-stroke-width stroke-width inter-radius center-radius ranks-radiuses]} ranks-radiuses]
    (vec (map #(paths-rank (get ranks-radiuses %) %
                           stroke-width pos-to-color) (range 6)))))
