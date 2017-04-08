(ns chess3man-web.app
  (:require [reagent.core :as r]
            [clj3manchess.engine.vectors :as vec]
            [chess3man-web.play.game.board.squares :as sq :refer [paths]]))
            ;;[clj3manchess.engine.pos :refer [rank]]))

(enable-console-print!)

(print sq/all-ranks-and-files)


(def dark-color "#a65c13")
(def light-color "#f4d80b")

(defn give-normal-colors [even-dark] (zipmap sq/all-ranks-and-files
                                             (map #(if (= even-dark
                                                          (= (even? (first %))
                                                             (even? (second %))))
                                                     dark-color light-color)
                                                  sq/all-ranks-and-files)))

(defonce board-rtl true)

(def main-board-rot (r/atom 3))

(defn to-game-file [board-file] (if board-rtl (- 24 (mod (- board-file @main-board-rot) 24))
                                    (mod (- board-file @main-board-rot) 24)))

(defn to-board-file [game-file] (if board-rtl (to-game-file game-file) (mod (+ to-board-file @main-board-rot) 24)))

(def main-board-colors (r/atom (give-normal-colors true)))

(defn set-normal-colors [even-dark] (reset! main-board-colors (give-normal-colors even-dark)))

(defn set-color [pos color] (swap! main-board-colors assoc pos color))

(defn main-board-color [pos] (r/cursor main-board-colors [pos]))

(def main-moats (r/atom [true true true]))

(def pionek-prefix "http://archiet.platinum.edu.pl/3manchess/res/pionki/Chess_")
(def pionek-typ {:bishop "b" :king "k" :knight "n" :pawn "p" :queen "q" :rook "r"})
(def pionek-kolor-list ["l" "g" "d"])
(def pionek-kolor-map {:white "l" :gray "g" :black "k"})
(def pionek-suffix "t60.png")
(defn pionek-url [typ kolor] (str pionek-prefix (pionek-typ typ) (pionek-kolor-map kolor) pionek-suffix))

(def main-board-figs (r/atom {}))

(defn main-board-pos [pos] (r/cursor main-board-figs [pos]))

(def the-paths (vec (into [:g {:transform "translate(425,425)"}] (paths 130 415 main-board-color))))

(defn some-component []
  [:div
   [:svg {:width 850 :height 850 :viewBox "0 0 850 850"}
    (into the-paths [[:text {:x 0 :y 0} "A kliknięte" (str @sq/clicked)]
                     [:image {:x 150 :y 0 :xlinkHref (pionek-url :king :white)}]])]
   [:div {:style {:float :right}} "Kliknięte " (str @sq/clicked) (str (vec/addvec {:inward true} [1 1])) [:br]]])

(defn init []
  (r/render-component [some-component]
                            (.getElementById js/document "container")))
