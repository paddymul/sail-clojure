(clojure.core/use 'nstools.ns)
;;(ns+ unit-demo
(ns+ sail.sail-units
  (:clone nstools.generic-math)
  (:from units dimension? in-units-of)
  (:use    [logo.draw   :only [draw-point draw-forward]]
           [logo.turtle-prim   :only [mk-turtle clockwise forward]]
           )
     (:require [clojure.contrib.generic.arithmetic :as ga]
	    [clojure.contrib.generic.comparison :as gc]
	    [clojure.contrib.generic.math-functions :as gm]
            [sail.sail-unitsystem  :as si]
;;             [units.si :as si]
             [units]
   ))



(def three-knots (* 3 si/Nm))
(def five-meter  (* 5 si/m))
(def one-hour    (* 1 si/h))
(units/defunit px "Pixels" (* si/m 500))
(units/defunit px "Pixels" (* si/m 1))
;;(units/defdimension revs "rpm" rpm [si/angle 1 time -1])



(defn in-px [measurement]
  {:pre [(si/length? measurement)]}
  (units/in-units-of px measurement))

(defn raw-px [measurement]
  {:pre [(si/length? measurement)]}
  (/ (in-px measurement) px))




(defn raw-turtle [turtle]
  (let [pos (:position turtle)]
    {:direction (:direction turtle)
     :position
           {:x         (raw-px (:x pos))
            :y         (raw-px (:y pos))}}))
(defn unit-turtle [turtle]
  (mk-turtle :position {:x (px (:x (:position turtle)))
                        :y (px (:y (:position turtle)))}
             :direction (:direction turtle)))

(defn draw-forward-unit [turtle dist]
  {:pre [(si/length? dist)]}
  (unit-turtle
   (draw-forward (raw-turtle turtle)
                 (raw-px dist))))
(defn draw-point-unit [turtle]
   (draw-point (:position (raw-turtle turtle))))

