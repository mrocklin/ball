(ns protosite.models
  (:require [protosite.lahman-datomic :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(def batting-attrs ["playerID" "yearID" "G" "AB" "R" "H" "2B" "3B" "HR" "RBI" "BB"])

(defn keywordify [s] (->> s (str "lahman/") keyword)) 
(defn symbolify  [s] (->> s (str "?") symbol)) 

(defn lvar [s] (symbol (str "?" s)))


(defn team-record [conn teamID years]
  (let [attrs batting-attrs
        x (lvar "x")
        valnames (map lvar attrs)
        keyword-attrs (map keywordify attrs)
        where-clauses (map #(vector x %1 %2) keyword-attrs valnames)]
    (q {:find valnames
        :in '[$ [?year ...]]
        :where (concat [[x :lahman/teamID teamID]
                        [x :lahman/yearID '?year]]
                       where-clauses)}
                  
         (db conn) years)))
