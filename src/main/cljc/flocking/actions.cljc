(ns flocking.actions)

(defn adjust-behavior [boid behavior amount]
  (update-in boid [:behaviors behavior :strength] #(max (+ % amount) 0)))

(defn adjust-behaviors [boids behavior amount]
  (map #(adjust-behavior % behavior amount) boids))

(defn next-behavior [{:keys [boids] :as state} behavior]
  (let [[f :as behaviors] (cycle (sort-by name (distinct (mapcat keys (keep :behaviors boids)))))]
    (if behavior
      (let [[x [_ n & _]] (split-with (complement #{behavior}) behaviors)]
        n)
      f)))