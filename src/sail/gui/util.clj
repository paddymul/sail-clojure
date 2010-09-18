(ns sail.gui.util
  (:use
      [rosado.processing :only [ JAVA2D OPENGL P3D P2D PDF toupper ]]))

(import '(javax.swing JFrame JLabel JTextField JButton JComboBox JPanel Timer)
        '(java.awt.event ActionListener)
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

(comment
  

(defn new-flipper []
  (agent {:total 0, :heads 0,
          :running false,
          :random (java.util.Random.)}))


(defn calculate [state]
  (if (:running state)
    (do (send *agent* calculate)
        (assoc state
          :total (inc (:total state))
          :heads (if (.nextBoolean (:random state))
                   (inc (:heads state))
                   (:heads state))))
    state))
(defn start [state]
  (send *agent* calculate)
  (assoc state :running true))

(defn stop2 [state]
  (assoc state :running false))
(defn error [state]
  (if (zero? (:total state)) 0.0
      (- (/ (double (:heads state))
            (:total state))
         0.5)))

  (defn flipper-app []
  ;; Construct components:
  (let [flipper (new-flipper)
        b-start (JButton. "Start")
        b-stop (doto (JButton. "Stop")
                 (.setEnabled false))
        total (text-field "0")
        heads (text-field "0")
        t-error (text-field "0.0")
        timer (Timer. 100 nil)]

    ;; Setup actions:
    (with-action timer e
      (let [state @flipper]
        (.setText total (str (:total state)))
        (.setText heads (str (:heads state)))
        (.setText t-error (format "%.10g" (error state)))))
    (with-action b-start e
      (send flipper start)
      (.setEnabled b-stop true)
      (.setEnabled b-start false)
      (.start timer))
    (with-action b-stop e
      (send flipper stop)
      (.setEnabled b-stop false)
      (.setEnabled b-start true)
      (.stop timer))

    ;; Create window and layout:
    (doto (JFrame. "logoemulation")
      (.setContentPane
       (doto (JPanel.)
         (.add (JLabel. "Total:"))
         (.add total)
         (.add (JLabel. "Heads:"))
         (.add heads)
         (.add (JLabel. "Error:"))
         (.add t-error)
         (.add b-start)
         (.add b-stop)))
      (.pack)
      (.setVisible true))))


(do (doto-symbol flipper-app-sym kill-frame)
    (def flipper-app-sym (flipper-app)))

)