(ns protosite.lahman-load
  (:require [protosite.lahman-datomic :refer :all]
            [protosite.lahman-schema :refer :all]
            [protosite.lahman-parse :refer :all])
  (:use [datomic.api :only [db q] :as d])
  (:gen-class))

(def filenames ["resources/lahman2012/Master.csv"
                "resources/lahman2012/Batting.csv"
                "resources/lahman2012/TeamsFranchises.csv"])
(def facts (->> filenames
                (map datomic-facts-from-filename)
                flatten))

(defn populate [uri]
  (if (d/create-database uri)
    (let [conn (d/connect uri)]
           (d/transact conn schema)
           (println "Added Schema")
           (doseq [fact facts]
             (d/transact conn [fact]))
           (println (format "Added %d facts" (count facts)))
           (d/request-index conn)
           (println "Requesting index"))
    (println "Database already up")))

(defn -main []
  (populate db-uri)
  (println "left populate")
  (shutdown-agents))
