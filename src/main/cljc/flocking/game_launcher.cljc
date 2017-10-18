(ns flocking.game-launcher
  (:require
    [quil.core :as q #?@(:cljs [:include-macros true])]
    [quil.middleware :as m]
    [flocking.simulation :as sim]
    [flocking.rules :as rules]
    [flocking.quil-renderer :as qr]
    [flocking.actions :as actions]
    [flocking.io :as io]
    [clojure.pprint :as pp]))

(defn setup [num-boids]
  (q/smooth)
  (q/frame-rate 30)
  (rules/initial-state num-boids 20))

;https://clojure.org/guides/weird_characters#__code_code_var_quote
(defn launch-sketch [{:keys[width height host num-boids]}]
  (q/sketch
    :title "Flocking Behaviors"
    #?@(:cljs [:host host])
    :setup #(setup num-boids)
    :draw #'qr/draw
    :update #'sim/sim
    :mouse-clicked #'io/mouse-click
    :mouse-moved #'io/mouse-move
    ;:key-pressed #'io/key-pressed
    :key-typed #'io/key-pressed
    :middleware [m/fun-mode]
    :size [width height]))

;; Launch demo
; (launch-sketch { :width 600 :height 600 :num-boids 100 })

;#?(:clj (launch-sketch { :width 600 :height 600 :num-boids 100 }))

#?(:cljs (defn ^:export launch-app[host width height num-boids]
           (launch-sketch { :width width :height height :host host :num-boids num-boids })))