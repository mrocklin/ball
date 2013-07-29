(ns protosite.datomic-lahman-test
  (:require [midje.sweet :refer :all]
            [protosite.datomic-test :refer :all]
            [protosite.lahman-schema :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(fact "Schema is loaded and consistent"
       (with-connection conn
          (d/transact conn schema)
          (q '[:find ?typ :where [?entity :db/ident :lahman.weight ]
                                 [?entity :db/valueType ?typ       ]] (db conn))
               => :db.type/int))



