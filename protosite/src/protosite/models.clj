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

(def get-names (memoize (fn [conn]
    (q '{:find [?pID ?first ?last]
         :where [[?x :lahman/playerID ?pID]
                [?x :lahman/nameFirst ?first]
                [?x :lahman/nameLast  ?last]]}
     (db conn)))))

(def get-name-map (memoize (fn [conn]
  (into {} (for [[pid f l] (get-names conn)] [pid [f l]])))))


(defn team-record [conn teamID year]
  (let [attrs batting-attrs
        x (lvar "x")
        y (lvar "y")
        valnames (map lvar attrs)
        keyword-attrs (map keywordify attrs)
        where-clauses (map #(vector x %1 %2) keyword-attrs valnames)]
    (->>
      (q {:find  (concat ['?first '?last] valnames)
        :where (concat [[x :lahman/teamID teamID]
                        [x :lahman/yearID year]
                        [x :lahman/playerID '?playerID]]
                       where-clauses
                       [[y :lahman/playerID '?playerID]
                        [y :lahman/nameFirst '?first]
                        [y :lahman/nameLast '?last]])}

         (db conn) )
      (sort-by second))))

(defn teamIDs [conn]
  (map first (q '[:find ?team :where [?x :lahman/teamID ?team]] (db conn))))

(defn team-names [conn]
  (into {} (q '[:find ?teamName ?teamID
            :where [?x :lahman/name ?teamName]
                   [?x :lahman/teamID ?teamID]] (db conn))))
