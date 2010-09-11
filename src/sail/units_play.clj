(clojure.core/use 'nstools.ns)
;;(ns+ unit-demo
(ns+ sail.units-play
  (:clone nstools.generic-math)
  (:from units dimension? in-units-of)
  (:require [units.si :as si]
            [units]))

(units/defunit Nm "NauticalMile" (* si/m 1852))


(def three-knots (* 3 Nm))
(def five-meter  (* 5 si/m))
(def one-hour    (* 1 si/h))
(in-units-of si/m (- three-knots five-meter))
(units/defunit px "Pixels" (* si/m 500))
(units/defunit px "Pixels" (/ si/m 500))




