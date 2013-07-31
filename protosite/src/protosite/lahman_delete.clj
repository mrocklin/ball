(ns protosite.lahman-delete
  (:use [datomic.api :only [db q] :as d])
  (:gen-class))

(def db-uri "datomic:free://localhost:4334/lahman")

(defn -main []
  (d/delete-database db-uri))
