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
                (make-count-predicate 150)
                starboard-tack-instructions
                ;;                [straight (mk-decreasing-distance-p dest)]
                straight-instructions
                )
        [port-tack-boat port-tack-boat-n]
        (sail-instructions
                (nodeps/mk-managed-boat :boat boat)
                sailing-environment
                (make-count-predicate 150)
                port-tack-instructions
                ;;[straight (mk-decreasing-distance-p dest)]
                straight-instructions
                )]
    [(:boat starboard-tack-boat) (:boat port-tack-boat)]))

(compute-tacks {:position {:x 50, :y 850},
                :direction 45, :speed 0, :rudder-angle 0,
                :rotation 1, :minimum-speed 0.1,
                :maximum-possible-speed 1, :pointing-angle 45}

               {:destination {:x 300, :y 100}}
                       {:wind-direction 180}
 )
 
       

(defn boat-prime [boat]
  (select-keys boat [:position :direction]))

  
(defnk mk-s-set [:direction 0
                 :position  {:x 100 :y 100}
                 :destination {:x 100 :y 100}
                 :wind-direction 180]
  [(mk-boat :direction direction :position position)
   {:destination destination}
   {:wind-direction wind-direction}])

(comment
(deftest test-compute-tacks
  (let [[s-boat s-notes s-se] (mk-s-set
                               :position {:x 150 :y 150})]
    (is (= 314
           (map boat-prime (compute-tacks s-boat s-notes s-se)))))
  (let [[s-boat s-notes s-se] (mk-s-set
                               :position {:x 50 :y 150})]
    (is (= 45
           (map boat-prime (compute-tacks s-boat s-notes s-se)))))))

(defn find-better-tack [boat notes sailing-environment
                        starboard-tack-boat port-tack-boat]
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
              (:position  starboard-tack-boat)
              (:position  port-tack-boat))

    (if (> 5 tack-dist-diff)
      (do
        ;;(println " chosing closer-tack" closer-tack)
        closer-tack)
      (do
        ;;(println "tack-a-dist tack-b-dist" starboard-tack-dist port-tack-dist)
        (if (<= starboard-tack-dist port-tack-dist)
          starboard-tack-heading port-tack-heading)))))

(deftest test-find-better-tack
  "figure out how to test for oscilation"
  (let [[s-boat s-notes s-se]
        (mk-s-set :position {:x 50 :y 150} :direction 0)]
    (is (=
         45
         (find-better-tack
          s-boat s-notes s-se
          (mk-boat :position {:x 100 :y 100} :direction 45)
          (mk-boat :position {:x 0 :y 100} :direction 314))))))
  

(defn lifted-tack [boat notes sailing-environment]
  " given the basic sailing variables returns the tack that will get
us closer to the mark "
  (let [[starboard-tack-boat port-tack-boat]
        (compute-tacks boat notes sailing-environment)]
    (find-better-tack
     boat notes sailing-environment
     starboard-tack-boat port-tack-boat)))

(deftest test-lifted-tack
  (let [[s-boat s-notes s-se]
        (mk-s-set
         :destination {:x 100 :y 100}
         :direction 0
         :position {:x 150 :y 150})]
    (is (= 314
           (lifted-tack s-boat s-notes s-se))))
  (let [[s-boat s-notes s-se]
        (mk-s-set :destination {:x 100 :y 100}
                  :position {:x 50 :y 150})]
    (is (= 46
           (lifted-tack s-boat s-notes s-se))))


  (let [[s-boat s-notes s-se]
        (mk-s-set :destination {:x 300, :y 100}
                  :position {:x 50, :y 850}                 
                  :direction 107)]
    (is (= 46
           (lifted-tack s-boat s-notes s-se)))))


(defn make-good-velocity [boat notes  sailing-environment]
  "this function's name is a play on velocity-made-good "
  (let [pos     (boat :position)
        dest    (notes :destination)
        dir     (boat :direction)]
    
    (println "---------make-velocity-good")
        (println "notes" notes)
        (println "pos" pos)
        (println "dir" dir)


        (let [mark-bearing (logot/bearing pos dest)
              mark-boat    (assoc boat :direction mark-bearing)
              can-we-sail  (can-sail
                            mark-boat
                            sailing-environment)
              ]
          (println "se " sailing-environment)
          (println "mark-bearing" mark-bearing)
          (println "can-we-sail" can-we-sail)
          (println "---------")
      (if can-we-sail
        (do 
          (println "if we can go straight to the mark we have it easy")
          (if (> 2 (cmath/abs (angle-diff dir mark-bearing)))
            (do
              (println "if we are pointing at the mark, go towards it!")
              [0 notes])
            (do
              ;;(println "mark-bearing direction"  mark-bearing dir)
              (println "let's start turning towards the mark")
              ;; hopefully the signs are correct
              [(updated-heading dir mark-bearing) notes])))
        (do
          (pcomment "aha life is interesting, the mark is upwind of us")
          (let [lifted-heading (lifted-tack boat notes sailing-environment)]
            (if (= dir lifted-heading)
              (do
                (println "if we are on the lifted-tack now, let's go forward")
                [0 notes])
              (do
                (println "otherwise, let's start turning towards the mark")
                [(updated-heading dir lifted-heading) notes]
                ))))))))

(comment
(deftest make-good-velocity-sanity
  (is (=
       (make-good-velocity {:position {:x 50, :y 850},
                            :direction 107, :speed 0, :rudder-angle 1,
                            :rotation 1, :minimum-speed 0.1,
                            :maximum-possible-speed 1, :pointing-angle 45}
                           {:destination {:x 300, :y 100}}
                           {:wind-direction 180})
       0)))
)

(defn update-marks [notes]
  ;;  (println " update marks ")
  (let [marks      (notes :marks)
        new-mark   (first marks)
        new-marks  (rest marks)]
    (merge notes {:marks new-marks :destination (:position new-mark)})))

(deftest update-marks-test

  (is (= {:destination {:x 30, :y 30}, :marks [{:position {:x 90, :y 90}}]}
         (update-marks {:marks [{:position {:x 30 :y 30}}
                                {:position {:x 90 :y 90}}]}))))


(defn boat-turn [boat notes sailing-environment]
;;  (println "mboat sailing-environment notes" boat sailing-environment notes)
  (let [
        pos     (boat  :position)
        dest    (notes :destination)
        dir     (boat :direction)]

        ;;    (println "boat-turn sailing-environment" boat sailing-environment notes)
    (if (< destination-resolution
           (point-distance dest pos))
      ;; if we aren't at the mark

      (make-good-velocity  boat    notes sailing-environment)
      ;; we are at the mark
      ;;for now we will pretend that reaching the
      ;; mark will take a whole turn, i'm lazy
      (do
        (println "calling-update-marks")
        [0 (update-marks notes)]))))


(deftest test-boat-turn
  (let [mb (mk-managed-boat :destination {:x 100 :y 100}
                            :position {:x 50 :y 50})
        boat (:boat mb)
        notes (:notes mb)]
    (is (= [1 notes] (boat-turn boat  notes  {:wind-direction 180})))))




(comment
  I think I should make some optimize functions, these will be useful with tactics estimator
  

(comment
(defn turn-into-irons [boat notes]
  (let [dir     ((boat :turtle) :direction)]
      (if (= dir (-c 180 wind-direction))
        [0  notes] ;; if we are pointing at the wind stay there
        [-1 notes] ;; otherwise turn into the wind, to starboard)
        )))
)

  )
