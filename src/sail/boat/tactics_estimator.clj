(ns sail.boat.tactics-estimator
  (:use
   [clojure.contrib.trace]
   [clojure.test :only [is deftest]]
   [clojure.contrib.def :only [defnk ]]
   [sail.boat.physics :only [boat-physics]]
   [sail.boat.nodeps :only [mk-boat]]
   ))


(defn tactics-estimator-internal  [boat sailing-environment termination-predicate iteration-count ]
  (let [[rudder-angle should-terminate ]
        (apply termination-predicate [boat iteration-count])
        boat2 (boat-physics boat sailing-environment rudder-angle)]

    (if should-terminate
      boat
      (recur boat2 sailing-environment termination-predicate (+ iteration-count 1)))))

(defnk tactics-estimator [boat
                          sailing-environment
                          termination-predicate
                          :iteration-count 0]
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
  (tactics-estimator-internal
   boat
   sailing-environment
   termination-predicate
   iteration-count)
  )

(deftest estimator-test

  (tactics-estimator (mk-boat)
                     {:wind-direction 180}
                     (fn [boat itercount] [0 (> 3 itercount)]))
  (tactics-estimator (mk-boat :direction 50)
                     {:wind-direction 180}
                     (fn [boat itercount]
                       (println boat)
                       [0 (< 3 itercount)]))
  )

(defn tactics-estimator-internal  [boat sailing-environment termination-predicate iteration-count ]
  (let [[rudder-angle should-terminate ]
        (apply termination-predicate [boat iteration-count])
        boat2 (boat-physics boat sailing-environment rudder-angle)]

    (if should-terminate
      boat
      (recur boat2 sailing-environment termination-predicate (+ iteration-count 1)))))

(defnk tactics-estimator [boat
                          sailing-environment
                          termination-predicate
                          :iteration-count 0]
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
  (tactics-estimator-internal
   boat
   sailing-environment
   termination-predicate
   iteration-count)
  )

(deftest estimator-test

  (tactics-estimator (mk-boat)
                     {:wind-direction 180}
                     (fn [boat itercount] [0 (> 3 itercount)]))
  (tactics-estimator (mk-boat :direction 50)
                     {:wind-direction 180}
                     (fn [boat itercount]
                       (println boat)
                       [0 (< 3 itercount)]))
  )