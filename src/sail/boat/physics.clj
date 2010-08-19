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


(def boat-movement 10)
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

(defn tactics-estimator-internal  [boat termination-predicate iteration-count ]
  (let [[rudder-angle should-terminate ]
        (apply termination-predicate [boat iteration-count])
        boat2 (boat-physics boat rudder-angle)]

    (if should-terminate
      boat
      (recur boat2 termination-predicate (+ iteration-count 1)))))

(defnk tactics-estimator [boat termination-predicate :iteration-count 0]
  (comment
    hopefully with tactics estimator I can build a cute little dsl
    that looks somehting like this
    ((tack-port) 50 )  meaning go on a port tack and then 50 steps afterwards
    the great thing about this is, I can use this to estimate other boats progress too
    this will be helpfull with search based approaches to the problem

    I am enjoying the constraints of functional programing here

    maybe for right now this is a bit advanced something like tactics
    estimator should be built before I have a more advanced physics
    model in place

    right now physics are so dirt simple that I can model them quickly
    inside of lifted-tack that won't always be the case

    )
  (tactics-estimator-internal boat termination-predicate iteration-count)
  )

(deftest estimator-test

   (tactics-estimator (mk-boat) (fn [boat itercount] [0 (> 3 itercount)]))
 (tactics-estimator (mk-boat :direction 50) (fn [boat itercount]
                                  (println boat)
                                  [0 (< 3 itercount)]))
  )
;;(trace (tactics-estimator (mk-boat) (fn [boat itercount] [0 (> 3 itercount)])))

;;  (trace)