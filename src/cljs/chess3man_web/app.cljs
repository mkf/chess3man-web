(ns chess3man-web.app
  (:require [reagent.core :as r]
            [chess3man-web.play.game.board.squares :as sq :refer [paths]]))
            ;;[clj3manchess.engine.pos :refer [rank]]))

(enable-console-print!)

(print sq/all-ranks-and-files)


(def dark-color "#a65c13")
(def light-color "#f4d80b")

(def main-squares-colors (zipmap sq/all-ranks-and-files (map #(r/atom (if (= (even? (first %)) (even? (second %)))
                                                                        dark-color light-color))
                                                             sq/all-ranks-and-files)))

(def the-paths (vec (concat [:g {:transform "translate(100,100)"}] (paths 0 40 90 #(get main-squares-colors %) ))))

(defn some-component []
  [:div
   [:h3 "I am a component!"]
   [:p.someclass
    "I have " [:strong "bold"]
    [:span {:style {:color "red"}} " and red"
     [:svg {:width 200 :height 200 :viewBox "0 0 200 200"}
      the-paths]]
    " text."]])

(defn calling-component []
  [:div "Parent component"
   [some-component]])

(defn init []
  (r/render-component [calling-component]
                            (.getElementById js/document "container")))
