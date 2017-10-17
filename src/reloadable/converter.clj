(ns reloadable.converter
  (:import (javax.swing JTextField JFrame Box JLabel)
           (java.awt BorderLayout)
           (java.awt.event ActionListener)))

(defn f->c [f]
  (-> f (- 32.0) (/ 9) (* 5)))

(defn c->f [c]
  (-> c (* 9) (/ 5) (+ 32.0)))

(defonce app (doto (JFrame. "F2C")
               (.setSize 400 150)
               (.setVisible true)))

(defonce state (atom {:celsius 100.0}))

(defonce c-field (JTextField. (-> @state :celsius str)))
(defonce f-field (JTextField. (-> @state :celsius c->f str)))

(doto app
  (.setLayout (BorderLayout.))
  (.add (doto (Box/createVerticalBox)
          (.add (doto (Box/createHorizontalBox)
                  (.add (JLabel. "C: "))
                  (.add c-field)))
          (.add (doto (Box/createHorizontalBox)
                  (.add (JLabel. "F: "))
                  (.add f-field))))
        BorderLayout/CENTER)
  (.revalidate))

(.addActionListener
  c-field
  (reify ActionListener
    (actionPerformed [_ _]
      (swap! state assoc :celsius (read-string (.getText c-field))))))

(.addActionListener
  f-field
  (reify ActionListener
    (actionPerformed [_ _]
      (swap! state assoc :celsius (f->c (read-string (.getText f-field)))))))

(add-watch
  state
  :values-changed
  (fn [_ _ o n]
    (when (not= o n)
      (.setText c-field (str (:celsius n)))
      (.setText f-field (-> n :celsius c->f str)))))
