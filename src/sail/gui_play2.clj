(ns sail.gui-play
  (:import (java.awt BorderLayout Color Dimension Font Toolkit)
	   (java.awt.event ActionListener KeyAdapter KeyEvent WindowAdapter
			   WindowListener)
	   (javax.swing BoxLayout JButton JDialog JFrame JLabel JPanel JSpinner
			SpinnerNumberModel Timer WindowConstants))

  (:use
   [rosado.processing :only [line frame-count]]
   [rosado.processing.applet :only [defapplet run]]
   [sail.gui.util :only
    [grid-bag-layout doto-symbol kill-frame
     with-change-listener
     text-field with-action init-applet]]))
(import '(javax.swing JFrame JLabel JTextField JButton JComboBox JPanel Timer)
        '(java.awt.event ActionListener)
        '(java.awt GridLayout GridBagLayout))


(defn draw-sort [canvas alg]
  (prn alg))

(def line-height (atom 50))
(def line-dest (atom 100))


(def initted-app (atom 1))
(defn reinit []
  (defapplet growing-triangle
    :draw (fn [] (line @line-height @line-height (frame-count) @line-dest)))
  (reset! initted-app (init-applet growing-triangle :size 500 700)))
(reinit)


(defn mk-algorithm-chooser []
  (doto (JComboBox.)
    (.addItem "Quick sort")
    (.addItem "Bubble sort")))
;;(defmacro mk-button  [button-text

(def original-left (atom 50))
(defn sortapp-proc []
  (let [
        main-frame (JFrame. "logoemulation")
        canvas (JPanel. true)
        algorithm-chooser (mk-algorithm-chooser)
        run-button (JButton. "Run Algorithm")
        stop-button (JButton. "stop app")
        monitor2 (mk-monitor "foo" (.frameCount @initted-app))
;;        left-time (JSpinner. (SpinnerNumberModel.
;;			      (int (/ @original-left 60)) 0 1000 1))
        left-time (JSpinner. (SpinnerNumberModel.
                              @original-left 0 1000 5))

        draw-frame (fn []
                     (doto main-frame
                       (.setContentPane
                        (doto (JPanel. (GridBagLayout.))
                          (grid-bag-layout
                           :gridx 5, :gridy 0 algorithm-chooser
                           :gridy 1 run-button
                           :gridy 2 stop-button
                           
                           :gridx 6 :gridy 1  monitor2
                           :gridy 2 left-time
                           :gridx 0, :gridy 0, :gridheight 10
                           :ipadx 800 :ipady 800 @initted-app)))
                       (.pack)
                       (.setVisible true)))
        ]

    (with-action run-button e
      ;;(reset! line-height (+ 4 @line-height))
      (reinit)
      (draw-frame)
      (draw-sort canvas
                 (.toString
                  (.getSelectedItem algorithm-chooser))))
    (with-change-listener left-time  e
;;    (with-action left-time  e
      (reset! original-left (.getValue left-time))
      (println @original-left)
      )

    (draw-frame)
))





(do
  (doto-symbol app kill-frame)
  (def app (sortapp-proc)))