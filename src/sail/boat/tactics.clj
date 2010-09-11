(ns sail.boat.tactics
  (:use
   [clj-stacktrace.repl]
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
   [sail.boat.tactics-estimator :as te]
   [sail.boat.wind :as wind]

   ))
(def destination-resolution 5)

(defn updated-heading [current-heading mark-bearing]
  "returns the angle that should be turned to point at mark "
  (let [turn-needed (angle-diff current-heading mark-bearing)]
    (if (= 0 turn-needed)
      0
      (if (> 0 turn-needed)
        -1
        1
        ))))


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






(defnk mk-s-set [:direction 0
                 :position  {:x 100 :y 100}
                 :destination {:x 100 :y 100}
                 :wind-direction 180]
  [(mk-boat :direction direction :position position)
   {:destination destination}
   {:wind-direction wind-direction}])

(comment
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

")


(defn closer-to-mark [current-dir
                      starboard-tack-dist starboard-tack-heading
                      port-tack-dist port-tack-heading]
  (comment
    (println "closer-to-mark")
    (println "starboard-tack-heading"  starboard-tack-heading)
    (println "port-tack-heading"  port-tack-heading)
    )
  (let [
        closer-tack (closer-angle current-dir
                                  starboard-tack-heading port-tack-heading)
        tack-dist-diff (cmath/abs (- starboard-tack-dist port-tack-dist))]
    (if (> 190 tack-dist-diff)
      (do
;;        (println " chosing closer-tack" closer-tack)
        closer-tack)
  
    (do
;;        (println "tack-a-dist tack-b-dist" starboard-tack-dist port-tack-dist)
        (if (< starboard-tack-dist port-tack-dist)
          (do
;;            (println starboard-tack-dist port-tack-dist starboard-tack-heading)
            starboard-tack-heading)
          port-tack-heading)))))

(comment closer-to-mark-test
         (is (= 45
                (closer-to-mark 0  0 45 100  314))))


(defn find-better-tack [boat sailing-environment notes 
                        starboard-tack-boat port-tack-boat]

  (let [dir     (boat  :direction)
        dest    (notes :destination)
        starboard-tack-heading  (:direction starboard-tack-boat)
        port-tack-heading   (:direction port-tack-boat)
        starboard-tack-dist (point-distance
                             (:position  starboard-tack-boat)
                             dest)
        port-tack-dist (point-distance
                        (:position  port-tack-boat)
                        dest)]
    (closer-to-mark dir
                    starboard-tack-dist starboard-tack-heading
                    port-tack-dist port-tack-heading)))


(deftest test-find-better-tack
  "figure out how to test for oscilation"
  (is (= 45
         (let [[s-boat s-notes s-se]
               (mk-s-set :position {:x 50 :y 150} :direction 0)]
           (find-better-tack
            s-boat s-se  s-notes
            (mk-boat :position {:x 100 :y 100} :direction 45)
            (mk-boat :position {:x 0 :y 100} :direction 314))))))


(defn boat-prime [boat]
  (select-keys boat [:position :direction]))

(defn boat-close-enough [desired-boat boat]
  (let [distance-err 3
        a-boat (:boat boat)
        dist (point-distance (:position desired-boat) (:position a-boat))
        dir   (= (:direction desired-boat) (:direction a-boat))]
    (if (and (< dist distance-err) dir)
      true
      (do
        (println "expected :" desired-boat)
        (println "got      :" (boat-prime a-boat))
        false))))




(defn compute-starboard-tack [boat  sailing-environment notes]
  (let [dest    (notes :destination)]
    (first (sail-instructions (nodeps/mk-managed-boat :boat boat)
                              sailing-environment (make-count-predicate 150)
                              starboard-tack-instructions
                              [straight (mk-decreasing-distance-p dest)]
                              ;;straight-instructions
                              ))))


(deftest test-compute-tacks
  (is (boat-close-enough
       {:position {:x 75 :y 75} :direction 314}
       (let [[s-boat s-notes s-se] (mk-s-set
                                    :position {:x 150 :y 150})]
         (compute-starboard-tack s-boat s-se s-notes))))
  (is  (boat-close-enough
        {:position {:x 75 :y 75} :direction 314}
        (let [[s-boat s-notes s-se]
              (mk-s-set :direction 314 :position {:x 150 :y 150})]
          (compute-starboard-tack s-boat s-se s-notes)))
       ))




(comment
  (let [[s-boat s-notes s-se] (mk-s-set
                               :position {:x 50 :y 150})]
    (is (= 45
           (map boat-prime (compute-tacks s-boat s-se s-notes )))))

  (compute-tacks {:position {:x 50, :y 850},
                  :direction 45, :speed 0, :rudder-angle 0,
                  :rotation 1, :minimum-speed 0.1,
                  :maximum-possible-speed 1, :pointing-angle 45}
                 {:wind-direction 180}
                 {:destination {:x 300, :y 100}}

                 )
  )

(defn compute-port-tack [boat  sailing-environment notes]
  (let [dest    (notes :destination)]
    (first (sail-instructions (nodeps/mk-managed-boat :boat boat)
                              sailing-environment
                              (make-count-predicate 150)
                              port-tack-instructions
                              [straight (mk-decreasing-distance-p dest)]
                              ;;straight-instructions
                              ))))

(defn compute-tacks [boat  sailing-environment notes]
  [(:boat (compute-starboard-tack boat  sailing-environment notes))
;;   {:position {:x 9000 :y 9000} :direction 45}
  (:boat (compute-port-tack boat  sailing-environment notes))

   ])



(defn lifted-tack [boat sailing-environment notes ]
  " given the basic sailing variables returns the tack that will get
us closer to the mark "
  (let [[starboard-tack-boat port-tack-boat]
        (compute-tacks boat sailing-environment notes )]
    (find-better-tack
     boat sailing-environment notes 
     starboard-tack-boat port-tack-boat)))

(deftest test-lifted-tack
  (is (= 314
         (let [[s-boat s-notes s-se]
               (mk-s-set
                :destination {:x 100 :y 100}
                :direction 0
                :position {:x 150 :y 150})]

           (lifted-tack s-boat s-se  s-notes))))
  (is (= 46
         (let [[s-boat s-notes s-se]
               (mk-s-set :destination {:x 100 :y 100}
                         :position {:x 50 :y 150})]
           (lifted-tack s-boat s-se  s-notes))))


  (is (= 46
         (let [[s-boat s-notes s-se]
               (mk-s-set :destination {:x 300, :y 100}
                         :position {:x 50, :y 850}                 
                         :direction 107)]
           (lifted-tack s-boat s-se  s-notes)))))



(defn make-good-velocity [boat sailing-environment notes]
  "this function's name is a play on velocity-made-good "
  (let [pos     (boat :position)
        dest    (notes :destination)
        dir     (boat :direction)]
    (comment
      (println "---------make-velocity-good")
      (println "notes" notes)
      (println "pos" pos)
      (println "dir" dir))


    (let [mark-bearing (logot/bearing pos dest)
          mark-boat    (assoc boat :direction mark-bearing)
          can-we-sail  (can-sail
                        mark-boat
                        sailing-environment)
          can-point2 (fn [dir]
                       (wind/can-point dir
                                       (:wind-direction sailing-environment)
                                       (:pointing-angle boat)))
          ]
      (comment
        (println "se " sailing-environment)
        (println "mark-bearing" mark-bearing)
        (println "can-we-sail" can-we-sail)
        (println "---------"))
      (if can-we-sail
        (do 
          ;;(println "if we can go straight to the mark we have it easy")
          (if (and (can-point2 dir)
                   (> 2 (cmath/abs (angle-diff dir mark-bearing))))
            (do
  (println "if we are pointing at the mark, go towards it!")
              [0 notes])
            (do
              ;;(println "mark-bearing direction"  mark-bearing dir)
              ;;(println "let's start turning towards the mark")
              ;; hopefully the signs are correct
              [(updated-heading dir mark-bearing) notes])))
        (do
          (pcomment "aha life is interesting, the mark is upwind of us")
          (let [lifted-heading (lifted-tack boat sailing-environment notes)]
            (if (and (can-point2 dir)
                     (> 8  (cmath/abs (angle-diff
                                       dir lifted-heading))))
              (do
;;      (println "if we are on the lifted-tack now, let's go forward")
                [0 notes])
              (do
;;                (println "otherwise, let's start turning towards the mark")
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


(defn boat-turn [boat sailing-environment notes]
;;    (println "mboat sailing-environment notes" boat sailing-environment notes)
  (let [
        pos     (boat  :position)
        dest    (notes :destination)
        dir     (boat :direction)]
;;    (println       (notes :marks))
    ;;    (println "boat-turn sailing-environment" boat sailing-environment notes)
    (if (< destination-resolution
           (point-distance dest pos))
      ;; if we aren't at the mark

      (make-good-velocity  boat    sailing-environment notes )
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
    (is (= [1 notes] (boat-turn boat  {:wind-direction 180}   notes )))))

(defn update-marks-turn [boat sailing-environment notes ]
  [0 (update-marks notes)])

(defn port-tack [boat sailing-environment notes ]
  (let [mb (mk-managed-boat :boat boat)]
    (if (first (te/tack-port-tp mb sailing-environment notes ))
      (straight  boat sailing-environment notes )
      (te/tack-port  boat  sailing-environment notes))))

(defn starboard-tack [boat sailing-environment notes ]
  (let [mb (mk-managed-boat :boat boat)]
    (if (first (te/tack-starboard-tp mb sailing-environment notes ))
      (straight boat sailing-environment notes )
      (te/tack-starboard boat sailing-environment notes ))))

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
