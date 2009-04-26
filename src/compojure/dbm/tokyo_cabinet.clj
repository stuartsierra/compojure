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
  (:use compojure.dbm.spec)
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

(defmethod open-db :tokyo-cabinet []
  (let [db-class (db-classes (:storage-type *dbm-repo*))
        db       (.newInstance db-class)
        filename (:filename *dbm-repo*)
        success? (.open db filename write+create)]
    (if success?
      db
      (throwf (str "Could not open file: " (error-message db))))))

(defmethod close-db :tokyo-cabinet []
  (let [db (:object *dbm-repo*)]
    (when-not (.close db)
      (throwf (str "Could not close file: " (error-message db))))))

(defmethod fetch :tokyo-cabinet
  [key]
  (.get (:object *dbm-repo*) key))

(defmethod store :tokyo-cabinet
  [key value]
  (.put (:object *dbm-repo*) key value))

(defmethod delete :tokyo-cabinet
  [key]
  (.out (:object *dbm-repo*) key))
