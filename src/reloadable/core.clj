(ns reloadable.core
  (:require [clojure.pprint :as pp]
            [cheshire.core :as ch])
  (:import (javax.swing JFrame JTable JMenuBar JMenu JMenuItem JSeparator)
           (java.awt BorderLayout)
           (java.awt.event ActionListener)
           (javax.swing.table DefaultTableModel)))

;https://github.com/toddmotto/public-apis#weather
;https://www.metaweather.com/api/
;https://www.metaweather.com/api/location/search/?query=boise
;https://www.metaweather.com/api/location/2366355/
(defn add-action [component action-fn]
  (.addActionListener
    component
    (reify ActionListener
      (actionPerformed [this event]
        (action-fn event)))))

(defonce frame (JFrame. "Reloadable"))

(defonce state (atom [["ABC" 123]
                      ["U" "Me"]]))

(defn model [state]
  (proxy [DefaultTableModel] []
    (getRowCount [] (-> @state count))
    (getColumnCount [] (-> @state first count))
    (getValueAt [row col] (get-in @state [row col]))))

(doto frame
  (.setLayout (BorderLayout.))
  (.setJMenuBar (doto (JMenuBar.)
                  (.add (doto (JMenu. "File")
                          (.add (doto (JMenuItem. "Open...")
                                  (add-action (fn [_] (prn "load")))))
                          (.add (JMenuItem. "Save..."))
                          (.add (JMenuItem. "Save as..."))
                          (.add (JSeparator.))
                          (.add (doto (JMenuItem. "Exit")
                                  (add-action #(pp/pprint (bean %)))))))
                  (.add (doto (JMenu. "Edit")))
                  (.add (doto (JMenu. "Tools")))
                  (.add (doto (JMenu. "Help")))))
  (.add (JTable. (model state)) BorderLayout/CENTER)
  (.setSize 800 600)
  (.setVisible true)
  (.revalidate))


