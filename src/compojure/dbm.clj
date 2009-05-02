;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.dbm:
;;
;; Multimethods that define a common interface to a key/value based database,
;; such as JDBM or Tokyo Cabinet.

(ns compojure.dbm
  (:use compojure.control)
  (:use compojure.repository))

(defmulti db-open
  "Open a database repository identified by a map. Returns a modified map with
  the repository connection/handle attached."
  (fn [repo] (use-repository repo)))

(defmulti db-close
  "Close a database repository."
  (fn [repo] (use-repository repo)))

(defmulti db-fetch
  "Use a key to retrieve a value from the repository."
  (fn [repo key] (use-repository repo)))

(defmulti db-store
  "Store a key and value in the repository."
  (fn [repo key val] (use-repository repo)))

(defmulti db-delete
  "Delete a key and its associated value from the repository"
  (fn [repo key] (use-repository repo)))

(declare *dbm-repo*)

(defn fetch
  "Uses db-fetch to fetch a Clojure object from the *dbm-repo* repository."
  [key]
  (maybe read-string (db-fetch *dbm-repo* (str key))))

(defn store
  "Uses db-store to store a Clojure object in the *dbm-repo* repository."
  [key value]
  (db-store *dbm-repo* (str key) (pr-str value)))

(defn delete
  "Deletes a value from the *dbm-repo* repository."
  [key]
  (db-delete *dbm-repo* (str key)))

(defmacro with-db
  "Open a database repository, evaluate the body, then close the database."
  [repository & body]
  `(binding [~'*dbm-repo* (make-repository ~repository)]
    (try
      (set! ~'*dbm-repo* (db-open ~'*dbm-repo*))
      ~@body
      (finally (db-close ~'*dbm-repo*)))))
