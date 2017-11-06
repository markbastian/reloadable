(ns reloadable.converter
  (:import (javax.swing JTextField JFrame Box JLabel)
           (java.awt BorderLayout)
           (java.awt.event ActionListener)))

;;; Convenience functions
(defn rankine->kelvin [r] (/ (* r 5.0) 9.0))
(defn kelvin->rankine [k] (/ (* k 9.0) 5.0))
(defn celsius->kelvin [c] (+ c 273.15))
(defn kelvin->celsius [c] (- c 273.15))
(defn farenheit->rankine [f] (+ f 459.67))
(defn rankine->farenheit [f] (- f 459.67))

(def farenheit->celsius
  (comp kelvin->celsius
        rankine->kelvin
        farenheit->rankine))

(def celsius->farenheit
  (comp rankine->farenheit
        kelvin->rankine
        celsius->kelvin))

(defn add-action [component func]
  (.addActionListener
    component
    (reify ActionListener
      (actionPerformed [this event]
        (func this event)))))

;NOTE: ns-level values can be encapsulated into a
; function once everything is working.

;;; Our singleton application
(defonce app (doto (JFrame. "F2C")
               (.setSize 400 150)
               (.setVisible true)))

;Our singleton app state
(defonce state (atom {:celsius 100.0}))

;;; Fields to be added. We don't define these inline
;;; since we want to manipulate them later.
;Note that you could wrap these up using functions/doto
; such that you never see the variable names.
(defonce ^JTextField c-field
         (JTextField.
           (-> @state
               :celsius
               str)))
(defonce ^JTextField f-field
         (JTextField.
           (-> @state
               :celsius
               celsius->farenheit
               str)))

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
  (fn [_ _]
    (swap!
      state
      assoc
      :celsius
      (-> c-field .getText read-string))))
#_
(add-action
  f-field
  (fn [_ _]
    (swap!
      state
      assoc
      :celsius
      (-> f-field
          .getText
          read-string
          farenheit->celsius))))

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
      (.setText c-field
                (str (:celsius n)))
      (.setText f-field
                (-> n :celsius celsius->farenheit str)))))
