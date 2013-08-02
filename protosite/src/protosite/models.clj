(ns protosite.models
  (:require [protosite.lahman-datomic :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(def batting-attrs ["playerID" "yearID" "G" "AB" "R" "H" "2B" "3B" "HR" "RBI" "BB"])

(defn keywordify [s] (->> s (str "lahman/") keyword)) 

(defn lvar [s] (symbol (str "?" s)))

(def conn (d/connect db-uri))

(defn team-record [conn teamID year]
  (let [attrs batting-attrs
        x (lvar "x")
        valnames (map lvar attrs)
        keyword-attrs (map keywordify attrs)
        where-clauses (map #(vector x %1 %2) keyword-attrs valnames)]
    (q {:find valnames
        :where (concat [[x :lahman/teamID teamID]
                        [x :lahman/yearID year]]
                       where-clauses)}
                  
         (db conn) )
      
      ))

(def c-team-record (partial team-record conn))
