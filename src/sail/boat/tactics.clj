(ns sail.boat.tactics
  (:use
   [clojure.test :only [is deftest]]
   [clojure.contrib.def :only [defnk ]]
   [logo.math :only [angle-diff -c +c point-distance]]
   [logo.turtle-prim :only
    [ move-point-dir]] 
   [sail.boat.nodeps :only
    [mk-managed-boat pcomment b-forward b-clockwise b-anti-clockwise]]
   [sail.boat.wind :only [ can-sail]]
   [sail.boat.tactics-estimator :only [port-tack-instructions
                                       starboard-tack-instructions
                                       straight-instructions straight
                                       make-count-predicate
                                       or-predicates
                                       mk-decreasing-distance-p
                                       sail-instructions
                                       ]]
   [sail.boat.nodeps :as nodeps]
   )
  
  (:require
   [clojure.contrib.math :as cmath]
   [logo.math :as lmath]
   [logo.turtle-prim :as logot]

   ))
(def destination-resolution 5)

(defn updated-heading [current-heading mark-bearing]
  "returns the angle that should be turned to point at mark "
  (let [turn-needed (angle-diff current-heading mark-bearing)]
    (if (> 0 turn-needed)
      -1
      1)))


(deftest test-updated-heading
  (is (= 1
         (updated-heading  315 45))))


(def tack-outlook 500)
(comment
(defn lifted-tack [boat notes wind-direction]
  "determines which tack will get us closer to the mark

   it does this by extrapolating how far from the mark you would be if
   you stayed on each tack for 10 * the boat movement figure, maybe it
   would be better to just compare the mark angle to the pointing
   angle???

   I'm worried that this function could end up oscilating very quickly
   so that the boat tacks back and forth without putting much time in
   on any one tack

   I think I could make it better by taking into account the current
   position, and how long we expect it to take to tack over to the
   other position

   then it becomes a comparison not of which tack will get us closer
   to the mark, but which course of actions in t+ x will get us closer
   to the mark

"
  (let [pos     (boat :position)
        dest    (notes :destination)
        dir     (boat  :direction)
        pointing-angle (boat :pointing-angle)
        tack-a  (-c (-c 180 wind-direction) pointing-angle)
        tack-b  (+c (-c 180 wind-direction) pointing-angle)
        tack-a-turn (cmath/abs (angle-diff  dir tack-a))
        tack-b-turn (cmath/abs (angle-diff  dir tack-b))
        closer-tack (if (< tack-a-turn tack-b-turn)
                      tack-a tack-b)
        mark-distance (point-distance pos dest)
        tack-outlook  (+ mark-distance 20)
        tack-a-pos (move-point-dir
                    pos tack-a
                    (* (* boat-rotation (- tack-outlook tack-a-turn))
                       boat-movement))
        tack-b-pos (move-point-dir
                    pos tack-b
                    (* (* boat-rotation (- tack-outlook tack-b-turn))
                       boat-movement))


        tack-a-dist (point-distance tack-a-pos dest)
        tack-b-dist (point-distance tack-b-pos dest)
        tack-dist-diff  (cmath/abs (- tack-a-dist tack-b-dist))]
    (pcomment "tack-a-turn tack-a-dist" tack-a-turn tack-a-dist)
    (pcomment "tack-b-turn tack-b-dist tack-dist-diff"
              tack-b-turn tack-b-dist tack-dist-diff)

    (if (> 5 (cmath/abs (- tack-a-dist tack-b-dist)))
      (do
        (pcomment " chosing closer-tack" closer-tack)
        closer-tack)
      (do
        (pcomment "tack-a-dist tack-b-dist" tack-a-dist tack-b-dist)
        (if (>= tack-a-dist tack-b-dist)
          tack-a
          tack-b)))))
)

(defn closer-angle [to angle-a angle-b]
  (let [angle-a-diff
        (cmath/abs (angle-diff  to angle-a))
        angle-b-diff
        (cmath/abs (angle-diff  to angle-b))]
    (if (< angle-a-diff angle-b-diff)
      angle-a
      angle-b)))
                   

(defn compute-tacks [boat notes sailing-environment]
  (let [
        dest    (notes :destination)
        [starboard-tack-boat starboard-tack-boat-n]
        (sail-instructions
         (nodeps/mk-managed-boat :boat boat)
                sailing-environment
                (make-count-predicate 100)
                starboard-tack-instructions
                [straight (mk-decreasing-distance-p dest)]
                )
        [port-tack-boat port-tack-boat-n]
        (sail-instructions
                (nodeps/mk-managed-boat :boat boat)
                sailing-environment
                (make-count-predicate 100)
                port-tack-instructions
                [straight (mk-decreasing-distance-p dest)])]
    [(:boat starboard-tack-boat) (:boat port-tack-boat)]))

(defn boat-prime [boat]
  (select-keys boat [:position :direction]))

(comment
  "I get nasty numbers here "
(deftest test-compute-tacks
  (let [mb (mk-managed-boat :destination {:x 100 :y 100}
                            :position {:x 150 :y 150})
        boat (:boat mb)
        notes (:notes mb)
        se {:wind-direction 180}]
    (is (= 314
           (map boat-prime (compute-tacks boat notes se)))))
  (let [mb (mk-managed-boat :destination {:x 100 :y 100}
                            :position {:x 50 :y 150})
        boat (:boat mb)
        notes (:notes mb)
        se {:wind-direction 180}]
    (is (= 45 
           (map boat-prime (compute-tacks boat notes se))))))
)

(defn find-better-tack [boat notes sailing-environment
                        starboard-tack-boat port-tack-boat]
  (let [dir     (boat  :direction)
        dest    (notes :destination)
        starboard-tack-heading  (:direction starboard-tack-boat)
        port-tack-heading   (:direction port-tack-boat)
        closer-tack (closer-angle dir starboard-tack-heading port-tack-heading)
        starboard-tack-dist (point-distance
                             (:position  starboard-tack-boat)
                             dest)
        port-tack-dist (point-distance
                        (:position  port-tack-boat)
                        dest)
        tack-dist-diff (cmath/abs (- starboard-tack-dist port-tack-dist))]
    (pcomment "tack-a-pos tack-b-pos"
              (:position (:boat starboard-tack-boat))
              (:position (:boat port-tack-boat)))

    (if (> 5 tack-dist-diff)
      (do
        (pcomment " chosing closer-tack" closer-tack)
        closer-tack)
      (do
        (pcomment "tack-a-dist tack-b-dist" starboard-tack-dist port-tack-dist)
        (if (>= starboard-tack-dist port-tack-dist)
          starboard-tack-heading port-tack-heading)))))

(defnk mk-s-set [:direction 0
                 :position  {:x 100 :y 100}
                 :destination {:x 100 :y 100}
                 :wind-direction 180]
  [(mk-boat :direction direction :position position)
   {:destination destination}
   {:wind-direction wind-direction}])

(deftest test-find-better-tack
  (let [[s-boat s-notes s-se]
        (mk-s-set :position {:x 50 :y 150} :direction 0)]

    (is (=
         45
         (find-better-tack
          s-boat s-notes s-se
          (mk-boat :position {:x 100 :y 100} :direction 45)
          (mk-boat :position {:x 0 :y 100} :direction 45))))))

                         
                         


(defn lifted-tack2 [boat notes sailing-environment]
  (let [[starboard-tack-boat port-tack-boat]
        (compute-tacks boat notes sailing-environment)]
    (find-better-tack
     boat notes sailing-environment
     starboard-tack-boat port-tack-boat)))
(comment
(deftest test-lifted-tack
  (let [mb (mk-managed-boat :destination {:x 100 :y 100}
                            :position {:x 150 :y 150})
        boat (:boat mb)
        notes (:notes mb)
        se {:wind-direction 180}]
    (is (= 314
           (lifted-tack2 boat notes se))))
  (let [mb (mk-managed-boat :destination {:x 100 :y 100}
                            :position {:x 50 :y 150})
        boat (:boat mb)
        notes (:notes mb)
        se {:wind-direction 180}]
    (is (= 45 
           (lifted-tack2 boat notes se)))))
)
(defn make-good-velocity [boat sailing-environment notes]
  "this function's name is a play on velocity-made-good "
  (let [pos     (boat :position)
        dest    (notes :destination)
        dir     (boat :direction)]
    
    (let [mark-bearing (logot/bearing pos dest)]
      (if (can-sail (assoc boat :direction mark-bearing) sailing-environment)
        (do 
          (pcomment "if we can go straight to the mark we have it easy")
          (if (> 3 (clojure.contrib.math/abs (angle-diff dir mark-bearing)))
            (do
              (pcomment "if we are pointing at the mark, go towards it!")
              [0 notes])
            (do
              (println "mark-bearing direction"  mark-bearing dir)
              (pcomment "let's start turning towards the mark")
              ;; hopefully the signs are correct
              [(updated-heading dir mark-bearing) notes])))
        (do
          (pcomment "aha life is interesting, the mark is upwind of us")
          (let [lifted-heading (lifted-tack boat notes)]
            (if (= dir lifted-heading)
              (do
                (pcomment "if we are on the lifted-tack now, let's go forward")
                [0 notes])
              (do
                (pcomment "otherwise, let's start turning towards the mark")
                [(updated-heading dir lifted-heading) notes]
                ))))))))
(comment
(defn turn-into-irons [boat notes]
  (let [dir     ((boat :turtle) :direction)]
      (if (= dir (-c 180 wind-direction))
        [0  notes] ;; if we are pointing at the wind stay there
        [-1 notes] ;; otherwise turn into the wind, to starboard)
        )))
)

(defn update-marks [notes]
  (println " update marks ")
  (let [marks      (notes :marks)
        new-mark   (first marks)
        new-marks  (rest marks)]
    (merge notes {:marks new-marks :destination (:position new-mark)})))

(deftest update-marks-test

  (is (= {:destination {:x 30, :y 30}, :marks [{:position {:x 90, :y 90}}]}
         (update-marks {:marks [{:position {:x 30 :y 30}}
                                {:position {:x 90 :y 90}}]}))))


(defn boat-turn [boat sailing-environment notes]
  (let [pos     (boat  :position)
        dest    (notes :destination)
        dir     (boat :direction)]
    (println "boat-turn-variables" pos dest dir notes)
    (if (< destination-resolution
           (point-distance dest pos))
      ;; if we aren't at the mark
      (make-good-velocity  boat sailing-environment notes)
      ;; we are at the mark
      ;;for now we will pretend that reaching the
      ;; mark will take a whole turn, i'm lazy
      [0 (update-marks notes)])))


(deftest test-boat-turn
  (let [mb (mk-managed-boat :destination {:x 100 :y 100}
                            :position {:x 50 :y 50})
        boat (:boat mb)
        notes (:notes mb)]
    (is (= [1 notes] (boat-turn boat {:wind-direction 180}  notes)))))




(comment
  I think I should make some optimize functions, these will be useful with tactics estimator
  

  )