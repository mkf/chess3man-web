(ns chess3man-web.app
  (:require [reagent.core :as r]
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

(def main-board-colors (r/atom (give-normal-colors true)))

(defn set-normal-colors [even-dark] (reset! main-board-colors (give-normal-colors even-dark)))

(defn main-board-color [pos] (r/cursor main-board-colors [pos]))

(def the-paths (vec (concat [:g {:transform "translate(425,425)"}] (paths 130 415 main-board-color))))

(defn some-component []
  [:div
   "góra gen"
   [:svg {:width 850 :height 850 :viewBox "0 0 850 850"}
    (into the-paths [[:text {:x 0 :y 0} "A tu numer gry"]
                     [:image {:x 140 :y 0 :xlinkHref
                              "http://archiet.platinum.edu.pl/3manchess/res/pionki/Chess_klt60.png"}]])]
   "dół gen"])

(defn init []
  (r/render-component [some-component]
                            (.getElementById js/document "container")))
