(ns protosite.models-test
  (:require [midje.sweet :refer :all]
            [protosite.models :refer :all]
            [protosite.lahman-schema :refer [schema]]
            [protosite.datomic-test :refer [with-connection]]
            [protosite.datomic-lahman-test :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(facts "lvar produces a logic variable"
      (lvar "x") => '?x
      (let [x (lvar "x")]
        (q {:find [x]
            :where [[x 2 3]]}
           [[1,2,3], [3,2,3]])) => #{[1] [3]}
       )


(facts ""
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        (d/transact conn batting-facts)
        (team-record conn "NYN" [1989 1990]) => 
          #{["strawda01" 1990 152 542 92 150 18 1 37 108 70]
            ["strawda01" 1989 134 476 69 107 26 1 29 77 61]
            ["carrema01" 1990 82 188 30 47 12 0 10 26 15]
            ["carrema01" 1989 68 133 20 41 6 0 6 16 12]}
      ))
