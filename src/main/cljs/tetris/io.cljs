(ns tetris.io
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [tetris.rules :as rules]
            [cljs.core.async :refer [put! chan <! >! timeout close!]]
            [cljs.pprint :refer [pprint]]))

(defn disable-browser-key-conflicts []
  (set! (.-onkeydown js/window)
        (fn [e]
          (when (and (#{32 37 38 39 40} (.-keyCode e))
                     (= (.-target e) (.-body js/document)))
            (.preventDefault e)))))

(defn handle-key [k state]
  (case k
    37 (swap! state rules/x-shift dec)
    39 (swap! state rules/x-shift inc)
    38 (swap! state rules/rotate rules/rotate-cw)
    40 (swap! state rules/rotate rules/rotate-ccw)
    32 (swap! state rules/fast-drop)
    (pprint state)))

(defn handle-io [state]
  (let [c (chan)]
    (disable-browser-key-conflicts)
    ;dequeue key presses
    (go-loop [] (when-let [k (<! c)] (handle-key k state) (recur)))
    ;enqueue key presses
    (set! (.-onkeydown js/document) #(go (>! c (-> % .-keyCode))))))