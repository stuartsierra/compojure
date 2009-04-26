;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.dbm.tokyo-cabinet:
;;
;; Implementation of the DBM interface to Tokyo Cabinet.

(ns compojure.dbm.tokyo-cabinet
  (:use compojure.dbm)
  (:use clojure.contrib.except)
  (:import (tokyocabinet HDB FDB BDB)))

(def db-classes
  {:bdb BDB, :fdb FDB, :hdb HDB})

(def write+create
  (bit-or HDB/OWRITER HDB/OCREAT))  ; OWRITER and OCREAT same for all classes

(defn- error-message
  "Get the error message from the database object."
  [db]
  (.errmsg db (.ecode db)))

(defmethod db-open :tokyo-cabinet
  [repository]
  (let [db-class (db-classes (:storage-type repository))
        db       (.newInstance db-class)
        filename (:filename repository)
        success? (.open db filename write+create)]
    (if success?
      (assoc repository :object db)
      (throwf (str "Could not open file: " (error-message db))))))

(defmethod db-close :tokyo-cabinet
  [repository]
  (let [db (:object repository)]
    (when-not (.close db)
      (throwf (str "Could not close file: " (error-message db))))))

(defmethod db-fetch :tokyo-cabinet
  [repository key]
  (.get (:object repository) key))

(defmethod db-store :tokyo-cabinet
  [repository key value]
  (.put (:object repository) key value))

(defmethod db-delete :tokyo-cabinet
  [repository key]
  (.out (:object repository) key))
