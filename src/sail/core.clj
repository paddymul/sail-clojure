
(clojure.core/use 'nstools.ns)


(ns+  sail.core
  (:clone nstools.generic-math)
  (:from units dimension? in-units-of)

;;(ns sail.core
  (:use
   [clojure.contrib.trace]
   [rosado.processing    :only [frame-count]]
   [logo.processing-util :only [setup rerun-defapplet]])
    (:require   [units]

                [units.si  :as si]
                [sail.boat.nodeps]
                [sail.course.core]
                [sail.course.draw]
                [sail.boat.boat-core]
                [sail.boat.draw]
                )
  ;;  (:require [sail.course.core])
  )



(def boat-a
     (atom
      (let [orig-boat
            (sail.boat.nodeps/mk-managed-boat :destination
                             (:position
                              (nth sail.course.core/three-leg-course 0))
                             :position {:x (* 50 si/m) :y (* 850 si/m)}
                             ;;:position {:x 50 :y 850}
                             ;;:direction 45
                             :speed (* 2 si/m)
                             :pointing-angle 44.5
                             :rotation 1.1
                             :minimum-speed  (* 0.1 si/m)
                             :maximum-possible-speed (* 3.3 si/m)
                             )]
        (assoc orig-boat :notes
               (assoc 
                (:notes orig-boat)
                :marks sail.course.core/three-leg-course ))

        ))

     )
;;(* 3 si/m)
(def unit-boat (sail.boat.nodeps/mk-managed-boat
                :rudder-angle 1
                :position {:x (* 50 si/m) :y (* 80 si/m)}
                :direction 190))

(defn sail-draw []
  (sail.course.draw/draw-course sail.course.core/three-leg-course)
  (reset! boat-a (sail.boat.boat-core/update-managed-boat @boat-a))
  (sail.boat.draw/draw-boat-unit (:boat @boat-a))
  ;;(draw-boat-unit (:boat unit-boat))
  (when (> (frame-count) 50000)
    (/ 1 0)))

(defn run-app [] 
(rerun-defapplet logo-play2 :title "logoemulation"
                 :size [800 800]
                 :setup setup :draw sail-draw)
)
(println "hello")
(comment
(defn play []
  (doseq [a [0 1 2 3 4 5 6 7 8 9 10]]
    (reset! boat-a (update-managed-boat @boat-a))))
)
;;(play)
(run-app)