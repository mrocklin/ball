(ns protosite.datomic-lahman-test
  (:require [midje.sweet :refer :all]
            [protosite.datomic-test :refer :all]
            [protosite.lahman-datomic :refer :all]
            [protosite.lahman-schema :refer :all]
            [protosite.lahman-parse :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(fact "Database handles basic queries"
       (with-connection conn
          (d/transact conn schema)
          (d/transact conn [
              (-> {"AB" "5" "2B" "2" "playerID" "joe"} map-to-fact add-id)
              (-> {"AB" "7" "2B" "3" "playerID" "sally"} map-to-fact add-id)])
          (q '[:find ?AB :where [?ent :lahman/playerID "joe"]
                                [?ent :lahman/AB ?AB]] (db conn))
               => #{[5]}
          (q '[:find ?player :where [?ent :lahman/playerID ?player]] (db conn))
               => #{["joe"] ["sally"]}))

(def master-facts
  (datomic-facts-from-filename "resources/lahman2012/Master-test.csv"))

(def batting-facts
  (datomic-facts-from-filename "resources/lahman2012/Batting-test.csv"))

(def franchise-facts
  (datomic-facts-from-filename "resources/lahman2012/TeamsFranchises-test.csv"))

(def teams-facts
  (datomic-facts-from-filename "resources/lahman2012/Teams-test.csv"))

(fact "Lahman dataset parses and is queryable"
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        (d/transact conn batting-facts)
        (d/transact conn franchise-facts)
        (d/transact conn teams-facts)
        ; Dayrrl Strawberry was born in Los Angeles
        (q '[:find ?city :where [?x :lahman/playerID "strawda01"]
                                [?x :lahman/birthCity ?city]] (db conn))
                       => #{["Los Angeles"]}
        ; With what teams did Dayrrl play?
        (q '[:find ?team :where [?x :lahman/playerID "strawda01"]
                                [?x :lahman/teamID   ?team]] (db conn))
                       => #{["NYN"] ["LAN"] ["SFN"] ["NYA"]}
        ; With what teams did people who were born in Los Angeles play?
        (q '[:find ?team :where [?x :lahman/playerID ?player]
                                [?y :lahman/playerID ?player]
                                [?x :lahman/birthCity "Los Angeles"]
                                [?y :lahman/teamID   ?team]] (db conn))
                       => #{["NYN"] ["LAN"] ["SFN"] ["NYA"]}
        ; Name of the NYM is New York Mets
        (q '[:find ?name :where [?x :lahman/franchName ?name]
                                [?x :lahman/franchID "NYM"]] (db conn))
                       => #{["New York Mets"]}
        ; FranchID of NYN is NYM
        (q '[:find ?franchID :where [?x :lahman/teamID "NYN"]
                                    [?x :lahman/franchID ?franchID]] (db conn))
                       => #{["NYM"]}
        ; Name of NYN is New York Mets
        (q '[:find ?name :where [?x :lahman/teamID "NYN"]
                                    [?x :lahman/name ?name]] (db conn))
                       => #{["New York Mets"]}
        ; Daryll played for the New York Mets
        (q '[:find ?name :where [?x :lahman/playerID "strawda01"]
                                [?x :lahman/teamID ?team]
                                [?y :lahman/teamID ?team]
                                [?y :lahman/name ?name]] (db conn))
                       =>  #{["New York Mets"]}))
