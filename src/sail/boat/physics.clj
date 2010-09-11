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
    [can-sail can-point angle-to-wind]]
   )

  (:require
   [clojure.contrib.math :as cmath]
   [logo.math :as lmath]
   [logo.turtle-prim :as logot]
   [sail.boat.nodeps :as nodeps]
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

(defn sail-power [ang-to-wind]
  "eventually this should be a property of a boat, not the global
   environment "
  (let [rel-angle (cmath/abs ang-to-wind)]
    (cond (< rel-angle 30) -0.02
          (< rel-angle 60) 0.1
          (< rel-angle 120) 0.3
          (<= rel-angle 180) 0.2)))

(defn assure-boat-speed [boat]
  "this makes sure that the boat speed stays between the minimum boat
   speed and the maximum boat speed "
  (let [speed       (:speed boat)
        min-speed   (:minimum-speed boat)
        max-speed   (:maximum-possible-speed boat)]
    (if (< speed min-speed)
      min-speed
      (if (> speed max-speed)
        max-speed
        speed))))

  

(defn acceleration-boat-physics [boat sailing-environment]
  ;; a rudder angle of less than 0 means that the trailing edge of
  ;; the rudder is pointing to starboard, this will make the boat
  ;; turn to starboard
  (let [rudder-angle (:rudder-angle boat)
        boat-rotation (:rotation boat)
        turned-boat 
        (if (= rudder-angle 0)    
          boat
        (let [turn-amount
              (if (< 0 rudder-angle)
                boat-rotation
                (- 0 boat-rotation))]
          ;;(pcomment "turn-amount" turn-amount)
          (b-clockwise boat turn-amount)))
        old-boat-speed (assure-boat-speed boat)
        sp (sail-power (angle-to-wind
                        (:direction boat)
                        (:wind-direction sailing-environment)))

        new-boat-speed (+
                        old-boat-speed
                        (* old-boat-speed sp))
        ;;blah (println "sailpower new-boat-speed" sp new-boat-speed)
        ]
    (println "old-boat-speed" old-boat-speed new-boat-speed)
    (let [advanced-boat (b-forward turned-boat new-boat-speed)]
      (assoc advanced-boat :speed 
             new-boat-speed))))

(deftest acceleration-boat-physics-test
(is (= 1 (acceleration-boat-physics
          (mk-boat :rudder-angle 0
                   :direction 180 :position {:x 100 :y 100}
                   :pointing-angle 45
                   :speed 1)
          {:wind-direction 180}))))
    
      


(defn boat-physics [boat sailing-environment]
  ;; a rudder angle of less than 0 means that the trailing edge of
  ;; the rudder is pointing to starboard, this will make the boat
  ;; turn to starboard
  (let [rudder-angle (:rudder-angle boat)
        boat-rotation (:rotation boat)]
  (if (= rudder-angle 0)
    (if (can-sail boat sailing-environment)
      (b-forward boat (boat :maximum-possible-speed))
      boat)
    (let [turn-amount
          (if (< 0 rudder-angle)
            boat-rotation
            (- 0 boat-rotation))]
      ;;(pcomment "turn-amount" turn-amount)
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
  (is (= (boat-physics
          (mk-boat :rudder-angle 1
                   :direction 180 :position {:x 100 :y 100}
                   :pointing-angle 45
                   :speed 1)
          {:wind-direction 0})
         (mk-boat :rudder-angle 1
                  :direction 181 :position {:x 100 :y 100}
                  :pointing-angle 45
                  :speed 1)))

  )
         
          


(deftest physics-update-managed-boat-test
  (is (= (nodeps/mk-managed-boat)
         (nodeps/update-managed-boat
          (nodeps/mk-managed-boat)
          {:wind-direction 180}
          boat-physics (fn [boat sailing-environment notes]
                                 [0 notes]))))
  (is (= (nodeps/mk-managed-boat :rudder-angle 1 :direction 1)
         (nodeps/update-managed-boat
          (nodeps/mk-managed-boat)
          {:wind-direction 180}
          boat-physics (fn [boat sailing-environment notes] [1 notes])))))