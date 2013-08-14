(ns protosite.core-test
  (:use [midje.sweet])
  (:require [compojure.route :as route]
            [compojure.core :refer :all]
            [clojure.data.json :as json]
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
       (let [r (request "/html/example-static-file.html" app)]
         (:status r) => 200
         (slurp (:body r)) => (contains "Hi")))


(time (facts "Team route includes Dayrrl's at-bats for 1990"
       (let [r (request "/team/NYN/1990/" app)]
         (:status r) => 200
         (:body r) => (contains "152"))))

(facts "basic query gets a functioning route"
       (let [r (request "/query/" app
                    {"want" "AB"
                     "constraints" [["yearID" 1990] ["playerID" "strawda01"]]})]
         (:status r) => 200
         (json/read-str (:body r)) => {"data" [542] "columns" ["AB"]})
       (let [r (request "/query/" app
                    {"want" "AB"
                     "constraints" [["playerID" "strawda01"]]})]
         (:status r) => 200
         (:body r) => (contains "542"))
       (let [r (request "/query/" app
                    {"want" "yearID"
                     "constaints" [["name" "New York Mets"]]})]
         (:status r) => 200
         (:body r) => (contains "1990")
         (:body r) => (contains "1991")))

(fact "Player Query for Daryll includes his at-bats for 1990"
       (let [r (request "/player/strawda01/" app)
             body (-> r :body json/read-str)]
         (:status r) => 200
         (body "data") => truthy
         (body "columns") => truthy
         (str (body "data")) => (contains "152"))) ;; TODO: Better test

(fact "player-name yields strawda01's player name"
       (let [r (request "/player-name/strawda01/" app)]
         (:status r) => 200
         (json/read-str (:body r)) =>{"first" "Darryl" "last" "Strawberry"}))

