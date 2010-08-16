
 (ns sail.core
  (:use
   ;;[rosado.processing]
   [logo.macrology]
   [logo.turtle]
   [logo.turtle-prim :only [mk-turtle]]
   [logo.draw]
   [logo.core]
   [logo.math]
   [sail.boat :only [update-boat mk-boat]]
   [rosado.processing :only [background-float point
                             frame-count stroke-weight
                             no-stroke stroke-float
                             smooth
                             ]]))



;; marks
(defn make-mark [x y]
  (atom (mk-turtle
         :position {:x x :y y}
         :direction 0)))
(def marks [(make-mark 300  100)  (make-mark 100  300)  (make-mark 400  300)])  

(defn draw-marks []
  (stroke-weight 9)  ;; sets the turtle size
  (doseq [a-mark marks]
    (stroke-float 90 90 0)
    (draw-turtle a-mark)))


(def turtle-b (atom (mk-turtle :position {:x 200 :y 300} :direction 100)))

(def boat-b (atom (mk-boat :destination {:x 100 :y 100}
                           :position {:x 50 :y 250}
                           :direction 100
                           )))
(def boat-a (atom (mk-boat :destination (:position @(nth marks 0))
                           :position {:x 50 :y 850}
                           :direction 100
                           )))

;;(def boat-a (atom (mk-turtle :position {:x 200 :y 300} :direction 100)))
(println :start)
(defn sail-draw []
  
  (background-float  90 0 20) ;; redraws the background
  (draw-marks)
  
  (stroke-float 90)  ;; sets the turtle color
  (stroke-weight 20)  ;; sets the turtle size
  (forward! turtle-b 20)
  ;;(draw-turtle turtle-b)
  ;;(println ((@boat-a :turtle) :position))
  ;;(reset! boat-a (boat-turn @boat-a))
  (reset! boat-a (update-boat @boat-a))
  (draw-point ((@boat-a :turtle) :position))
  ;;(println ((@boat-a :turtle) :position)
  ;;((@boat-a :turtle) :direction))
  (when (> (frame-count) 1000)
    (/ 1 0))


  )


(rerun-defapplet logo-play2 :title "logoemulation"
                 :size [800 800]
                 :setup setup :draw sail-draw)
