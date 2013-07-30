(ns protosite.datomic-lahman-test
  (:require [midje.sweet :refer :all]
            [protosite.datomic-test :refer :all]
            [protosite.lahman-schema :refer :all]
            [protosite.lahman-parse :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(defn add-id [m] 
  (assoc m :db/id (d/tempid :db.part/user)))

(def known-keys (apply hash-set (map #(->> % (str "lahman/") keyword) (keys types))))

(defn remove-unknown-keys [m]
    (apply dissoc m (remove #(contains? known-keys %) (keys m))))

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

(def master-facts (->> "resources/lahman2012/Master-test.csv"
                       facts-from-filename
                       (map remove-unknown-keys)
                       (map add-id)))

(fact "Master dataset parses and is queryable"
      (with-connection conn
        (d/transact conn schema)
        (d/transact conn master-facts)
        ; Dayrl Strawberry was born in Los Angeles
        (q '[:find ?city :where [?x :lahman/playerID "strawda01"]
                                [?x :lahman/birthCity ?city]] (db conn))
                       => #{["Los Angeles"]}
      ))
