## Interactive Development with Reloadable Code
Mark Bastian
<br>November 9, 2017

----

# Overview
* Time
* Reloadable Code
* Solutions
* Demos

---

# The Time Problem

----

### Time: How much time do you spend...
* ...waiting for code to compile?
* ...getting your application into a testable/debuggable state?
* ...doing actual coding?

----

### Developer time is precious
* We want to maximize actual coding time
* This also minimizes time to deployment

----

### We spend most of our time in a development feature feedback loop
* You exit the loop when the feature is complete
* The loop starts again with a new feature or when you need to improve/debug an existing feature

----

### Development Models & The Feature Feedback Loop
| Model               | Cycle Time        | Tooling       | State |
| ------------------- | ----------------- |:-------------:| -----:|
| Edit, Compile, Run  | Minutes-Hours     | Static        | Lost  |
| Edit, Save, Refresh | Seconds-Minutes   | Dynamic       | Lost  |
| Edit, Save, Reload  | Immediate-Seconds | Dynamic       | Saved |

---

# The Solution: Reloadability

----

### What is Reloadable Code
<small>
* You make changes to your code
* You save or reload the file
* Changes are immediately propagated into your running client process and displayed
* Fundamental advantages
  * There is no need to refresh, restart, or otherwise interact with your client
  * State is preserved
* Contrast with Edit, Save, Refresh
  * You manually refresh/restart your client
  * State is lost
  
</small>

----

### Facilitating Reloadability
* State as a value
  * A simple representation that is easy to maintain
* Top-level behaviors decomplected from state
  * I can redefine functions without having to reload my value
* Interactivity
  * REPL: Read-Evaluate-Print-Loop
  * I can interactively and dynamically update both value and behavioral aspects of my application without the need to rebuild it

---

# Fundamentals

----

### Clojure(Script)
<small>
* Clojure is a modern Lisp dialect that is hosted on the JVM
  * Compiles to JVM bytecode
  * Data oriented and functional
* ClojureScript is a dialect of Clojure
  * Very few differences
  * Uses the Google Closure compiler to transpile Clojure code into JavaScript
* Both interop very well in both directions with their host platforms
  * Makes use of wide variety of existing libraries (Maven central, npm)
* Learn more at https://clojure.org/ and https://clojurescript.org/

</small>

----

### Data
```clojure
[{:a :map} #{:a :set} '(:a :list)]
```
----

### Behavior
```clojure
;A regular function
(defn my-function [arg-1 arg-2]
  (+ arg-1 arg-2))
  
;A lambda
#(+ %1 %2)
```
----

### Interop
Clojure
```clojure
(def frame (JFrame.))
```
Java
```java
JFrame frame = new JFrame();
```

---

# Solutions & Examples

----

## Solution: Reloadable Swing
* Define a single JFrame (e.g. using defonce)
* A REPL environment is required
* Can this be done with JShell?

----

## Solution: Quil
* “Clojure/ClojureScript library for creating interactive drawings and animations.” 
* http://quil.info
* sketches can be run as Java or JavaScript apps

----

## Solution: Figwheel
* "Figwheel builds your ClojureScript code and hot loads it into the browser as you are coding!"
* https://github.com/bhauman/lein-figwheel
* Can be used seamlessly with Reagent(React), Node, or plain JS
* Has a REPL and a host of other development and debugging tools
* Can use external client while coding

---

## Conclusions
<small>
* Reloadable code is a very effective and fun technique for rapid application development
* It drastically reduces development time and keeps the developer engaged
* Clojure(Script) approaches facilitate interactive development
  * Clojure's data-centric approach makes state continuity much easier than other approaches
  * Several options: REPL, Quil, Figwheel
* Give it a try!

</small>

---

# Extras

----

# Questions
* References
  * http://worrydream.com/dbx/
  * lighttable
  * quil
  * figwheel
  
----

## Some basic imports
```clojure
;I'm gonna use this stuff
(require 
  '[clojure.pprint :as pp]
  '[cheshire.core :as ch])
  
(import 
  '(javax.swing JFrame JTable JMenuBar JMenu JMenuItem JSeparator)
  '(java.awt BorderLayout)
  '(java.awt.event ActionListener)
  '(javax.swing.table DefaultTableModel))
```

----

## Defining the Frame
```clojure
;I'm gonna use this stiff
(defonce frame (JFrame. "Reloadable"))

(defonce state (atom [["ABC" 123]
                      ["U" "Me"]]))
```

----

## Basic Swing
```clojure
(doto frame
  (.setLayout (BorderLayout.))
  (.setSize 800 600)
  (.setVisible true)
  (.revalidate))
```

----

## Add a Table
```clojure
(defn model [state]
  (proxy [DefaultTableModel] []
    (getRowCount [] (-> @state count))
    (getColumnCount [] (-> @state first count))
    (getValueAt [row col] (get-in @state [row col]))))
    
(doto frame
  (.add (JTable. (model state)) BorderLayout/CENTER)
  (.revalidate))
```

----

## Add a Menubar
```clojure
(defn add-action [component action-fn]
  (.addActionListener
    component
    (reify ActionListener
      (actionPerformed [this event]
        (action-fn event)))))
        
(doto frame
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
  (.revalidate))
```
