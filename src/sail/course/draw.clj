(ns sail.course.draw
  (:use
   [logo.draw   :only [draw-point]]
   [rosado.processing :only [background-float stroke-weight stroke-float]]
   
   )
  (:require [sail.sail-units    :as su]
  ))


(defn draw-marks [course]
  (stroke-weight 9)  ;; sets the turtle size
  (doseq [a-mark course]
    (stroke-float 90 90 0)
    ;;(su/draw-point-unit (:position a-mark))
    (su/draw-point-unit a-mark)
    ))

(defn draw-course [course]
  (background-float  90 0 20) ;; redraws the background
  (draw-marks course))