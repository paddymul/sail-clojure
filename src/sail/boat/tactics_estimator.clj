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
    (println "should-terminate new-tp-notes" should-terminate new-tp-notes)
    (if should-terminate
      [new-boat new-tp-notes]
      (recur
       new-boat sailing-environment boat-thinking-fn
       termination-predicate new-tp-notes))))




(defn tack-port [boat sailing-environment notes]
  [1 notes])

(defn tack-port-tp [managed-boat sailing-environment tp-notes]
  (println "managed-boat sailing-environment tp-notes" managed-boat sailing-environment tp-notes)
  [(or (and
    (wind/can-sail (:boat managed-boat) sailing-environment)
    (wind/boat-on-port-heading (:boat managed-boat) sailing-environment))
    (< 200  (:count tp-notes)))
    
   (assoc tp-notes :count (+ 1 (:count tp-notes)))])


(comment asdf
  (is (= {}
         (nodeps/mk-managed-boat))))

(deftest tactics-estimator-test

  (is (= {}
         (tactics-estimator
          (nodeps/mk-managed-boat)
          {:wind-direction 180}
          tack-port
          tack-port-tp {:count 0}))))
(comment
(nodeps/update-managed-boat
                  managed-boat sailing-environment
                  physics/boat-physics boat-thinking-fn)
)
                                         [1 notes])))))
;;                                         tack-port)