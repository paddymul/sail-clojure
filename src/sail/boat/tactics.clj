(ns sail.boat.tactics
  (:use
   [clojure.test :only [is deftest]]
   [sail.boat.nodeps :only
    [mk-boat pcomment b-forward b-clockwise b-anti-clockwise]]
   [sail.boat.physics :only
    [boat-rotation wind-direction boat-movement pointing-angle can-point]]

   [clojure.contrib.math :only [abs]]
   [logo.math :only [angle-diff -c +c point-distance]]
   [logo.turtle-prim :only
    [mk-turtle move-point-dir]] 
   )
  
  (:require
   [clojure.contrib.math :as cmath]
   [logo.math :as lmath]
   [logo.turtle-prim :as logot]

   ))
(def destination-resolution 5)
(comment
  " We will know that we have rounded a mark when the bearing from the
  boat to the mark is 180 from the bearing from that mark to the
  previous mark

  Look at the following image.  M2 is the mark we care about M1 is the
  previous mark.  B is the boat.  B has now cleared the mark.  I'm not
  sure how to handle cases where B crosses 180 without touching it.

  


            B
             \\
               M2


       M3              M1

      Thinking about this problem prompted thought about defining a
      360 penalty.  the tracking for the 360 penalty must occur
      without rounding any marks

"

  )

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
                    (* (* boat-rotation (- tack-outlook tack-a-turn))
                       boat-movement))
        tack-b-pos (move-point-dir
                    pos tack-b
                    (* (* boat-rotation (- tack-outlook tack-b-turn))
                       boat-movement))


        tack-a-dist (point-distance tack-a-pos dest)
        tack-b-dist (point-distance tack-b-pos dest)
        tack-dist-diff  (clojure.contrib.math/abs (- tack-a-dist tack-b-dist))]
    (pcomment "tack-a-turn tack-a-dist" tack-a-turn tack-a-dist)
    (pcomment "tack-b-turn tack-b-dist tack-dist-diff"
              tack-b-turn tack-b-dist tack-dist-diff)

    (if (> 5 (clojure.contrib.math/abs (- tack-a-dist tack-b-dist)))
      (do
        (pcomment " chosing closer-tack" closer-tack)
        closer-tack)
      (do
        (pcomment "tack-a-dist tack-b-dist" tack-a-dist tack-b-dist)
        (if (>= tack-a-dist tack-b-dist)
          tack-a
          tack-b)))))

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


(defn boat-turn [boat]
  (let [pos     ((boat :turtle) :position)
        dest    (boat :destination)
        dir     ((boat :turtle) :direction)]
    (if (< destination-resolution
           (point-distance dest pos))
      ;; if we aren't at the mark
      (let [mark-bearing (logot/bearing pos dest)]
        (if (can-point mark-bearing)
          (do 
            (pcomment "if we can go straight to the mark we have it easy")
            (if (> 3 (clojure.contrib.math/abs (angle-diff dir mark-bearing)))
              (do
                (pcomment "if we are pointing at the mark, go towards it!")
                0)
              (do
                (println "mark-bearing direction"  mark-bearing dir)
                (pcomment "let's start turning towards the mark")
                ;; hopefully the signs are correct
                (updated-heading dir mark-bearing))))
          (do
            (pcomment "aha life is interesting, the mark is upwind of us")
            (let [lifted-heading (lifted-tack boat)]
              (if (= dir lifted-heading)
                (do
                  (pcomment "if we are on the lifted-tack now, let's go forward")
                  0)
                (do
                  (pcomment "otherwise, let's start turning towards the mark")
                  (updated-heading dir lifted-heading)))))))
      ;; we are at the mark
      ;; for now I will just point the boat at the wind
      ;;
      (if (= dir (-c 180 wind-direction))
        0 ;; if we are pointing at the wind stay there
        -1 ;; otherwise turn into the wind, to starboard)
        ))))


