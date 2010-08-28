(ns sail.boat.boat-core
  (:use
   [clojure.test :only [is deftest]]
   [sail.boat.nodeps  :only [mk-managed-boat]]
   [sail.boat.physics :only [boat-physics]]
   [sail.boat.tactics :only [boat-turn port-tack]]
   )
  (:require
   [sail.course.core]
   [sail.boat.nodeps :as nodeps]
  [sail.boat.tactics :as tac]
  ))



(def sailing-environment {:wind-direction  180})

(defn update-managed-boat [managed-boat]
  (nodeps/update-managed-boat
   managed-boat
   sailing-environment boat-physics
   
   boat-turn
   ;;port-tack
   ;;tac/starboard-tack
   ))
(def boat-a (atom (nodeps/mk-managed-boat)))
(defn play []
  (doseq [a [0]];; 1 2 3 4 5 6 7 8 9 10]]
    (reset! boat-a (update-managed-boat @boat-a))))
(play)





(comment


  eventually this should include fleet management tools

  and expose course information

  )
