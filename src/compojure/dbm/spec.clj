;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.dbm.spec:
;;
;; Multimethods that define a common interface to a key/value based database,
;; such as DBM, Tokyo Cabinet, or Amazon's SimpleDB.

(ns compojure.dbm.spec)

(declare *dbm-repo*)

(defmulti open-db
  "Open a database repository identified by a map."
  (fn [] (:type *dbm-repo*)))

(defmulti close-db
  "Close a database repository."
  (fn [] (:type *dbm-repo*)))

(defmacro with-db
  "Open a database repository, evaluate the body, then close the database."
  [repository & body]
  `(binding [~'*dbm-repo* ~repository]
    (try
      (set! ~'*dbm-repo*
        (assoc ~'*dbm-repo* :object (open-db)))
      ~@body
      (finally (close-db)))))

(defmulti fetch
  "Use a key to retrieve a value from the repository."
  (fn [key] (:type *dbm-repo*)))

(defmulti store
  "Store a key and value in the repository."
  (fn [key value] (:type *dbm-repo*)))

(defmulti delete
  "Delete a key and its associated value from the repository"
  (fn [key] (:type *dbm-repo*)))
