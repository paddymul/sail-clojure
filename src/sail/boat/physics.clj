(ns sail.boat.physics
  (:use
   ;;[rosado.processing]
   [logo.macrology]
   [logo.turtle]
   [clojure.test]
   [logo.turtle-prim :only
    [mk-turtle
     move-point-dir bearing
     forward clockwise anti-clockwise]]
   [clojure.contrib.def]
   [sail.boat.utility]
   [logo.core]
   [logo.math]
   ))

(defn boat-physics [boat rudder-angle]
  ;; a rudder angle of less than 0 means that the trailing edge of
  ;; the rudder is pointing to starboard, this will make the boat
  ;; turn to starboard
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
  (if (= rudder-angle 0)
    ;; aha this is wrong, I need a test to make sure we are not in irons
    (b-forward boat boat-movement)
    (let [turn-amount
          (if (< 0 rudder-angle)
            boat-rotation
            (- 0 boat-rotation))]
      (pcomment "turn-amount" turn-amount)
      (b-clockwise boat turn-amount))))
