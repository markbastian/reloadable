(ns flocking.simulation
  (:require [flocking.rules :as rules]
            [vecmath.vec :as vec]))

(defn w [v lo hi]
  (let [delta (- hi lo)]
    (loop [x v]
      (cond
        (< x lo)
        (recur (+ x delta))
        (>= x hi)
        (recur (- x delta))
        :else x))))

(defn wrap [state {:keys [minx maxx miny maxy]}]
  (-> state
      (update-in [:state 0 0] #(w % minx maxx))
      (update-in [:state 0 1] #(w % miny maxy))))

(defmulti steer (fn[behavior-name _ _ _] behavior-name))
(defmethod steer :wander [_ behavior boid flock] (rules/wander behavior boid flock))
(defmethod steer :separate [_ behavior boid flock] (rules/separate behavior boid flock))
(defmethod steer :align [_ behavior boid flock] (rules/align behavior boid flock))
(defmethod steer :cohere [_ behavior boid flock] (rules/cohere behavior boid flock))
(defmethod steer :seek [_ behavior boid flock] (rules/seek behavior boid flock))
(defmethod steer :flee [_ behavior boid flock] (rules/flee behavior boid flock))

(defn sim-boid [{:keys [state max-speed behaviors] :as boid } world-state dt]
  (let [[pos vel] state
        forces (for [[b behavior] behaviors] (steer b behavior boid world-state))
        vprime (vec/add vel (map #(* % dt) (apply map + forces)))
        vmag (vec/mag vprime)
        vp (if (zero? vmag) vel (map #(* max-speed (/ % vmag)) vprime))
        new-states [(vec/add pos (vec/scale vp dt)) vp]]
    (-> boid
        (assoc-in [:state] new-states)
        (update-in [:behaviors :wander] rules/update-wander)
        (wrap (:world world-state)))))

(defn averages [{:keys [boids] :as state}]
  (->> boids
       (map :state)
       (apply map vector)
       (map #(apply map + %))
       (map #(vec/scale % (/ 1.0 (count boids))))
       (zipmap [:average-position :average-velocity])
       (into state)))

(defn sim[state]
  (let [t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        dt (* (- t (state :time t)) 1E-3)
        new-boids (for [boid (:boids state)] (sim-boid boid state dt))]
    (-> state (into { :time t :boids new-boids }) averages)))
