(ns test.compojure.http.session
  (:use compojure.http.session)
  (:use compojure.dbm)
  (:use clojure.contrib.test-is))

;; Session multimethods

(defmethod db-open ::mock-dbm [rep] ::mock-dbm)
(defmethod db-close ::mock-dbm [rep])
(defmethod db-fetch ::mock-dbm [rep key] ":mock-value")
(defmethod db-store ::mock-dbm [rep key val])
(defmethod db-delete ::mock-dbm [rep key])

(deftest create-session-has-id
  (contains? (create-session ::mock-dbm) :id))

(deftest test-session-cookie
  (let [session (create-session ::mock-dbm)]
    (is (= (session-cookie ::mock-dbm true session)
           (session :id)))
    (is (nil? (session-cookie ::mock-dbm false session)))))

(deftest test-read-session
  (is (= (read-session ::mock-dbm ::mock-id) :mock-value)))

(deftest session-hmac-secret-key
  (let [rep {:repository :compojure.session/cookie
             :secret-key "test"}]
    (session-hmac rep "foobar")
    "ithiOBI7sp/MpMb9EXgxvm1gmufcQvFT+gRzIUiSd7A="))

;; Session routes

(deftest session-nil-response
  (let [handler  (with-session (constantly nil))
        response (handler {})]
    (is (nil? response))))

(defmethod create-session ::mock [rep]
  {:id ::mock-id})

(defmethod write-session ::mock [rep session])

(defmethod read-session ::mock [rep id]
  (is (= id ::mock-id))
  {:id ::mock-id})

(defmethod session-cookie ::mock [rep new? session]
  "mock-session-data")

(defn- mock-session-response [response]
  (let [handler (-> (constantly response) (with-session ::mock))]
    (handler {})))

(deftest new-session-cookie
  (let [response (mock-session-response {})]
    (is (= (get-in response [:headers "Set-Cookie"])
           "compojure-session=mock-session-data; path=/"))))

(deftest response-session-cookie
  (let [response (mock-session-response {:session {}})]
    (is (= (get-in response [:headers "Set-Cookie"])
           "compojure-session=mock-session-data; path=/"))))

(declare mock-store)

(derive ::mock-update ::mock)

(defmethod write-session ::mock-update [rep session]
  (set! mock-store session))

(defmethod read-session ::mock-update [rep id]
  mock-store)

(defn- mock-session-update [request response]
  (let [handler (-> (constantly response) (with-session ::mock-update))]
    (handler request)))

(deftest session-write-new
  (binding [mock-store nil]
    (mock-session-update {} {:session {:foo "bar"}})
    (is (= mock-store {:foo "bar"}))))

(deftest session-write-update
  (binding [mock-store {:foo "bar"}]
    (mock-session-update {} {:session {:foo "baz"}})
    (is (= mock-store {:foo "baz"}))))

(deftest session-write-no-update
  (binding [mock-store {:foo "bar"}]
    (mock-session-update {:cookies {:compojure-session "mock-id"}} {})
    (is (= mock-store {:foo "bar"}))))
