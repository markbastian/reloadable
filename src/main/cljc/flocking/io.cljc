(ns flocking.io
  (:require [quil.core :as q #?@(:cljs [:include-macros true])]
            [flocking.actions :as actions]))

(defn mouse-click [{:keys [world] :as state} {:keys [x y]}]
  (let [{ :keys [minx maxx miny maxy] } world
        dx (- maxx minx) dy (- maxy miny)
        max-world-dim (max dx dy)
        wx (* -1 dx (- (/ (double (- (q/width) x)) (q/width)) 0.5))
        wy (* dy (- (/ (double (- (q/height) y)) (q/height)) 0.5))]
    (update state :boids (fn [boids] (map #(assoc % :target-pos [wx wy]) boids)))))

(defn mouse-move [{:keys [world] :as state} {:keys [x y]}]
  (let [{ :keys [minx maxx miny maxy] } world
        dx (- maxx minx) dy (- maxy miny)
        max-world-dim (max dx dy)
        wx (* -1 dx (- (/ (double (- (q/width) x)) (q/width)) 0.5))
        wy (* dy (- (/ (double (- (q/height) y)) (q/height)) 0.5))]
    (update state :boids (fn [boids] (map #(assoc % :predator-pos [wx wy]) boids)))))

(defn key-pressed [{:keys [active-behavior boids] :as state} {:keys [key key-code raw-key modifiers]}]
  (let [[f :as behaviors] (cycle (sort-by name (distinct (mapcat keys (keep :behaviors boids)))))]
    (case key
      :n (update state :active-behavior (partial actions/next-behavior state))
      :+ (cond-> state active-behavior (update :boids actions/adjust-behaviors active-behavior 10))
      :- (cond-> state active-behavior (update :boids actions/adjust-behaviors active-behavior -10))
      :d (update state :boids (fn [boids] (map (fn [boid] (update-in boid [:behaviors :wander :debug] not)) boids)))
      state)))