(ns protosite.lahman-load
  (:require [protosite.lahman-datomic :refer [datomic-facts-from-filename
                                              db-uri]]
            [protosite.lahman-schema :refer [schema]])
  (:use [datomic.api :only [db q] :as d])
  (:gen-class))


(def filenames ["resources/lahman2012/Master.csv"
                "resources/lahman2012/Batting.csv"
                "resources/lahman2012/Teams.csv"
                "resources/lahman2012/TeamsFranchises.csv"])

(defn get-facts [] (->> filenames
                        (map datomic-facts-from-filename)
                        flatten))


(defn count-with-action
  "
  Apply f to s side-effect-ly while counting elements. Keep from
  blowing Java heap when processing / counting large numbers of
  elements.
  "
  ([f s]
     (count-with-action f s 0))
  ([f s n]
     (if-not (seq s)
       n
       (do
         (f (first s))
         (recur f (rest s) (inc' n))))))

(defn populate [uri]
  (println "Trying to create DB...")
  (if-not (d/create-database uri)
    (println "Database already up")
    (do
      (println "Connecting...")
      (let [conn (d/connect uri)]
        (d/transact conn schema)
        (println "Added Schema; processing facts...")
        (let [facts (get-facts)
              n (count-with-action #(d/transact conn [%]) facts)]
          (println (format "Processed %d facts" n)))
        (println "Requesting index")
        (d/request-index conn)
        (println "Shutting down")
        (d/release conn)))))


(defn -main []
  (populate db-uri))
