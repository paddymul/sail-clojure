(clojure.core/use 'nstools.ns)
(ns+ sail.boat.draw
  (:clone nstools.generic-math)
  (:from units dimension? in-units-of)
  (:use
   [logo.draw   :only [draw-point draw-forward]]
   [logo.turtle-prim   :only [mk-turtle clockwise forward]]
   [sail.sail-units    :only [draw-forward-unit]]
   [rosado.processing :only [ stroke-weight stroke-float]]
   )
  (:require
   [sail.sail-unitsystem  :as si]
   [units]
   [sail.sail-units    :as su]
                [sail.boat.nodeps  :as nodeps]
   ))


(def unit-boat
     (nodeps/mk-managed-boat
      :position {:x (* 3 si/m) :y (* 8 si/m)}
      :direction 90))

(defn turtle-from-boat [boat]
  (mk-turtle :position (:position boat)
             :direction (:direction boat)))

(def rudder-exageration -30)
(defn t-stroke-float [turtle & args]
  (apply stroke-float args)
  turtle)


(def boat-magnification 2)

(defn draw-boat-unit [boat]
  (stroke-float 250 20 20)  ;; sets the turtle color
  (stroke-weight 5)  ;; sets the turtle size
  ;;(draw-forward-unit  boat si/five-meter)
  
  (let [exagerated-rudder (* rudder-exageration (:rudder-angle boat))
        forward (fn [boat dist] (draw-forward-unit
                                 boat
                                 (* boat-magnification (* si/feet dist))))
        ]
    (-> (mk-turtle :position (:position boat)
                   :direction (:direction boat))
        (forward  50)
        (clockwise 30)
        (forward  20)

        (clockwise 120)
        (forward     20)
        (clockwise  30)
        (forward       50)
        (clockwise 90)
        (forward      10)
        (clockwise 270)

        (t-stroke-float 90 50 30)
        (clockwise exagerated-rudder)
        (forward      20)
        (clockwise 180)
        (forward      20)
        (clockwise 270)
          (t-stroke-float 250 20 20)  ;; sets the turtle color
;;        (t-stroke-float 10 10 10)
        (clockwise (- exagerated-rudder))
        (forward       10)
        )))


        


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
