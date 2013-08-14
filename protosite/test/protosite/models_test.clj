(ns protosite.models-test
  (:require [midje.sweet :refer :all]
            [clojure.data.json :as json]
            [protosite.models :refer :all]
            [protosite.lahman-schema :refer [schema]]
            [protosite.datomic-test :refer [with-connection]]
            [protosite.datomic-lahman-test :refer :all]
            [midje.sweet :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(facts "lvar produces a logic variable"
      (lvar "x") => '?x
      (let [x (lvar "x")]
        (q {:find [x]
            :where [[x 2 3]]}
           [[1,2,3], [3,2,3]])) => #{[1] [3]}
       )

(fact "get-names "
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        (contains? (get-names conn) ["strawda01" "Darryl" "Strawberry"]) => true))

(fact "get-name-map returns map of playerID to [first, last]"
  (with-connection conn
    (d/transact conn schema)
    (d/transact conn master-facts)
    ((get-name-map conn) "strawda01") => ["Darryl" "Strawberry"]))


(fact ""
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        (d/transact conn batting-facts)
        (let [result (team-record conn "NYN" 1990 batting-attrs)]
          (:data result) => [["carrema01" "Mark" "Carreon" 82 188 30 47 12 0 10 26 15]
                             ["strawda01" "Darryl" "Strawberry" 152 542 92 150 18 1 37 108 70]]
          (:columns result) => (concat ["playerID" "first" "last"] batting-attrs)
          (:rows result) => ["carrema01" "strawda01"])
        (let [result (response (team-record conn "NYN" 1990 batting-attrs))]
          (every? #(instance? String %) (first (:data result))) => true
          (first (:columns result)) => "playerID")))




(fact "NYN is in teams"
       (with-connection conn
             (d/transact conn schema)
             (d/transact conn teams-facts)
             (teamIDs conn) => (contains "NYN")))

(fact "New York Mets is in team-names"
       (with-connection conn
             (d/transact conn schema)
             (d/transact conn teams-facts)
             ((team-names conn) "New York Mets") => "NYN"))

(facts "basic-query works"
  (with-connection conn
    (d/transact conn schema)
    (d/transact conn master-facts)
    (d/transact conn batting-facts)
    (d/transact conn teams-facts)
    (let [result (basic-query conn "G" [["teamID" "NYN"] ["yearID" 1990]])]
      (:data result)  => (contains [82 152] :in-any-order :gaps-ok)
      (:columns result) => ["G"])
    (let [result (basic-query conn "name" [["teamID" "NYN"] ["yearID" 1990]])]
      (:data result) => ["New York Mets"]
      (:columns result) => ["name"])))


(facts "no-join-query works"
  (with-connection conn
    (d/transact conn schema)
    (d/transact conn batting-facts)
    (no-join-query conn ["G" "G_batting"] [["teamID" "NYN"] ["yearID" 1990] ["playerID" "strawda01"]])
               => {:data #{[152 152]}
                   :columns ["G" "G_batting"]}))

(facts "no-join-response contains aaData and aoColumns with correct entries"
  (with-connection conn
    (d/transact conn schema)
    (d/transact conn batting-facts)
    (response (no-join-query conn ["G" "G_batting"] [["teamID" "NYN"] ["yearID" 1990] ["playerID" "strawda01"]]))
    => {:data [["152" "152"]]
        :columns ["G" "G_batting"]}))
