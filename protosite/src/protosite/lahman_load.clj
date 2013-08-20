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

(defn lazy-mapcat
  "
  Fully lazy version of mapcat.  See:
  http://clojurian.blogspot.com/2012/11/beware-of-mapcat.html
  "
  [f coll]
  (lazy-seq
   (if (not-empty coll)
     (concat
      (f (first coll))
      (lazy-mapcat f (rest coll))))))


(defn lazy-flatten [coll] (lazy-mapcat identity coll))


;; Stuart Halloway
;; https://groups.google.com/forum/#!msg/datomic/ZethRt6dqxs/GceIbj53_P8J
(defn transact-pbatch
    "Submit txes in batches of size batch-size, default is 100"
    ([conn txes] (transact-pbatch conn txes 100))
    ([conn txes batch-size]
          (->> (partition-all batch-size txes)
               (pmap #(d/transact-async conn (lazy-flatten %)))
               (map deref)
               dorun)
          :ok))


(defn get-transactions []
  (map vector
       (->> filenames
             (map datomic-facts-from-filename)
             lazy-flatten)))


(defn populate [uri]
  (println "Trying to create DB...")
  (if-not (d/create-database uri)
    (println "Database already up")
    (do
      (println "Connecting...")
      (let [conn (d/connect uri)]
        (d/transact conn schema)
        (println "Added Schema; processing facts...")

        (doseq [filen filenames]
          (transact-pbatch conn (map vector (datomic-facts-from-filename filen))))

        (println "Processed facts")
        (println "Requesting index")
        (d/request-index conn)
        (println "Shutting down")
        (d/release conn)))))


(defn -main []
  (populate db-uri))
