(ns sail.boat.wind
  (:use   [clojure.test :only [is deftest]])
  (:require
   [clojure.contrib.math :as cmath]
   [logo.math :as lmath]   ))


(defn angle-to-wind [dir wind-direction]
  (lmath/angle-diff dir (lmath/-c 180 wind-direction)))

(defn can-point [dir wind-direction pointing-angle]
  (< pointing-angle (cmath/abs (angle-to-wind dir wind-direction))))

(defn can-sail [boat sailing-environment]
  (can-point  (:direction boat)
              (:wind-direction sailing-environment)
              (:pointing-angle boat)
              ))

;; I don't like this name
(defn pinch [heading wind-direction pointing-angle]
  (can-sail {:direction heading :pointing-angle pointing-angle}
            {:wind-direction wind-direction}
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

(defn on-port-heading [dir wind-direction]
    (> 0 (angle-to-wind dir wind-direction)))

(defn on-starboard-heading [dir wind-direction]
  (not (on-port-heading dir wind-direction)))

(deftest on-starboard-heading-test
  (is (= true  (on-starboard-heading 359 180)))
  (is (= false (on-starboard-heading 1 180)))

  )