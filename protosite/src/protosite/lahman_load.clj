(ns protosite.lahman-load
  (:require [protosite.lahman-datomic :refer :all]
            [protosite.lahman-schema :refer :all]
            [protosite.lahman-parse :refer :all])
  (:use [datomic.api :only [db q] :as d])
  (:gen-class))

(def filenames ["resources/lahman2012/Master.csv"
                "resources/lahman2012/Batting.csv"])
(def facts (->> filenames
                (map datomic-facts-from-filename)
                flatten))

(def db-uri "datomic:free://localhost:4334/lahman")

(defn populate [uri]
  (if (d/create-database uri)
    (let [conn (d/connect uri)]
           (d/transact conn schema)
           (doseq [fact facts]
             (d/transact conn [fact]))
           (d/request-index conn))
    (println "Database already up")))

(defn -main []
  (populate db-uri))
