(ns sail.boat.boat-core
  (:use
   [clojure.test :only [is deftest]]
   [sail.boat.nodeps  :only [mk-managed-boat]]
   [sail.boat.physics :only [boat-physics]]
   [sail.boat.tactics :only [boat-turn]]
   )
  (:require
   [sail.course.core]
   [sail.boat.nodeps :as nodeps])
  
  )



(def sailing-environment {:wind-direction  180})

(defn update-managed-boat [managed-boat]
  (nodeps/update-managed-boat
   managed-boat
   sailing-environment boat-physics boat-turn))






(comment


  eventually this should include fleet management tools

  and expose course information

  )
