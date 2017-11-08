(ns tetris.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [reagent.core :as reagent :refer [atom]]
            [cljs.core.async :refer [put! chan <! >! timeout close!]]
            [cljs.pprint :refer [pprint]]))

(enable-console-print!)

(def shapes
  {
   ;:I [[0 0 0 0]
   ;    [1 1 1 1]
   ;    [0 0 0 0]
   ;    [0 0 0 0]]
   ;:J [[0 0 0]
   ;    [1 1 1]
   ;    [0 0 1]]
   ;:L [[0 0 0]
   ;    [1 1 1]
   ;    [1 0 0]]
   :O [[1 1]
       [1 1]]
   ;:S [[0 0 0]
   ;    [0 1 1]
   ;    [1 1 0]]
   ;:T [[0 0 0]
   ;    [1 1 1]
   ;    [0 1 0]]
   ;:Z [[1 1 0]
   ;    [0 1 1]
   ;    [0 0 0]]
   })

(defn initial-state []
  {:frame 0
   :speed 50
   :score 0
   :high-score 0
   :locked #{}
   :shape-pos [(rand-int 7) 0]
   :shape ((rand-nth (keys shapes)) shapes)})

(defn rotate-ccw [shape]
  (apply mapv vector (map rseq shape)))

(defn rotate-cw [shape]
  (apply mapv (comp vec rseq vector) shape))

(defn shape-coords [{:keys [shape-pos shape]}]
  (let [d (count shape)]
    (for [i (range d) j (range d) :when (= 1 (get-in shape [i j]))]
      (mapv + [i j] shape-pos))))

(defn score [{:keys [score high-score] :as state} amt]
  (let [new-score (+ score amt)]
    (cond-> (assoc state :score new-score)
            (> new-score high-score)
            (assoc :high-score new-score))))

(defn valid? [{:keys [locked] :as state}]
  (every? (fn [[x y :as c]]
            (and ((complement locked) c) (<= 0 x 9) (< y 22)))
          (shape-coords state)))

(defn x-shift [state f]
  (let [shifted (update-in state [:shape-pos 0] f)]
    (if (valid? shifted) shifted state)))

(defn rotate [state f]
  (let [shifted (update state :shape f)]
    (if (valid? shifted) shifted state)))

(defn clear-row [{:keys [locked] :as state} row]
  (if (every? locked (for [i (range 10)] [i row]))
    (-> state
        (score 10)
        (update :speed dec)
        (assoc :locked
               (set (for [[i j] locked :when (not= j row)]
                      (if (< j row) [i (inc j)] [i j])))))
    state))

(defn fall [state]
  (let [shifted (update-in state [:shape-pos 1] inc)]
    (if (valid? shifted)
      shifted
      (let [locked-coords (shape-coords state)]
        (-> state
            (update :locked into locked-coords)
            (score 1)
            (#(reduce clear-row % (map second locked-coords)))
            (into { :shape ((rand-nth (keys shapes)) shapes)
                   :shape-pos [(rand-int 7) 0]}))))))

(defn fast-drop [{:keys [locked] :as state}]
  (some #(when (not= locked (:locked %)) %)
        (iterate fall state)))

(defn game-step [{:keys [frame locked speed] :as state}]
  (cond-> (update state :frame inc)
          ;;Comment out these two lines to stop action
          (zero? (mod frame (max speed 1)))
          fall
          (some zero? (map second locked))
          (into (dissoc (initial-state) :high-score))))

(defn render [state]
      (let [cell-dim 20 ;Tweak to control game size
            {:keys [locked score high-score]} @state
            sc (set (shape-coords @state))]
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
            #_[:h4 (str "Score: " score)]
            #_[:h4 (str "High Score: " high-score)]]))

;Comment out swap! form to pause the action.
(defn sim [state] (swap! state game-step))

(defonce state (atom (initial-state)))

(defn disable-browser-key-conflicts []
  (set! (.-onkeydown js/window)
        (fn [e]
          (when (and (#{32 37 38 39 40} (.-keyCode e))
                     (= (.-target e) (.-body js/document)))
            (.preventDefault e)))))

(defn handle-key [k state]
  (case k
    37 (swap! state x-shift dec)
    39 (swap! state x-shift inc)
    38 (swap! state rotate rotate-cw)
    40 (swap! state rotate rotate-ccw)
    32 (swap! state fast-drop)
    (pprint state)))

(defn handle-io [state]
  (let [c (chan)]
    (disable-browser-key-conflicts)
    ;dequeue key presses
    (go-loop [] (when-let [k (<! c)] (handle-key k state) (recur)))
    ;enqueue key presses
    (set! (.-onkeydown js/document) #(go (>! c (-> % .-keyCode))))))

(reagent/render-component
  [#'render state]
  (do
    (handle-io state)
    (. js/document (getElementById "app"))))

;simulate the game
(defonce interval-id (js/setInterval (partial #'sim state) 10))

;(defonce app (launch state (. js/document (getElementById "app"))))

;Things to try -
; disable falling in game-step
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
