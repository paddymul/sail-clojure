(ns sail.gui-play
  (:use
   [rosado.processing :only [line frame-count]]
   [rosado.processing.applet :only [defapplet run]]
   [sail.gui.util :only [grid-bag-layout doto-symbol kill-frame
                         text-field with-action init-applet]]))
(import '(javax.swing JFrame JLabel JTextField JButton JComboBox JPanel Timer)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout GridBagLayout))


(defn draw-sort [canvas alg]
  (prn alg))

(def line-height (atom 50))
(def line-dest (atom 100))

(defapplet growing-triangle
  :draw (fn [] (line @line-height @line-height (frame-count) @line-dest)))

(def initted-app (atom (init-applet growing-triangle)))
(defn reinit []
  (reset! initted-app (init-applet growing-triangle :size 500 700)))
(reinit)


(defn mk-algorithm-chooser []
  (doto (JComboBox.)
    (.addItem "Quick sort")
    (.addItem "Bubble sort")))

(defn sortapp-proc []
  (let [canvas (JPanel. true)
        algorithm-chooser (mk-algorithm-chooser)
        run-button (JButton. "Run Algorithm")
        monitor2 (mk-monitor "foo" (.frameCount @initted-app))
        ]
    (with-action run-button e
      (reset! line-height (+ 4 @line-height))
      (reinit)
      (draw-sort canvas
                 (.toString
                  (.getSelectedItem algorithm-chooser))))
      (doto (JFrame. "logoemulation")
        (.setContentPane
         (doto (JPanel. (GridBagLayout.))
           (grid-bag-layout
            :gridx 5, :gridy 0 algorithm-chooser
            :gridy 1 run-button
            :gridy 3 monitor2
            :gridx 0, :gridy 0, :gridheight 10
            :ipadx 800 :ipady 800 @initted-app)))
        (.pack)
        (.setVisible true))))





(do
  (doto-symbol app kill-frame)
  (def app (sortapp-proc)))

