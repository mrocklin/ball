(ns protosite.models
  (:require [protosite.lahman-datomic :refer :all]
            [clojure.data.json :as json])
  (:use [datomic.api :only [db q] :as d]))

(def batting-attrs ["G" "AB" "R" "H" "2B" "3B" "HR" "RBI" "BB"])

(defn keywordify [s] (->> s (str "lahman/") keyword))

(defn lvar [s] (symbol (str "?" s)))

(defn mapify [x] {"aaData" x})

(defn ready-for-data-tables [grid]
  (->> grid
    (map #(map str %))
    mapify
    json/write-str))

(defn get-name-map [conn]
  (let [results
    (q '[:find ?playerID ?first ?last :where [?x :lahman/playerID ?playerID]
                                             [?x :lahman/nameFirst ?first]
                                             [?x :lahman/nameLast  ?last]]
     (db conn))]
    (zipmap (map first results) (map rest results))))


(defn team-record [conn teamID year]
  (let [attrs batting-attrs
        x (lvar "x")
        valnames (map lvar attrs)
        keyword-attrs (map keywordify attrs)
        where-clauses (map #(vector x %1 %2) keyword-attrs valnames)]
    (->>
      (q {:find (concat '[?first ?last] valnames)
        :where (concat [[x :lahman/playerID '?playerID]
                         [x :lahman/teamID teamID]
                         [x :lahman/yearID year]
                         ['?y :lahman/playerID '?playerID]
                         ['?y :lahman/nameFirst '?first]
                         ['?y :lahman/nameLast '?last]]
                       where-clauses)}

         (db conn) )
      (sort-by second))))
