(ns reloadable.converter
  (:import (javax.swing JTextField JFrame Box JLabel)
           (java.awt BorderLayout)
           (java.awt.event ActionListener)))

;;; Convenience functions
(defn f->c [f]
  (-> f (- 32.0) (/ 9) (* 5)))

(defn c->f [c]
  (-> c (* 9) (/ 5) (+ 32.0)))

(defn add-action [component func]
  (.addActionListener
    component
    (reify ActionListener
      (actionPerformed [this event] (func this event)
        #_(swap! state assoc :celsius (read-string (.getText c-field)))))))

;NOTE: ns-level values can be encapsulated into a function once everything is working.

;;; Our singleton application
(defonce app (doto (JFrame. "F2C")
               (.setSize 400 150)
               (.setVisible true)))

;Our singleton app state
(defonce state (atom {:celsius 100.0}))

;;; Fields to be added. We don't define these inline since we want to manipulate them later.
;Note that you could wrap these up using functions/doto such that you never see the variable names.
#_
(defonce c-field (JTextField. (-> @state :celsius str)))
#_
(defonce f-field (JTextField. (-> @state :celsius c->f str)))

;Existence proof
;(:text (bean c-field))

;;; Add fields to app
#_
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

;Edit fields. View State. No connection.
;@state

;;; Update state when editing UI
#_
(add-action
  c-field
  (fn [_ _] (swap! state assoc :celsius (read-string (.getText c-field)))))
#_
(add-action
  f-field
  (fn [_ _] (swap! state assoc :celsius (f->c (read-string (.getText f-field))))))

;No link from state to form fields.
;(swap! state assoc :celsius 10)

;;; Update UI when state modified.
;;; Note that state is not held by a bean. Good separation of concerns.
#_
(add-watch
  state
  :values-changed
  (fn [_ _ o n]
    (when (not= o n)
      (.setText c-field (str (:celsius n)))
      (.setText f-field (-> n :celsius c->f str)))))
