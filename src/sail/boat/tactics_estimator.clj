(ns sail.boat.tactics-estimator
  (:use
   [clojure.contrib.trace]
   [clojure.test :only [is deftest]]
   [clojure.contrib.def :only [defnk ]]
   )
  (:require
   [sail.boat.wind :as wind]
   [sail.boat.physics :as physics]
   [sail.boat.nodeps :as nodeps]
   ))


(comment
  hopefully with tactics estimator I can build a cute little dsl
  that looks somehting like this
  ((tack-port) 50 )  meaning go on a port tack and then 50 steps afterwards
  the great thing about this is, I can use this to estimate other boats progress too
  this will be helpfull with search based approaches to the problem

  I am enjoying the constraints of functional programing here

  maybe for right now this is a bit advanced something like tactics
  estimator should be built before I have a more advanced physics
  model in place

  right now physics are so dirt simple that I can model them quickly
  inside of lifted-tack that won't always be the case

  )


(defn tactics-estimator [managed-boat sailing-environment
                         boat-thinking-fn
                         termination-predicate termination-predicate-notes]
  (let [new-boat (nodeps/update-managed-boat
                  managed-boat sailing-environment
                  physics/boat-physics boat-thinking-fn)
        [should-terminate new-tp-notes]
        (termination-predicate new-boat sailing-environment
                               termination-predicate-notes)]
    ;;  (println "should-terminate new-tp-notes" should-terminate new-tp-notes)
    (if should-terminate
      [new-boat new-tp-notes]
      (recur
       new-boat sailing-environment boat-thinking-fn
       termination-predicate new-tp-notes))))


(defn straight [boat sailing-environment notes]
  [0 notes])

(defn always-go-p [managed-boat sailing-environment tp-notes]
  [false tp-notes])


(defn make-count-predicate [count]
  (fn [managed-boat sailing-environment tp-notes]
    [(< count  (:count tp-notes))
     (assoc tp-notes :count (+ 1 (:count tp-notes)))]))

(defn or-predicates [pred1 pred2]
  (fn [managed-boat sailing-environment tp-notes]
    (let [[pred1-p pred1-n]
           (pred1 managed-boat sailing-environment tp-notes)
          [pred2-p pred2-n]
           (pred2 managed-boat sailing-environment tp-notes)]
      [(or pred1-p pred2-p)
       (merge pred1-n pred2-n)])))
        

(deftest tactics-estimator-test
  (is (= [(nodeps/mk-managed-boat :direction 46 :rudder-angle 1) {:count 46}]
           (tactics-estimator
            (nodeps/mk-managed-boat)
            {:wind-direction 180}
            tack-port
            tack-port-tp {:count 46}))))


(defn sail-instructions
  [boat sailing-environment global-predicate & pred-pairs]
  (let [estim (fn [boat-notes instr-pair]
                (let [[instr instr-p] instr-pair
                      [boat pre-notes] boat-notes]
                  (tactics-estimator
                   boat sailing-environment instr
                   (or-predicates instr-p global-predicate)
                   pre-notes)))
        boat-notes-pair (atom [boat {:count 0}])]
    (doseq [pred-pair pred-pairs]
      (reset! boat-notes-pair (estim @boat-notes-pair pred-pair)))
  @boat-notes-pair))


(defn tack-port [boat sailing-environment notes]
  [1 notes])


(defn tack-port-tp [managed-boat sailing-environment tp-notes]
  [(and
        (wind/can-sail (:boat managed-boat) sailing-environment)
        (wind/boat-on-port-heading (:boat managed-boat) sailing-environment))
   tp-notes
   ])

(defn tack-starboard [boat sailing-environment notes]
  [-1 notes])


(defn tack-starboard-tp [managed-boat sailing-environment tp-notes]
  [(and
        (wind/can-sail (:boat managed-boat) sailing-environment)
        (not (wind/boat-on-port-heading (:boat managed-boat) sailing-environment)))
   tp-notes
   ])


(def port-tack-instructions [tack-port tack-port-tp])
(def starboard-tack-instructions [tack-starboard tack-starboard-tp])
(def straight-instructions [straight always-go-p])

    
      
         
(sail-instructions (nodeps/mk-managed-boat)
                    {:wind-direction 180}
                    (make-count-predicate 200)
                    port-tack-instructions straight-instructions)

(sail-instructions (nodeps/mk-managed-boat)
                    {:wind-direction 180}
                    (make-count-predicate 200)
                    port-tack-instructions
                    [straight (make-count-predicate 0)]
                    ;;straight-instructions
                    starboard-tack-instructions
                    straight-instructions
                    )

