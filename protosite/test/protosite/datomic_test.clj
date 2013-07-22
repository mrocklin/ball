(ns protosite.datomic-test
  (:require [midje.sweet :refer :all]
            [protosite.core :refer :all])
  (:use [datomic.api :only [db q] :as d]))


;; Adapted from
;; https://github.com/thearthur/datomic-from-clojure-example/blob/master/src/illu/core.clj:


(def test-uri "datomic:mem://test")


(defn make-db [] (d/create-database test-uri))


(defn delete-db [] (d/delete-database test-uri))


(defmacro with-connection [conn & body]
  `(do (make-db)
       (let [~conn (d/connect test-uri)]
         ~@body)
       (delete-db)))


(defn add-person-attribute [conn]
  (d/transact conn [{:db/id #db/id[:db.part/db]
                     :db/ident :person/name
                     :db/valueType :db.type/string
                     :db/cardinality :db.cardinality/one
                     :db/doc "A person's name"
                     :db.install/_attribute :db.part/db}]))

(defn add-a-person [conn name]
  (d/transact conn [{:db/id #db/id[:db.part/user] :person/name name}]))


(defn get-all-people [conn]
  (q '[:find ?n :where [?c person/name ?n ]] (db conn)))


(facts", simple ones, about Datomic"
       (with-connection c
         (add-person-attribute c)
         (add-a-person c "john")
         (fact "John is sollipsistic"
               (let [[john & others] (into [] (get-all-people c))]
                 john => ["john"]
                 others => nil))))
