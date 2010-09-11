(clojure.core/use 'nstools.ns)
;;(ns+ unit-demo
(ns+ sail.units-play
  (:clone nstools.generic-math)
  (:from units dimension? in-units-of)
  (:require
             [units.si :as si]
             [units]
   ))


(units/defunit Nm "NauticalMile" (* si/m 1852))
(units/defunit feet "feet"       (* si/m 0.3048))

(def three-knots (* 3 Nm))
(def five-meter  (* 5 si/m))
(def one-hour    (* 1 si/h))
(units/defunit px "Pixels" (* si/m 500))
(units/defunit px "Pixels" (/ si/m 5))

(defn in-px [measurement]
  {:pre [(si/length? measurement)]}
  (units/in-units-of px measurement))

(defn raw-px [measurement]
  {:pre [(si/length? measurement)]}
  (/ (in-px measurement) px))



