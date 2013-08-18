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

;; Stuart Halloway
;; https://groups.google.com/forum/#!msg/datomic/ZethRt6dqxs/GceIbj53_P8J
(defn transact-pbatch
    "Submit txes in batches of size batch-size, default is 100"
    ([conn txes] (transact-pbatch conn txes 100))
    ([conn txes batch-size]
          (->> (partition-all batch-size txes)
                      (pmap #(d/transact-async conn (mapcat identity %)))
                      (map deref)
                      dorun)
          :ok))


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


(defn get-transactions []
  (for [f
    (->> filenames
      (map datomic-facts-from-filename)
      flatten)]
    [f]))


(defn populate [uri]
  (println "Trying to create DB...")
  (if-not (d/create-database uri)
    (println "Database already up")
    (do
      (println "Connecting...")
      (let [conn (d/connect uri)]
        (d/transact conn schema)
        (println "Added Schema; processing facts...")
        (transact-pbatch conn (get-transactions))
        (println "Processed facts")
        (println "Requesting index")
        (d/request-index conn)
        (println "Shutting down")
        (d/release conn)))))


(defn -main []
  (populate db-uri))
