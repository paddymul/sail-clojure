(ns sail.gui.util
  (:use
      [rosado.processing :only [ JAVA2D OPENGL P3D P2D PDF toupper ]]))

(import '(javax.swing JFrame JLabel JTextField JButton JComboBox JPanel Timer)
        '(java.awt.event ActionListener)
        '(javax.swing.event ChangeListener)
        '(java.awt GridLayout))

(defmacro set-grid! [constraints field value]
  `(set! (. ~constraints ~(symbol (name field)))
         ~(if (keyword? value)
            `(. java.awt.GridBagConstraints
                ~(symbol (name value)))
            value)))
(defmacro grid-bag-layout [container & body]
  (let [c (gensym "c")
        cntr (gensym "cntr")]
    `(let [~c (new java.awt.GridBagConstraints)
           ~cntr ~container]
       ~@(loop [result '() body body]
           (if (empty? body)
             (reverse result)
             (let [expr (first body)]
               (if (keyword? expr)
                 (recur (cons `(set-grid! ~c ~expr
                                          ~(second body))
                              result)
                        (next (next body)))
                 (recur (cons `(.add ~cntr ~expr ~c)
                              result)
                        (next body)))))))))
(defmacro with-action [component event & body]
  `(. ~component addActionListener
      (proxy [java.awt.event.ActionListener] []
        (actionPerformed [~event] ~@body))))
(defmacro with-change-listener [component event & body]
  `(. ~component addChangeListener
      (proxy [javax.swing.event.ChangeListener] []
        (stateChanged [~event] ~@body))))
  
(import '(javax.swing JPanel JFrame JButton JTextField
                      JLabel Timer SwingUtilities))

(defn text-field [value]
  (doto (JTextField. value 15)
    (.setEnabled false)
    (.setHorizontalAlignment JTextField/RIGHT)))

(import '(javax.swing JFrame JPanel JButton)
        '(java.awt GridBagLayout Insets))
(defmacro doto-symbol [name-of-symbol func]
  " this macro will perform func against the symbol if it exists,
  otherwise it will do nothing, very useful for gui code"
  `(do
     (try
       ;; we use eval here because without it, the compiler will catch
       ;; the fact that app-name isn't defined the first time this is
       ;; run.  in effect we are using eval to circumvent compiler
       ;; correctness checking, since this is wrapped in a macro, i'm
       ;; not too concerned
       (~func (eval (symbol (str '~name-of-symbol))))
       (catch Exception e#
         ;(println e#)
         ;(println "aha you haven't started anything yet")
         ))))


(defn kill-frame [frame]
     (doto frame
       (.hide)
       (.dispose)))

(def #^{:private true}
     modes {:JAVA2D JAVA2D :OPENGL OPENGL
            :P3D P3D :P2D P2D :PDF PDF})

(defn init-applet "breaking out run2"
  [applet & interactive?]
  (.init applet)
  (let [m (.meta applet)
        [width height & mode] (or (:size m) [200 200])
        mode (if-let [kw (first mode)]
               (modes (-> kw name toupper keyword))
               JAVA2D)
        ]
    (.size applet width height mode)
    applet))
    
(defmacro mk-monitor [monitor-name & what-to-monitor]
  `(let [monitor-field# (text-field ~monitor-name)
         timer# (Timer. 100 nil)]
     (doto timer#
       (.start)
       (with-action e#
         (.setText monitor-field# (str ~@what-to-monitor))))
     monitor-field#
     ))

