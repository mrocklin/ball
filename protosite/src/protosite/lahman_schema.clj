(ns protosite.lahman-schema
  (:require [datomic.api :only [db] :as d]))

(defn lahman-keywordify [k] (keyword (str "lahman." k)))

(def idents (let [m
                  {"lahmanID" :db.type/int
                   "playerID" :db.type/string
                   "managerID" :db.type/string
                   "hofID" :db.type/string
                   "birth" :db.type/instant
                   "birthCountry" :db.type/string
                   "birthState" :db.type/string
                   "birthCity" :db.type/string
                   "death" :db.type/instant
                   "deathCountry" :db.type/string
                   "deathState" :db.type/string
                   "deathCity" :db.type/string
                   "nameFirst" :db.type/string
                   "nameLast" :db.type/string
                   "nameNote" :db.type/string
                   "nameGiven" :db.type/string
                   "nameNick" :db.type/string
                   "weight" :db.type/int
                   "height" :db.type/int
                   "bats" :db.type/string
                   "throws" :db.type/string
                   "debut" :db.type/instant
                   "finalGame" :db.type/instant
                   "college" :db.type/string
                   "lahman40ID" :db.type/string
                   "lahman45ID" :db.type/string
                   "retroID" :db.type/string
                   "holtzID" :db.type/string
                   "bbrefID" :db.type/string
                   "yearID" :db.type/int
                   "stint" :db.type/int
                   "teamID" :db.type/string
                   "lgID" :db.type/string
                   "G" :db.type/int
                   "G_batting" :db.type/int
                   "AB" :db.type/int
                   "R" :db.type/int
                   "H" :db.type/int
                   "2B" :db.type/int
                   "3B" :db.type/int
                   "HR" :db.type/int
                   "RBI" :db.type/int
                   "SB" :db.type/int
                   "CS" :db.type/int
                   "BB" :db.type/int
                   "SO" :db.type/int
                   "IBB" :db.type/int
                   "HBP" :db.type/int
                   "SH" :db.type/int
                   "SF" :db.type/int
                   "GIDP" :db.type/int
                   "G_old" :db.type/int
                   }]
            (zipmap (map lahman-keywordify (keys m)) (vals m))))

    ;:db/unique :db.unique/identity
    ;:db/index true
    ;:db/doc "The Hash of a commit - Unique 40 character string"

(def unique-keys #{:lahman.lahmanID :lahman.playerID})

(def schema (for [[ident typ] idents]
                   {:db/id #db/id[:db.part/db]
                    :db/ident ident
                    :db/valueType typ
                    :db/cardinality :db.cardinality/one
                    :db/unique (contains? unique-keys ident)
                    :db.install/_attribute :db.part/db}))
