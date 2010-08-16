(ns sail.boat.utility
  (:use
   ;;[rosado.processing]
   [logo.macrology]
   [logo.turtle]
   [clojure.test]
   [logo.turtle-prim :only
    [mk-turtle
     move-point-dir bearing
     forward clockwise anti-clockwise]]
   [clojure.contrib.def]
   [logo.core]
   [logo.math]
   [sail.boat.tactics :only [boat-turn]]
   [sail.boat.physics :only [boat-physics]]
   ))

(defstruct boat :destination :turtle)

(defnk mk-boat [:destination {:x 50 :y 0}
                :position {:x 75 :y 100}
                :direction 0]
  (struct boat
          destination
          (mk-turtle :position position :direction direction)))


(println (mk-boat))
(println (mk-boat :destination {:x 0 :y 0}))


(defn b-forward [boat distance]
  (let [n-turtle (forward (boat :turtle) distance)
        position (:position n-turtle)
        direction (:direction n-turtle)
        n-boat     (mk-boat
                    :destination (boat :destination)
                    :position (:position n-turtle)
                    :direction (:direction n-turtle))
        ]
    n-boat))

(defn b-clockwise [boat delta-angle]
  (let [n-turtle (clockwise (boat :turtle) delta-angle)
        position (:position n-turtle)
        direction (:direction n-turtle)
        n-boat     (mk-boat
                    :destination (boat :destination)
                    :position (:position n-turtle)
                    :direction (:direction n-turtle))
        ]
    n-boat))


(defn b-anti-clockwise [boat delta-angle]
  (let [n-turtle (anti-clockwise (boat :turtle) delta-angle)
        position (:position n-turtle)
        direction (:direction n-turtle)
        n-boat     (mk-boat
                    :destination (boat :destination)
                    :position (:position n-turtle)
                    :direction (:direction n-turtle))
        ]
    n-boat))


(deftest b-tests
  (is (= {:x 110 :y 100}
         (:position
          (:turtle
           (b-forward
            (mk-boat :direction 90
                     :position  {:x 100 :y 100})
            10))))))


(defn update-boat [boat]
  (boat-physics boat (boat-turn boat)))
