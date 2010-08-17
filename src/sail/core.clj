 (ns sail.core
  (:use
   [rosado.processing :only [background-float point
                             frame-count stroke-weight
                             no-stroke stroke-float
                             smooth
                             ]]
   [logo.processing-util :only [setup rerun-defapplet]]
   [logo.turtle-prim :only [mk-turtle]]
   [logo.draw   :only [draw-point draw-turtle forward! ]]
   [sail.boat.course :only [three-leg-course]]
   [sail.boat.boat-core :only [update-boat]]
   [sail.boat.nodeps    :only [mk-boat]]
))



;; marks

(defn draw-marks [course]
  (stroke-weight 9)  ;; sets the turtle size
  (doseq [a-mark course]
    (stroke-float 90 90 0)
    (draw-turtle a-mark)))


(def turtle-b (atom (mk-turtle :position {:x 200 :y 300} :direction 100)))

(def boat-b (atom (mk-boat :destination {:x 100 :y 100}
                           :position {:x 50 :y 250}
                           :direction 100
                           )))
(def boat-a (atom (mk-boat :destination (:position @(nth three-leg-course 0))
                           :position {:x 50 :y 850}
                           :direction 100
                           )))

;;(def boat-a (atom (mk-turtle :position {:x 200 :y 300} :direction 100)))
(println :start)
(defn sail-draw []
  
  (background-float  90 0 20) ;; redraws the background
  (draw-marks three-leg-course)
  
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
