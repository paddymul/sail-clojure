(ns sail.boat.boat-core
  (:use
   [sail.boat.nodeps  :only [mk-managed-boat]]
   [sail.boat.physics :only [boat-physics]]
   [sail.boat.tactics :only [boat-turn]]
   ))


(defn update-boat [boat]
  (let [boat-pair (boat-turn boat)
        rudder-angle (first boat-pair)
        boat-notes   (first (rest boat-pair))]
    (boat-physics boat rudder-angle)))

(defn update-managed-boat [managed-boat]
  (let [orig-boat  (:boat managed-boat)
        orig-notes (:notes managed-boat)
        boat-pair (boat-turn orig-boat  (:notes managed-boat))
        rudder-angle (first boat-pair)
        up-notes   (first (rest boat-pair))]
    (println orig-boat rudder-angle)
    (merge managed-boat {:boat (boat-physics orig-boat rudder-angle)
                         :notes up-notes})))



(comment
  eventually this should include fleet management tools

  and expose course information

  )