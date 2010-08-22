(ns sail.boat.nodeps
  (:use
   [clojure.test :only [is deftest]]
   [clojure.contrib.def :only [defnk]]

   
   )
  (:require
   [logo.turtle-prim :as logo-p])
  )

(defstruct boat
  :position
  :direction
  :speed
  :rudder-angle
  ;; the folowing 4 parameters will be fixed for the life of the boat
  ;;to make life easier, we let the boat move a little while in irons
  
  :rotation  
  :minimum-speed  
  :maximum-possible-speed
  :pointing-angle)
  

(defnk mk-boat [:position {:x 75 :y 100}
                :direction 0
                :speed 0
                :rudder-angle 0
                :rotation  1
                :minimum-speed  0.1
                :maximum-possible-speed 1
                :pointing-angle 45
                ]
  (struct   boat
            position   direction   speed rudder-angle
          rotation
          minimum-speed  
          maximum-possible-speed
          pointing-angle
          
          ))

(deftest mk-boat-test

  (is (= {
          :speed 0
          :rudder-angle 0
          :rotation  1
          :minimum-speed  0.1
          :maximum-possible-speed 1
          :pointing-angle 45
          :position {:x 75 :y 100} :direction 0
          }
         (mk-boat))))

(defstruct managed-boat :boat :notes)

(defnk mk-managed-boat
  [:speed 0
   :rudder-angle 0
   :rotation  1
   :minimum-speed  0.1
   :maximum-possible-speed 1
   :pointing-angle 45
   :destination {:x 50 :y 0}
   :position {:x 75 :y 100}
   :direction 0]
  (struct managed-boat 
          (mk-boat
           :rudder-angle                 rudder-angle 
           :rotation                     rotation
           :minimum-speed                minimum-speed
           :speed                        speed                      
           :maximum-possible-speed       maximum-possible-speed 
           :pointing-angle               pointing-angle      
           :position position :direction direction)
          {:destination destination}))


(deftest managed-boat-test

  (is (=
       {:boat
        {:speed 0
         :rudder-angle 0
         :rotation  1
         :minimum-speed  0.1
         :maximum-possible-speed 1
         :pointing-angle 45
         :position {:x 75, :y 100},
         :direction 0},
        :notes
        {:destination {:x 50, :y 0}}}

       (mk-managed-boat)))

  (is (=
       {:boat
        {:speed 0
         :rudder-angle 0
         :rotation  1
         :minimum-speed  0.1
         :maximum-possible-speed 1
         :pointing-angle 45
         :position {:x 75, :y 100},
          :direction 0},
        :notes
        {:destination {:x 500, :y 0}}
        }

       (mk-managed-boat :destination {:x 500, :y 0} )))


  )


(defn update-managed-boat [managed-boat
                           sailing-environment  boat-physics-fn boat-turn-fn]
  " this function keeps boat-turn from cheating,  boat cheating gets all the relevent information about its' boat, but it only returns rudder-angle, we merge rudder-angle outside of boat turn, thus boat turn can't put a boat at the finish just because it wants to"
  (let [orig-boat  (:boat managed-boat)
        [rudder-angle up-notes]
            (boat-turn-fn
               orig-boat sailing-environment  (:notes managed-boat))
        bp-boat    (assoc orig-boat :rudder-angle rudder-angle)]
;;    (println orig-boat rudder-angle)
    (merge managed-boat
           {:boat (boat-physics-fn bp-boat sailing-environment)
            :notes up-notes})))

(defn mk-turtle [boat]
  (logo-p/mk-turtle :position  (:position boat)
                    :direction (:direction boat)))

(defn b-forward [boat distance]
  (assoc boat :position
         (:position (logo-p/forward
                     (mk-turtle boat) distance))))

(defn b-clockwise [boat delta-angle]
  (assoc boat :direction
         (:direction (logo-p/clockwise
                     (mk-turtle boat) delta-angle))))


(defn b-anti-clockwise [boat delta-angle]
  (assoc boat :direction
         (:position (logo-p/anti-clockwise
                     (mk-turtle boat) delta-angle))))


(deftest b-tests
  (is (= {:x 110 :y 100}
         (:position
           (b-forward
            (mk-boat :direction 90
                     :position  {:x 100 :y 100})
            10)))))


(defn pcomment [& comments]
;;  (apply println comments)
  )

(comment
  (symbol-macro
   boat-destructure
   "consider a series of  symbol-macros that takes the place of"
   (let [
         pos     ((boat :turtle) :position)
         dest    (boat :destination)
         dir     ((boat :turtle) :direction)])))
