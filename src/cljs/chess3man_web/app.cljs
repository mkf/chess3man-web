(ns chess3man-web.app
  (:require [reagent.core :as r]
            [reagent.ratom :as ra]
            [clj3manchess.engine.pos :as p]
            [clj3manchess.engine.color :as c]
            [clj3manchess.engine.vectors :as vec]
            [clj3manchess.engine.board :as b]
            [clj3manchess.engine.fig :as f]
            [schema.core :as s]
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
(defn to-game-pos [pos] [(first pos) (to-game-file (second pos))])

(defn to-board-file [game-file] (if board-rtl (to-game-file game-file) (mod (+ to-board-file @main-board-rot) 24)))
(defn to-board-pos [pos] [(first pos) (to-board-file (second pos))])

(def main-board-colors (r/atom (give-normal-colors true)))

(defn set-normal-colors [even-dark] (reset! main-board-colors (give-normal-colors even-dark)))

(defn set-color [pos color] (swap! main-board-colors assoc pos color))

(defn main-board-color [pos] (r/cursor main-board-colors [pos]))

(def main-moats (r/atom [true true true]))

(def pionek-prefix "http://archiet.platinum.edu.pl/3manchess/res/pionki/Chess_")
(def pionek-typ {:bishop "b" :king "k" :knight "n" :pawn "p" :queen "q" :rook "r"})
(def pionek-kolor-list ["l" "g" "d"])
(def pionek-kolor-map {:white "l" :gray "g" :black "d"})
(def pionek-suffix "t60.png")
(s/defn pionek-url :- s/Str ([typ :- f/FigType kolor :- c/Color]
                             (str pionek-prefix (pionek-typ typ) (pionek-kolor-map kolor) pionek-suffix))
  ([fig :- (s/either f/Piece f/Pawn)] (pionek-url (:type fig) (:color fig))))

(def main-board-figs (r/atom (into {} (map (fn [x] {x {:type :queen :color (nth c/colors (quot (p/file x) 8))}}) p/all-pos))))

(def centerradius (r/atom 130))

(def size (r/atom 850))
(def half-size (/ @size 2) )
(def half-size-10 (- half-size 10) )
(def translate-string (str "translate(" half-size "," half-size ")") )
(def viewBox (str 0 " " 0 " " @size " " @size) )

(def main-fig-images (map (fn [arr] [:image (let [_ (println arr)
                                                  _ (println (to-board-pos (first arr)))
                                                  {:keys [x y]} (sq/whatxy @centerradius half-size-10 (to-board-pos (first arr)))
                                                  xlinkhref (pionek-url (second arr))]
                                              {:x x :y y :xlinkHref xlinkhref}
                                              )]) (into [] @main-board-figs)))

(defn main-board-pos [pos] (r/cursor main-board-figs [pos]))

(def the-paths (vec (into [:g {:transform translate-string}] (paths @centerradius half-size-10 main-board-color))))

(defn some-component []
  [:div
   [:svg {:width @size :height @size :viewBox viewBox}
    (into (into the-paths [[:text {:x 0 :y 0} "A kliknięte" (str @sq/clicked)]
                     [:image {:x 150 :y 0 :xlinkHref (pionek-url :king :white)}]])
          main-fig-images) ]
   [:div {:style {:float :right}} "Kliknięte " (str @sq/clicked) (str (vec/addvec {:inward true} [1 1])) [:br]]])

(defn init []
  (r/render-component [some-component]
                            (.getElementById js/document "container")))
