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
                   ; Batting
                   "playerID" :db.type/string
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
                   ;Team
                   "yearID" :db.type/int
                   "lgID" :db.type/string
                   "teamID" :db.type/string
                   "franchID" :db.type/string
                   "divID" :db.type/string
                   "Rank" :db.type/int
                   "G" :db.type/int
                   "Ghome" :db.type/int
                   "W" :db.type/int
                   "L" :db.type/int
                   "DivWin" :db.type/string
                   "WCWin" :db.type/string
                   "LgWin" :db.type/string
                   "WSWin" :db.type/string
                   "R" :db.type/int
                   "AB" :db.type/int
                   "H" :db.type/int
                   "2B" :db.type/int
                   "3B" :db.type/int
                   "HR" :db.type/int
                   "BB" :db.type/int
                   "SO" :db.type/int
                   "SB" :db.type/int
                   "CS" :db.type/int
                   "HBP" :db.type/int
                   "SF" :db.type/int
                   "RA" :db.type/int
                   "ER" :db.type/int
                   "ERA" :db.type/float
                   "CG" :db.type/int
                   "SHO" :db.type/int
                   "SV" :db.type/int
                   "IPouts" :db.type/int
                   "HA" :db.type/int
                   "HRA" :db.type/
                   "BBA" :db.type/int
                   "SOA" :db.type/int
                   "E" :db.type/int
                   "DP" :db.type/int
                   "FP" :db.type/float
                   "name" :db.type/string
                   "park" :db.type/string
                   "attendance" :db.type/int
                   "BPF" :db.type/int
                   "PPF" :db.type/int
                   "teamIDBR" :db.type/string
                   "teamIDlahman45" :db.type/string
                   "teamIDretro" :db.type/string
                   }]
            (zipmap (map lahman-keywordify (keys m)) (vals m))))

    ;:db/unique :db.unique/identity
    ;:db/index true
    ;:db/doc "The Hash of a commit - Unique 40 character string"

(def index-keys #{:lahman.lahmanID :lahman.playerID})
(def unique-keys #{})

(def schema (for [[ident typ] idents]
                   {:db/id #db/id[:db.part/db]
                    :db/ident ident
                    :db/valueType typ
                    :db/cardinality :db.cardinality/one
                    :db/unique (contains? unique-keys ident)
                    :db/index (contains? index-keys ident)
                    :db.install/_attribute :db.part/db}))
