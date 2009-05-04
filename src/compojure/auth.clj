;; Copyright (c) James Reeves. All rights reserved.
;; The use and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which
;; can be found in the file epl-v10.html at the root of this distribution. By
;; using this software in any fashion, you are agreeing to be bound by the
;; terms of this license. You must not remove this notice, or any other, from
;; this software.

;; compojure.auth:
;;
;; Middleware and overridable multimethods to handle authentication.

(ns compojure.auth
  (:use compojure.html.form-helpers))

(defn basic-login-page
  "A route that shows a basic username/password login page on GET."
  [path options]
  (GET path
    ((:template options)
      (form-to {:class "login"} [:post path]
        [:div.username
          (label :username "Username")
          (text-field :username)]
        [:div.password
          (label :password "Password")
          (password-field :password)]))))

(defn basic-post-auth
  "A route that handles basic username/password authentication from a form
  POST action."
  [path options]
  (POST path
    (if-let [user ((
