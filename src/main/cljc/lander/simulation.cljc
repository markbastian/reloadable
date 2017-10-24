(ns lander.simulation
  (:require [lander.terrain :as terrain]))

(def default-state
  { :game-state :live
   :world { :minx -200 :maxx 200 :miny 0 :maxy 400 }
   ;:gravity -9.81
   :landing-zones { :locations [-50 50] :width 10 }
   :lander {:width 6
            :height 10
            :state [0 0 400 0 0]
            :theta 0
            :thrust 0
            :fuel-mass 10
            :max-landing-velocity 10 }})

;No gravity
;(def gravity 0)
;Earth
;(def gravity -9.81)
;Moon
;(def gravity -1.622)
;Jupiter
(def gravity -24.79)
;Upside down
;(def gravity 9.81)

(defn flatten-landing-zones [terrain lzs]
  (reduce
    (fn [t lz]
      (let [h (terrain/terrain-height lz t)
            hw (* 0.5 (lzs :width))]
        (terrain/make-flat ((juxt #(- % hw) #(+ % hw)) lz) h t)))
    terrain (lzs :locations)))

(defn reset-game []
  (let [{ { :keys [minx maxx] } :world lzs :landing-zones } default-state
        t (terrain/gen-real {:roughness 100 :cells { 0 0 1 0 } } 8 minx maxx)]
    (into default-state
          {:time (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
           :terrain (flatten-landing-zones t lzs)})))

(defmulti sim :game-state)

;States are [dt px py vx vy]
(defmethod sim :live [{:keys [time lander] :as s}]
  (let [{:keys [theta thrust state fuel-mass] } lander
        t (.getTime #?(:clj (java.util.Date.) :cljs (js/Date.)))
        ;DEBUG - Freeze time
        ;dt 0
        dt (* (- t time) 1E-3)
        remaining-fuel (max 0.0 (if (pos? thrust) (- fuel-mass dt) fuel-mass))
        effective-thrust (if (pos? remaining-fuel) thrust 0.0)
        f [(-> theta (* Math/PI) (/ -180) Math/sin (* effective-thrust))
           (+ gravity (-> theta (* Math/PI) (/ -180) Math/cos (* effective-thrust)))]
        v-new (mapv + [(state 3) (state 4)] (map #(* % dt) f))
        [_ py :as p-new] (mapv + [(state 1) (state 2)] (map #(* % dt) [(state 3) (state 4)]))
        new-states (reduce into [dt] [p-new v-new])]
    (-> s
        (into { :time t })
        (update :lander into {
                              ;DEBUG - Hold fuel constant
                              ;:fuel-mass 10
                              :fuel-mass remaining-fuel
                              ;DEBUG - Hold states constant
                              ;:state (assoc new-states 1 0 2 200)
                              :state new-states
                              :thrust effective-thrust})
        ;Use to debug falling
        #_(cond-> (< py 150) (assoc-in [:lander :state] [dt 0 400 0 0]))
        )))

(defmethod sim :default [state] state)
