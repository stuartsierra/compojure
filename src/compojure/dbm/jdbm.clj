;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.dbm.jdbm:
;;
;; Implementation of the compojure.dbm interface to JDBM.

(ns compojure.dbm.jdbm
  (:use compojure.dbm)
  (:use clojure.contrib.java-utils)
  (:import jdbm.RecordManager)
  (:import jdbm.RecordManagerFactory)
  (:import jdbm.htree.HTree)
  (:import jdbm.btree.BTree))

(def db-classes
  {:htree HTree, :btree BTree})

(defn- invoke-static
  "Dynamically invoke a static method on a class."
  [class method types args]
  (.invoke
    (.getMethod class (as-str method) (into-array Class types))
    class
    (into-array Object args)))

(defn- load-db
  "Load an existing database."
  [manager class id]
  (invoke-static class :load [RecordManager Long/TYPE] [manager id]))

(defn- create-db
  "Create a new database."
  [manager class name]
  (let [store (invoke-static class :createInstance [RecordManager] [manager])]
    (.setNamedObject manager name (.getRecid store))
    store))

(defn- get-db
  "Load or create the named database."
  [manager class name]
  (let [id (.getNamedObject manager name)]
    (if (not= id 0)
      (load-db manager class id)
      (create-db manager class name))))

(defmethod db-open :jdbm
  [repository]
  (let [filename (:filename repository)
        manager  (RecordManagerFactory/createRecordManager filename)
        db-class (db-classes (:storage-type repository))
        db       (get-db manager db-class (:name repository))]
    (merge repository
      {:object  db
       :manager manager})))

(defmethod db-close :jdbm
  [repository]
  (.close (:manager repository)))

(defmethod db-fetch :jdbm
  [repository key]
  (.get (:object repository) key))

(defmethod db-store :jdbm
  [repository key value]
  (.put (:object repository) key value))

(defmethod db-delete :jdbm
  [repository key]
  (.remove (:object repository) key))
