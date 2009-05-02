(ns test.compojure.dbm.memory
  (:use compojure.dbm)
  (:use compojure.dbm.memory)
  (:use clojure.contrib.test-is))

(def memory-db
  {:repository :compojure.dbm.memory/hash-map
   :name       "test-db"})

(deftest test-with-db-empty
  (is (with-db memory-db)))

(deftest test-bad-fetch
  (with-db memory-db
    (is (nil? (fetch :k0)))))

(deftest test-store-fetch
  (with-db memory-db
    (store :k1 "v1")
    (is (= (fetch :k1) "v1"))))

(deftest test-fetch-after-close
  (with-db memory-db
    (store :k2 "v2"))
  (with-db memory-db
    (is (= (fetch :k2) "v2"))))

(deftest test-delete
  (with-db memory-db
    (store :k3 "v3")
    (delete :k3)
    (is (nil? (fetch :k3)))))
