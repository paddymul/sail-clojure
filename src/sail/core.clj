(ns sail.core
  (:use
   [clojure.contrib.trace]
   [rosado.processing    :only [frame-count]]
   [logo.processing-util :only [setup rerun-defapplet]]
   [sail.course.core     :only [three-leg-course]]
   [sail.course.draw     :only [draw-course]]
   [sail.boat.boat-core  :only [update-managed-boat]]
   [sail.boat.draw       :only [draw-boat]]
   [sail.boat.nodeps     :only [mk-managed-boat]])
  ;;  (:require [sail.course.core])
  )



(def boat-a (atom
             (mk-managed-boat :destination
                              (:position
                               (nth sail.course.core/three-leg-course 0))
                           :position {:x 50 :y 850}
                           :direction 45
                           )))

(reset! boat-a (let [b @boat-a
                     notes (:notes b)]
                 (assoc b :notes 
                        (assoc notes :marks three-leg-course))))

(defn sail-draw []
  

  (draw-course three-leg-course)
  (reset! boat-a (update-managed-boat @boat-a))
  (draw-boat (:boat @boat-a))
  (when (> (frame-count) 1000)
    (/ 1 0)))


(rerun-defapplet logo-play2 :title "logoemulation"
                 :size [800 800]
                 :setup setup :draw sail-draw)
