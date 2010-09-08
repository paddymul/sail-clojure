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



(def boat-a
     (atom
      (let [orig-boat
            (mk-managed-boat :destination
                             (:position
                              (nth sail.course.core/three-leg-course 0))
                             :position {:x 50 :y 850}
                             ;;:direction 45
                             :pointing-angle 44.5
                             :rotation 1.1
                             :maximum-possible-speed 3.3
                             )]
        (assoc orig-boat :notes
               (assoc 
                (:notes orig-boat)
                :marks sail.course.core/three-leg-course ))

        ))

     )

(defn sail-draw []
  

  (draw-course three-leg-course)
  (reset! boat-a (update-managed-boat @boat-a))
  (draw-boat (:boat @boat-a))
  (when (> (frame-count) 500)
    (/ 1 0)))

(defn run-app [] 
(rerun-defapplet logo-play2 :title "logoemulation"
                 :size [800 800]
                 :setup setup :draw sail-draw)
)
(println "hello")
(defn play []
  (doseq [a [0 1 2 3 4 5 6 7 8 9 10]]
    (reset! boat-a (update-managed-boat @boat-a))))
;;(play)
(run-app)