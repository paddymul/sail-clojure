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


;; boat stuff

(defn can-point [dir wind-direction pointing-angle]
  (let [angle-to-wind
        (abs
         (angle-diff dir (-c 180 wind-direction)))]
    (< pointing-angle angle-to-wind)))

(defn can-sail [turtle sailing-environment pointing-angle]
  (can-point  (:direction turtle)
              (:wind-direction sailing-environment)
              pointing-angle
              ))


(defn pinch [heading wind-direction pointing-angle]
  (can-sail (mk-turtle :direction heading)
            {:wind-direction wind-direction}
            pointing-angle
            ))

(defn assert-can-sail [heading wind-direction pointing-angle ]
  (is (= true (pinch  heading  wind-direction pointing-angle))))

(defn assert-cannot-sail [heading wind-direction pointing-angle]
  (is (= false (pinch heading     wind-direction pointing-angle))))

(deftest test-can-sail
  (assert-can-sail      100 180 45)
  (assert-cannot-sail   0   180 45)
  (assert-cannot-sail   320 180 45)
  (assert-cannot-sail   40  180 45)   )


(def boat-movement 10)
(def boat-rotation 1)
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
  (let [rudder-angle (:rudder-angle boat)]
  (if (= rudder-angle 0)
    (if (can-point (:direction boat)
                   (:wind-direction sailing-environment)
                   (:pointing-angle boat)
                   )
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
         
          




