 (ns sail.core
  (:use
   [rosado.processing :only [background-float stroke-weight stroke-float
                             frame-count]]
   [logo.processing-util :only [setup rerun-defapplet]]
   [sail.boat.course :only [three-leg-course draw-marks]]
   [sail.boat.boat-core :only [update-boat]]
   [sail.boat.draw :only [draw-boat]]
   [sail.boat.nodeps    :only [mk-boat]]
))



;; marks


(def boat-b (atom (mk-boat :destination {:x 100 :y 100}
                           :position {:x 50 :y 250}
                           :direction 100
                           )))
(def boat-a (atom (mk-boat :destination (:position @(nth three-leg-course 0))
                           :position {:x 50 :y 850}
                           :direction 100
                           )))

(defn sail-draw []
  
  (background-float  90 0 20) ;; redraws the background
  (draw-marks three-leg-course)
  (stroke-float 90)  ;; sets the turtle color
  (stroke-weight 20)  ;; sets the turtle size
  (reset! boat-a (update-boat @boat-a))
  (draw-boat @boat-a)
  (when (> (frame-count) 1000)
    (/ 1 0)))


(rerun-defapplet logo-play2 :title "logoemulation"
                 :size [800 800]
                 :setup setup :draw sail-draw)
