(ns sail.boat.boat-core
  (:use
   [sail.boat.nodeps  :only [mk-managed-boat]]
   [sail.boat.physics :only [boat-physics]]
   [sail.boat.tactics :only [boat-turn]]
   )
  (:require
   [sail.boat.nodeps :as nodeps])
  )



(def sailing-environment {:wind-angle  180}
(defn update-managed-boat [managed-boat]
  (nodeps/update-managed-boat
   sailing-environment boat-physics boat-turn))


(comment
  eventually this should include fleet management tools

  and expose course information

  )