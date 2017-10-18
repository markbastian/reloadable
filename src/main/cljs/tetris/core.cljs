(ns tetris.core
  (:require [tetris.io :as game-io]
            [reagent.core :as reagent :refer [atom]]
            [tetris.rules :as rules]
            [cljs.pprint :refer [pprint]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(defn render [state]
      (let [cell-dim 20 ;Tweak to control game size
            {:keys [locked score high-score]} @state
            sc (set (rules/shape-coords @state))]
           [:div
            [:h1 "Tetris Clone"]
            [:svg {:width (* cell-dim 10) :height (* cell-dim 22)}
             (doall (for [i (range 10) j (range 22)]
                         [:rect { :key (str i ":" j) :x (* i cell-dim) :y (* j cell-dim)
                                 :width cell-dim :height cell-dim
                                 :stroke :red :fill (cond
                                                      (locked [i j]) :blue
                                                      (sc [i j]) :orange
                                                      :default :black) }]))]
            [:h4 (str "Score: " score)]
            [:h4 (str "High Score: " high-score)]]))

;Comment out swap! form to pause the action.
(defn sim [state] (swap! state rules/game-step))

(defonce state (atom (rules/initial-state)))

(reagent/render-component
  [#'render state]
  (do
    (game-io/handle-io state)
    (. js/document (getElementById "app"))))

;simulate the game
(defonce interval-id (js/setInterval (partial #'sim state) 10))

;(defonce app (launch state (. js/document (getElementById "app"))))

;Things to try -
; disable falling in rules/game-step
; Changing the shape position
;; (swap! state assoc :shape-pos [5 10])
; Changing the shape
;; (swap! state assoc :shape (:l tetris.shapes/shapes))
;;Custom shape
#_(swap!
    state
    assoc
    :shape
    [[1 0 1]
     [0 1 0]
     [1 0 1]])
; Change sim speed
;; (swap! state assoc :speed 25)
;Tweak colors in the renderer
; Paramaterize colors
;; (swap! state assoc :falling-color :cyan)

(defn on-js-reload []
      ;; optionally touch your app-state to force rerendering depending on
      ;; your application
      ;; (swap! app-state update-in [:__figwheel_counter] inc)
      )
