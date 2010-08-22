(ns sail.boat.physics
  (:use
   [clojure.contrib.trace]
   [clojure.test :only [is deftest]]
   [clojure.contrib.math :only [abs]]
   [clojure.contrib.def :only [defnk ]]
   
   [logo.math :only [angle-diff -c +c]]
   [logo.turtle-prim :only
    [mk-turtle move-point-dir]] 
   
   [sail.boat.nodeps :only
    [mk-boat pcomment b-forward b-clockwise b-anti-clockwise]]
   [sail.boat.wind :only
    [can-sail can-point]]
   )

  (:require
   [clojure.contrib.math :as cmath]
   [logo.math :as lmath]
   [logo.turtle-prim :as logot]

   ))


(comment
  "non-sailor explanation"
    ;;
  ;;  for non-sailors, when looking towards the bow (front) of the
  ;;  boat starboard is on your right, port is on your left
  ;;
  ;;
  ;;  in the folowing picture there is a negative rudder angle
  ;;
  ;;         ^
  ;;       /   \
  ;;       |   |
  ;;       |   |
  ;;       |   |
  ;;       =====
  ;;         \
  ;;    
)

(defn boat-physics [boat sailing-environment]
  ;; a rudder angle of less than 0 means that the trailing edge of
  ;; the rudder is pointing to starboard, this will make the boat
  ;; turn to starboard
  (let [rudder-angle (:rudder-angle boat)
        boat-rotation (:rotation boat)
        ]
  (if (= rudder-angle 0)
    (if (can-sail boat sailing-environment)
      (b-forward boat (boat :maximum-possible-speed))
      boat)
    (let [turn-amount
          (if (< 0 rudder-angle)
            boat-rotation
            (- 0 boat-rotation))]
      (pcomment "turn-amount" turn-amount)
      (b-clockwise boat turn-amount)))))

(deftest boat-physics-test
  (is (= (boat-physics
          (mk-boat :rudder-angle 0
                   :direction 180 :position {:x 100 :y 100}
                   :pointing-angle 45
                   :speed 1)
          {:wind-direction 180})
         (mk-boat :rudder-angle 0
                  :direction 180 :position {:x 100 :y 101}
                  :pointing-angle 45
                  :speed 1)))

  (is (= (boat-physics
          (mk-boat :rudder-angle 0
                   :direction 180 :position {:x 100 :y 100}
                   :pointing-angle 45
                   :speed 1)
          {:wind-direction 0})
         (mk-boat :rudder-angle 0
                  :direction 180 :position {:x 100 :y 100}
                  :pointing-angle 45
                  :speed 1)))

  )
         
          




