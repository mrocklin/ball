(ns protosite.lahman-delete
  (:require [protosite.lahman-datomic :refer [db-uri]])
  (:use [datomic.api :only [db q] :as d])
  (:gen-class))

(defn -main []
  (d/delete-database db-uri))
