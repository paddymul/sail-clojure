(ns sail.boat.nodeps
  (:use
   [clojure.test :only [is deftest]]
   [logo.turtle-prim :only
    [mk-turtle
     forward clockwise anti-clockwise]]
   [clojure.contrib.def :only [defnk]]
   ))

(defstruct boat
  :speed
  :rotation  
  :minimum-speed  

  :maximum-possible-speed
  :pointing-angle
  :turtle )

(defnk mk-boat [:speed 0
                
                :rotation  1
                :minimum-speed  0.1
                :maximum-possible-speed 1
                :pointing-angle 45
                :position {:x 75 :y 100}
                :direction 0]
  (struct boat
          speed
          ;; the folowing 4 parameters will be fixed for the life of the boat
          ;;to make life easier, we let the boat move a little while in irons
          rotation
          minimum-speed  
          maximum-possible-speed
          pointing-angle
          (mk-turtle :position position :direction direction)))

(deftest mk-boat-test

  (is (= {
          :speed 0
          :rotation  1
          :minimum-speed  0.1
          :maximum-possible-speed 1
          :pointing-angle 45
          :turtle (mk-turtle :position {:x 75 :y 100} :direction 0)
          }
         (mk-boat))))

(defstruct managed-boat :boat :notes)

(defnk mk-managed-boat
  [:speed 0
   :rotation  1
   :minimum-speed  0.1
   :maximum-possible-speed 1
   :pointing-angle 45
   :destination {:x 50 :y 0}
   :position {:x 75 :y 100}
   :direction 0]
  (struct managed-boat 
          (mk-boat
           :rotation                    rotation
           :minimum-speed               minimum-speed
           :speed                       speed                      
           :maximum-possible-speed      maximum-possible-speed 
           :pointing-angle              pointing-angle      
           :position position :direction direction)
          {:destination destination}))


(deftest managed-boat-test

  (is (=
       {:boat
        {:speed 0,
         :rotation  1
         :minimum-speed  0.1
         :maximum-possible-speed 1
         :pointing-angle 45
         :turtle
         {:position {:x 75, :y 100},
          :direction 0}},
        :notes
        {:destination {:x 50, :y 0}}}

       (mk-managed-boat)))

  (is (=
       {:boat
        {:speed 0
         :rotation  1
         :minimum-speed  0.1
         :maximum-possible-speed 1
         :pointing-angle 45
         :turtle
         {:position {:x 75, :y 100},
          :direction 0}},
        :notes
        {:destination {:x 500, :y 0}}
        }

       (mk-managed-boat :destination {:x 500, :y 0} )))


  )

(defn b-forward [boat distance]
  (assoc boat :turtle
         (forward (boat :turtle) distance)))

(defn b-clockwise [boat delta-angle]
  (assoc boat :turtle
         (clockwise (boat :turtle) delta-angle)))


(defn b-anti-clockwise [boat delta-angle]
  (assoc boat :turtle (anti-clockwise (boat :turtle) delta-angle)))

(comment
(deftest b-tests
  (is (= {:x 110 :y 100}
         (:position
          (:turtle
           (b-forward
            (mk-boat :direction 90
                     :position  {:x 100 :y 100})
            10))))))
)
(defn pcomment [& comments]
  (apply println comments)
  )

(comment
  (symbol-macro
   boat-destructure
   "consider a series of  symbol-macros that takes the place of"
   (let [
         pos     ((boat :turtle) :position)
         dest    (boat :destination)
         dir     ((boat :turtle) :direction)])))
