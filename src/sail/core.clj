
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

                ;;[units.si  :as si]
                [sail.sail-unitsystem  :as si]
                [sail.boat.nodeps]
                [sail.course.core]
                [sail.course.draw]
                [sail.boat.boat-core]
                [sail.boat.draw]
                )
  ;;  (:require [sail.course.core])
  )



(def timer (atom (* si/s 1)))
(def time-delta (* si/s 0.3))
(def boat-a
     (atom
      (let [orig-boat
            (sail.boat.nodeps/mk-managed-boat :destination
                             (:position
                              (nth sail.course.core/three-leg-course 0))
                             :position {:x (* 50 si/m) :y (* 850 si/m)}
                             :speed (/ (si/Nm 50) (si/h 1))
                             :direction 0
                             :pointing-angle 44.5
                             :rotation (/ (si/deg 360) (si/s 60))
                             ;;:minimum-speed  (* 0.1 si/m)
                             :minimum-speed  (/ (si/Nm 1) (si/h 1))
                             ;;:maximum-possible-speed (* 3.3 si/m)
                             :maximum-possible-speed (/ (si/Nm 16.3) (si/h 1))
                             )]
        (assoc orig-boat :notes
               (assoc 
                (:notes orig-boat)
                :marks sail.course.core/three-leg-course )))))

(def unit-boat (sail.boat.nodeps/mk-managed-boat
                :rudder-angle 1
                :position {:x (* 50 si/m) :y (* 80 si/m)}
                :direction 190))

(defn sail-draw []
  (sail.course.draw/draw-course sail.course.core/three-leg-course)
  
  (reset! boat-a (sail.boat.boat-core/update-managed-boat @boat-a time-delta))
  (reset! timer (+ @timer time-delta))
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