(ns protosite.core-test
  (:use [midje.sweet])
  (:require [compojure.route :as route]
            [compojure.core :refer :all]
            [protosite.core :refer :all]))


(defn request
  "
  http://nakkaya.com/2010/01/22/unit-testing-compojure-routes/
  "
  [resource web-app & params]
  (web-app {:request-method :get
            :uri resource
            :params (first params)
            :headers {}}))


(facts "About home page"
       (let [r (request "/" app)]
         (:status r) => 200
         (:body r) => (contains "Welcome")))


(facts "About unknown pages"
       (let [r (request "/homerun" app)]
         (:status r) => 404
         (:body r) => (contains "not found")))


(facts "About static HTML"
       (let [r (request "/example-static-file.html" app)]
         (:status r) => 200
         (slurp (:body r)) => (contains "Hi")))
