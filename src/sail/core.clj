 (ns sail.core
  (:use
   [rosado.processing :only [frame-count]]
   [logo.processing-util :only [setup rerun-defapplet]]
   [sail.boat.course :only [three-leg-course draw-course]]
   [sail.boat.boat-core :only [update-boat]]
   [sail.boat.draw :only [draw-boat]]
   [sail.boat.nodeps    :only [mk-boat]]
))


(def boat-a (atom (mk-boat :destination (:position @(nth three-leg-course 0))
                           :position {:x 50 :y 850}
                           :direction 100
                           )))

(defn sail-draw []
  

  (draw-course three-leg-course)
  (reset! boat-a (update-boat @boat-a))
  (draw-boat @boat-a)
  (when (> (frame-count) 1000)
    (/ 1 0)))


(rerun-defapplet logo-play2 :title "logoemulation"
                 :size [800 800]
                 :setup setup :draw sail-draw)
