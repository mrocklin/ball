(ns protosite.lahman-datomic
  (:require [protosite.lahman-schema :refer :all]
            [protosite.lahman-parse :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(defn add-id [m] 
  (assoc m :db/id (d/tempid :db.part/user)))

(def known-keys (apply hash-set (map #(->> % (str "lahman/") keyword) (keys types))))

(defn remove-unknown-keys [m]
    (apply dissoc m (remove #(contains? known-keys %) (keys m))))

(defn datomic-facts-from-filename [f]
  (->> f facts-from-filename (map remove-unknown-keys) (map add-id)))
