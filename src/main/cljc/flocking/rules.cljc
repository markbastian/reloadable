(ns flocking.rules
  (:require [vecmath.vec :as vec]))

;http://www.red3d.com/cwr/steer/Wander.html
;https://www.khanacademy.org/computer-programming/boid-seek-arrive-wander-behaviors/5939360759808000


(defn update-wander [{:keys [rate] :as s}]
  (update s :direction #(+ % (* (- (rand) 0.5) rate))))

(defn tovec[m d]((juxt #(* m (Math/cos %)) #(* m (Math/sin %))) d))

(defn wander [{:keys [direction strength]} {:keys [state]} _]
  (let [velocity (state 1)
        m (vec/mag velocity)
        f (if (zero? m) velocity (map #(* (Math/sqrt 2.0) (/ % m)) velocity))
        w (tovec strength direction)]
    (vec/add f w)))

(defn separate [{:keys [range strength]} {:keys [state]} {:keys [boids]}]
  (let [nearby (for [{[p] :state } boids
                     :let [dp (vec/sub (state 0) p) m (vec/mag dp)]
                     :when (<= 0 m range) ]
                 (if (zero? m) (state 0) (vec/scale dp (/ strength m m))))]
    (apply mapv + nearby)))

(defn weighted-vec [vec strength average-vec]
  (let [dv (vec/sub average-vec vec)
        mag (vec/mag dv)]
    (if (zero? mag)
      [0 0]
      (vec/scale dv (/ strength mag)))))

(defn align [{:keys [strength] } {:keys [state]} {:keys [average-velocity] }]
  (weighted-vec (state 1) strength average-velocity))

(defn cohere [{:keys [strength] } {:keys [state]} {:keys [average-position] }]
  (weighted-vec (state 0) strength average-position))

(defn seek [{:keys [strength]} {:keys [state target-pos]} _]
  (if target-pos
    (weighted-vec (state 0) strength target-pos)
    [0 0]))

(defn flee [{:keys [strength]} {:keys [state predator-pos]} _]
  (if predator-pos
    (weighted-vec predator-pos strength (state 0))
    [0 0]))

(defn gen-wander []
  { :direction (* 2 Math/PI (Math/random))
   :rate (Math/random)
   :strength (* 10 (Math/random)) })

(defn rand-range [lo hi]
  (+ lo (* (- hi lo) (Math/random))))

(defn gen-pos [{:keys [minx maxx miny maxy]}]
  [(rand-range minx maxx) (rand-range miny maxy)])

(defn gen-state [{:keys [minx maxx miny maxy]}]
  [[(rand-range minx maxx) (rand-range miny maxy)] [0 0]])

(defn initial-state [num-boids dim]
  (let [world { :minx (- dim) :maxx dim :miny (- dim) :maxy dim }]
    {:world world
     :boids (for [_ (range num-boids)]
              {:width 0.6
               :height 1.0
               :color [0 255 0]
               :max-speed 5.0
               :state (gen-state world)
               :behaviors
               {
                ;:hunger { :metabolism 100 :strength 10 }
                :wander (assoc (gen-wander) :strength 10 :debug false)
                :separate { :range 2 :strength 10 }
                :align { :strength 10 }
                :cohere { :strength 50 }
                :seek { :strength 0 }
                :flee { :strength 0 }}})
     :food (repeatedly 20 #(gen-pos world))}))