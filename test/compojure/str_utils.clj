(ns test.compojure.str-utils
  (:use compojure.str-utils)
  (:use clojure.test))

(deftest test-escape
  (is (= (escape "aeiou" "hello world")
         "h\\ell\\o w\\orld")))
