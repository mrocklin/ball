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

(facts "get-names "
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        (contains? (get-names conn) ["strawda01" "Darryl" "Strawberry"]) => true))


(facts ""
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        (d/transact conn batting-facts)
        (team-record conn "NYN" 1990) =>
             [["Mark" "Carreon" 82 188 30 47 12 0 10 26 15]
              ["Darryl" "Strawberry" 152 542 92 150 18 1 37 108 70]])
      )

(facts "ready-for-data-tables makes ideal output"
       (ready-for-data-tables [[1 2] [3 4]]) =>
       (json/write-str {"aaData" [["1" "2"] ["3" "4"]]}))
