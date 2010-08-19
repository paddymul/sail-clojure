(ns sail.boat.nodeps
  (:use
   [clojure.test :only [is deftest]]
   [logo.turtle-prim :only
    [mk-turtle
     forward clockwise anti-clockwise]]
   [clojure.contrib.def :only [defnk]]
   ))

(defstruct boat :speed :turtle)

(defnk mk-boat [:speed 0
                :position {:x 75 :y 100}
                :direction 0]
  (struct boat
          speed
          (mk-turtle :position position :direction direction)))

(defstruct managed-boat :boat :notes)

(defnk mk-managed-boat
  [:destination {:x 50 :y 0}
   :position {:x 75 :y 100}
   :direction 0]
  (struct managed-boat 
          (mk-boat :position position :direction direction)
           {:destination destination}))

(deftest managed-boat-test

  (is (=
       {:boat
        {:speed 0,
         :turtle
         {:position {:x 75, :y 100},
          :direction 0}},
        :notes
        {:destination {:x 50, :y 0}}}

       (mk-managed-boat)))

  (is (=
       {:boat
        {:speed 0,
         :turtle
         {:position {:x 75, :y 100},
          :direction 0}},
        :notes
        {:destination {:x 500, :y 0}}
        }

       (mk-managed-boat :destination {:x 500, :y 0} )))


  )

(defn b-forward [boat distance]
  (assoc boat :turtle
         (forward (boat :turtle) distance)))

(defn b-clockwise [boat delta-angle]
  (assoc boat :turtle
         (clockwise (boat :turtle) delta-angle)))


(defn b-anti-clockwise [boat delta-angle]
  (assoc boat :turtle (anti-clockwise (boat :turtle) delta-angle)))


(deftest b-tests
  (is (= {:x 110 :y 100}
         (:position
          (:turtle
           (b-forward
            (mk-boat :direction 90
                     :position  {:x 100 :y 100})
            10))))))

(defn pcomment [& comments]
  (apply println comments)
  )

(comment
  (symbol-macro
   boat-destructure
   "consider a series of  symbol-macros that takes the place of"
   (let [
         pos     ((boat :turtle) :position)
         dest    (boat :destination)
         dir     ((boat :turtle) :direction)])))
