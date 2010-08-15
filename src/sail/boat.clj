(ns sail.boat
  (:use
   ;;[rosado.processing]
   [logo.macrology]
   [logo.turtle]
   [clojure.test]
   [logo.turtle-prim :only [mk-turtle]]
   [logo.core]
   [logo.math]
   
   ))

(def wind-direction 180)
;; boat stuff
(def pointing-angle 45)

(defn -c [x y]
  (correct-angle (- x y)))

(defn angle-negative [angle]
  " given an angle between 0 and 360 returns an angle between -180 and
  180"
  (if (> angle 180)
    (- angle 360)
    angle))

(deftest angle-negative-test
  (is (= -30 (angle-negative 330)))
  (is (= 30 (angle-negative 30))))

(defn angle-diff [from to]
  (angle-negative (-c to from)))

(deftest angle-diff-test
  (is (= -30 (angle-diff 100 70)))
  (is (= 30 (angle-diff  70 100)))
  (is (= 30 (angle-diff  350 20))))

(defn can-sail [turtle]
  (let [angle-to-wind
        (clojure.contrib.math/abs
         (angle-diff (:direction turtle) (-c 180 wind-direction)))]
  (< pointing-angle angle-to-wind)))


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

