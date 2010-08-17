(ns sail.boat.physics
  (:use
   [clojure.test :only [is deftest]]
   [sail.boat.nodeps :only
    [mk-boat pcomment b-forward b-clockwise b-anti-clockwise]]
   [clojure.contrib.math :only [abs]]
   [logo.math :only [angle-diff -c +c]]
   [logo.turtle-prim :only
    [mk-turtle move-point-dir]] 
   )
  
  (:require
   [clojure.contrib.math :as cmath]
   [logo.math :as lmath]
   [logo.turtle-prim :as logot]

   ))

(def wind-direction 180)
;; boat stuff
(def pointing-angle 45)

(defn can-point [dir]
  (let [angle-to-wind
        (abs
         (angle-diff dir (-c 180 wind-direction)))]
    (< pointing-angle angle-to-wind)))

(defn can-sail [turtle]
  (can-point  (:direction turtle)))


(defn pinch [wind-direction2 heading]
  (can-sail (mk-turtle :direction heading)))

(defn assert-can-sail [wind-direction2 heading]
  (is (= true (pinch  wind-direction2 heading))))

(defn assert-cannot-sail [wind-direction2 heading]
  (is (= false (pinch  wind-direction2 heading))))

(deftest test-can-sail
  (assert-can-sail  180 100)
  (assert-cannot-sail  180 0)
  (assert-cannot-sail  180 320)
  (assert-cannot-sail  180 40))

(def destination-resolution 5)
(def boat-movement 5)
(def boat-rotation 1)

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
