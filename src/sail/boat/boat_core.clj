(ns sail.boat.boat-core
  (:use
   [sail.boat.physics :only [boat-physics]]
   [sail.boat.tactics :only [boat-turn]]
   ))


(defn update-boat [boat]
  (boat-physics boat (boat-turn boat)))

(comment
  eventually this should include fleet management tools

  and expose course information

  )