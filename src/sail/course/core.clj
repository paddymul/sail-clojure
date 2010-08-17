(ns sail.course.core
  (:use
   [logo.turtle-prim :only [mk-turtle]]
   ))

(defn make-mark [x y]
  (atom (mk-turtle
         :position {:x x :y y}
         :direction 0)))
(def three-leg-course
     [(make-mark 300  100)  (make-mark 100  300)  (make-mark 400  300)])  
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
