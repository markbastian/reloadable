(ns reloadable.core
  (:require [clojure.pprint :as pp]
            [cheshire.core :as ch])
  (:import (javax.swing JFrame JTable JMenuBar JMenu JMenuItem JSeparator)
           (java.awt BorderLayout Color)
           (java.awt.event ActionListener)
           (javax.swing.table DefaultTableModel DefaultTableCellRenderer)))

(def cell-renderer
  (proxy [DefaultTableCellRenderer] []
    (getTableCellRendererComponent [table color selected? has-focus? row col]
      (let [s (proxy-super getTableCellRendererComponent table color selected? has-focus? row col)]
        (doto s
          (.setBackground (if (even? row) Color/LIGHT_GRAY Color/WHITE)))))))

(defn add-action [component action-fn]
  (.addActionListener
    component
    (reify ActionListener
      (actionPerformed [this event]
        (action-fn event)))))

(defonce frame (JFrame. "Reloadable"))

;Add a watch for when the data changes to refresh the table
(defonce state (atom [["ABC" 123]
                      ["U" "Me"]]))

(defn model [state]
  (let [m (proxy [DefaultTableModel] []
            (getRowCount [] (-> @state count))
            (getColumnCount [] (-> @state first count))
            (getValueAt [row col] (get-in @state [row col]))
            (setValueAt [o row col] (swap! state assoc-in [row col] o))
            (isCellEditable [row col] true)
            )]
    #_(add-watch state :table-model-updater
               (fn [_ _ o n]
                 (when (not= o n)
                   (doto m
                     (.fireTableDataChanged)
                     (.fireTableStructureChanged)))))
    m))

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
  (.add (doto (JTable. (model state))
          (.setGridColor Color/BLACK)
          (.setDefaultRenderer Object cell-renderer)
          )
        BorderLayout/CENTER)
  (.setSize 600 400)
  (.setVisible true)
  (.revalidate))


