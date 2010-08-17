(ns sail.boat.nodeps
  (:use
   [clojure.test :only [is deftest]]
   [logo.turtle-prim :only
    [mk-turtle
     forward clockwise anti-clockwise]]
   [clojure.contrib.def :only [defnk]]
   ))

(defstruct boat :destination :turtle)

(defnk mk-boat [:destination {:x 50 :y 0}
                :position {:x 75 :y 100}
                :direction 0]
  (struct boat
          destination
          (mk-turtle :position position :direction direction)))



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

(defn pcomment [& comments]
  ;;(apply println comments)
  )

(comment
  (symbol-macro
   boat-destructure
   "consider a series of  symbol-macros that takes the place of"
   (let [
         pos     ((boat :turtle) :position)
         dest    (boat :destination)
         dir     ((boat :turtle) :direction)])))
