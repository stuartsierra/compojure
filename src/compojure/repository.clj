;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.repository:
;;
;; Functions for implementing the Compojure repository pattern.

(ns compojure.repository)

(defn repository-type
  "Return the type of the repository."
  [repository]
  (if (keyword? repository)
    repository
    (:repository repository)))

(defn require-repository
  "Require the namespace of a respository."
  [repository]
  (require (symbol (namespace (repository-type repository)))))

(defn use-repository
  "Loads the namespace of the repository and returns the repository type.
  Usually used by multimethods."
  [repository]
  (require-repository repository)
  (repository-type repository))
