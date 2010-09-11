(clojure.core/use 'nstools.ns)
(ns+  sail.core
  (:clone nstools.generic-math)
  (:from units dimension? in-units-of)

;;(ns sail.core
  (:use
   [clojure.contrib.trace]
   [rosado.processing    :only [frame-count]]
   [logo.processing-util :only [setup rerun-defapplet]]
   [sail.course.core     :only [three-leg-course]]
   [sail.course.draw     :only [draw-course]]
   [sail.boat.boat-core  :only [update-managed-boat]]
   ;;[sail.boat.draw       :only [draw-boat]]
   [sail.boat.draw       :only [draw-boat
                                draw-boat-unit
                                draw-forward-unit
                                ]]
   [sail.boat.nodeps     :only [mk-managed-boat]])
    (:require
                [units.si          :as si]
                [units]
                [sail.units-play    :as su])
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
                             :speed 2
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
;;(* 3 si/m)
(def unit-boat (mk-managed-boat
                :rudder-angle 1

                :position {:x (* 50 si/m) :y (* 80 si/m)}
                :direction 190))

(defn sail-draw []
  (draw-course three-leg-course)
  ;;(reset! boat-a (update-managed-boat @boat-a))
  ;;(draw-boat (:boat @boat-a))
  ;;(draw-forward-unit (:boat unit-boat) (* si/m 150))
  (draw-boat-unit (:boat unit-boat))
  (when (> (frame-count) 5)
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