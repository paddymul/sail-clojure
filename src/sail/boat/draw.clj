(ns sail.boat.draw
  (:use
   [logo.draw   :only [draw-point draw-forward]]
   [logo.turtle-prim   :only [mk-turtle clockwise forward]]
   [rosado.processing :only [ stroke-weight stroke-float]]
   )
  (:require
   [sail.boat.nodeps     :as nodeps])
  )

(comment
(defn draw-boat [boat]
  (stroke-float 90)  ;; sets the turtle color
  (stroke-weight 20)  ;; sets the turtle size

  (draw-point (boat  :position))

  ))


(comment
(defn draw-boat [boat]
  (stroke-float 90)  ;; sets the turtle color
  (stroke-weight 20)  ;; sets the turtle size

  (let [t (mk-turtle :position (:position boat)
                          :direction (:direction boat))]
    (draw-forward (clockwise (draw-forward t 20) 45) 10)
    )))

(defn t-stroke-float [turtle & args]
  (apply stroke-float args)
  turtle)

(def rudder-exageration 30)
(defn draw-boat [boat]
  (stroke-float 90)  ;; sets the turtle color
  (stroke-weight 5)  ;; sets the turtle size
  (let [exagerated-rudder (* rudder-exageration (:rudder-angle boat))]
    (-> (mk-turtle :position (:position boat)
                   :direction (:direction boat))
        (draw-forward 50)
        (clockwise 30)
        (draw-forward  20)
        (clockwise 120)
        (draw-forward  20)
        (clockwise     30)
        (draw-forward 50)
        (clockwise 90)
        (draw-forward 10)
        (clockwise 270)
        (t-stroke-float 90 50 30)
        (clockwise exagerated-rudder)
        (draw-forward 20)
        (clockwise 180)
        (draw-forward 20)
        (clockwise 270)
        (t-stroke-float 10 10 10)
        (clockwise (- exagerated-rudder))
        (draw-forward 10)
        )))
(comment
    (stroke-float 20 20 20)
    (->

        
        
        (draw-forward 300)
                   
       ))