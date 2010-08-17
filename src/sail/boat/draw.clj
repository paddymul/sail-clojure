(ns sail.boat.draw
  (:use
   [logo.draw   :only [draw-point]]
   [rosado.processing :only [ stroke-weight stroke-float]]
   ))

(defn draw-boat [boat]
  (stroke-float 90)  ;; sets the turtle color
  (stroke-weight 20)  ;; sets the turtle size

  (draw-point ((boat :turtle) :position))

  )