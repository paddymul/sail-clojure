(ns sail.boat.draw
  (:use
   [logo.draw   :only [draw-point]]
   ))

(defn draw-boat [boat]
  (draw-point ((boat :turtle) :position))

  )