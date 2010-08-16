(ns sail.boat
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
   [logo.core]
   [logo.math]
   
   ))

(def wind-direction 180)
;; boat stuff
(def pointing-angle 45)

(defn can-point [dir]
  (let [angle-to-wind
        (clojure.contrib.math/abs
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

(defstruct boat :destination :turtle)

(defnk mk-boat [:destination {:x 50 :y 0}
                :position {:x 75 :y 100}
                :direction 0]
  (struct boat
          destination
          (mk-turtle :position position :direction direction)))


(println (mk-boat))
(println (mk-boat :destination {:x 0 :y 0}))



(defn b-forward [boat distance]
  (let [n-turtle (forward (boat :turtle) distance)
        position (:position n-turtle)
        direction (:direction n-turtle)
        n-boat     (mk-boat
                    :destination (boat :destination)
                    :position (:position n-turtle)
                    :direction (:direction n-turtle))
        ]
    n-boat))

(defn b-clockwise [boat delta-angle]
  (let [n-turtle (clockwise (boat :turtle) delta-angle)
        position (:position n-turtle)
        direction (:direction n-turtle)
        n-boat     (mk-boat
                    :destination (boat :destination)
                    :position (:position n-turtle)
                    :direction (:direction n-turtle))
        ]
    n-boat))


(defn b-anti-clockwise [boat delta-angle]
  (let [n-turtle (anti-clockwise (boat :turtle) delta-angle)
        position (:position n-turtle)
        direction (:direction n-turtle)
        n-boat     (mk-boat
                    :destination (boat :destination)
                    :position (:position n-turtle)
                    :direction (:direction n-turtle))
        ]
    n-boat))


(deftest b-tests
  (is (= {:x 110 :y 100}
         (:position
          (:turtle
           (b-forward
            (mk-boat :direction 90
                     :position  {:x 100 :y 100})
            10))))))

(def destination-resolution 5)
(def boat-movement 5)
(def boat-rotation 1)

(comment
  (symbol-macro
   boat-destructure
   "consider a series of  symbol-macros that takes the place of"
   (let [
         pos     ((boat :turtle) :position)
         dest    (boat :destination)
         dir     ((boat :turtle) :direction)])))

(defn updated-heading [current-heading mark-bearing]
  "returns the angle that should be turned to point at mark "
  (let [turn-needed (angle-diff current-heading mark-bearing)]
    (if (> 0 turn-needed)
      (- 0 boat-rotation)
      boat-rotation)))

(deftest test-updated-heading
  (is (= 1
         (updated-heading  315 45))))

(defn lifted-tack [boat]
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
  (let [pos     ((boat :turtle) :position)
        dest    (boat :destination)
        dir     ((boat :turtle) :direction)
        tack-a  (-c (-c 180 wind-direction) pointing-angle)
        tack-b  (+c (-c 180 wind-direction) pointing-angle)
        tack-a-pos (move-point-dir pos tack-a (* 10 boat-movement))
        tack-b-pos (move-point-dir pos tack-b (* 10 boat-movement))
        tack-a-dist (point-distance tack-a-pos dest)
        tack-b-dist (point-distance tack-b-pos dest)]
    (if (> tack-a-dist tack-b-dist)
      tack-b
      tack-a))  )

(def tack-outlook 500)
(defn lifted-tack [boat]
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
  (let [pos     ((boat :turtle) :position)
        dest    (boat :destination)
        dir     ((boat :turtle) :direction)
        tack-a  (-c (-c 180 wind-direction) pointing-angle)
        tack-b  (+c (-c 180 wind-direction) pointing-angle)
        tack-a-turn (clojure.contrib.math/abs (angle-diff  dir tack-a))
        tack-b-turn (clojure.contrib.math/abs (angle-diff  dir tack-b))
        closer-tack (if (< tack-a-turn tack-b-turn)
                       tack-a tack-b)
        mark-distance (point-distance pos dest)
        tack-outlook  (+ mark-distance 20)
        tack-a-pos (move-point-dir
                    pos tack-a
                    (* (- tack-outlook tack-a-turn) boat-movement))
        tack-b-pos (move-point-dir
                    pos tack-b
                    (* (- tack-outlook tack-b-turn) boat-movement))

        tack-a-dist (point-distance tack-a-pos dest)
        tack-b-dist (point-distance tack-b-pos dest)
        tack-dist-diff  (clojure.contrib.math/abs (- tack-a-dist tack-b-dist))]
    (println "tack-a-turn tack-a-dist" tack-a-turn tack-a-dist)
    (println "tack-b-turn tack-b-dist tack-dist-diff"
             tack-b-turn tack-b-dist tack-dist-diff)

    (if (> 5 (clojure.contrib.math/abs (- tack-a-dist tack-b-dist)))
      (do
        (println " chosing closer-tack" closer-tack)
        closer-tack)
      (do
        (println "tack-a-dist tack-b-dist" tack-a-dist tack-b-dist)
        (if (<= tack-a-dist tack-b-dist)
          tack-b
          tack-a)))))

(deftest test-lifted-tack
  (is (= 45
         (lifted-tack
          (mk-boat :destination {:x 100 :y 100}
                   :position {:x 50 :y 50}))))
  (is (= 315
         (lifted-tack
          (mk-boat :destination {:x 100 :y 100}
                   :position {:x 150 :y 50}))))
  )

(defn pcomment [comments]
(println comments)
  
  )

(defn boat-turn [boat]
  (let [pos     ((boat :turtle) :position)
        dest    (boat :destination)
        dir     ((boat :turtle) :direction)]
    (if (< destination-resolution
           (point-distance dest pos))
      ;; if we aren't at the mark
      (let [mark-bearing (bearing pos dest)]
        (if (can-point mark-bearing)
          (do 
            (pcomment "if we can go straight to the mark we have it easy")
            (if (> 3 (clojure.contrib.math/abs (angle-diff dir mark-bearing)))
              (do
                (pcomment "if we are pointing at the mark, go towards it!")
                (b-forward boat boat-movement))
              (do
                (println "mark-bearing direction"  mark-bearing dir)
                (pcomment "let's start turning towards the mark")
                (b-clockwise boat (updated-heading dir mark-bearing)))))
          (do
            (pcomment "aha life is interesting, the mark is upwind of us")
            (let [lifted-heading (lifted-tack boat)]
              (if (= dir lifted-heading)
                (do
                  (pcomment "if we are on the lifted-tack now, let's go forward")
                  (b-forward boat boat-movement))
                (do
                  (pcomment "otherwise, let's start turning towards the mark")
                  (b-clockwise boat (updated-heading dir lifted-heading))))))))
      boat
      )))

