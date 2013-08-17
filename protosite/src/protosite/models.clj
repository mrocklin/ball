(ns protosite.models
  (:require [protosite.lahman-datomic :refer :all]
            [clojure.data.json :as json])
  (:use [datomic.api :only [db q] :as d]))

(def batting-attrs ["G" "AB" "R" "H" "2B" "3B" "HR" "RBI" "BB"])

(defn keywordify [s] (->> s (str "lahman/") keyword))

(defn lvar [s] (symbol (str "?" s)))

(def get-names (memoize (fn [conn]
    (q '{:find [?pID ?first ?last]
         :where [[?x :lahman/nameFirst ?first]
                 [?x :lahman/playerID  ?pID]
                 [?x :lahman/nameLast  ?last]]}
     (db conn)))))

(def get-name-map (memoize (fn [conn]
  (into {} (for [[pid f l] (get-names conn)] [pid [f l]])))))


(defn team-record [conn teamID year attrs]
  (let [x (lvar "x")
        y (lvar "y")
        valnames (map lvar attrs)
        keyword-attrs (map keywordify attrs)
        where-clauses (map #(vector x %1 %2) keyword-attrs valnames)
        result (sort-by first
      (q {:find  (concat ['?playerID '?first '?last] valnames)
        :where (concat [[x :lahman/teamID teamID]
                        [x :lahman/yearID year]
                        [x :lahman/playerID '?playerID]]
                       where-clauses
                       [[y :lahman/playerID '?playerID]
                        [y :lahman/nameFirst '?first]
                        [y :lahman/nameLast '?last]])} (db conn) ))]
      {:data (map rest result)
       :rows (map first result)
       :columns (concat ["first" "last"] attrs)}))

(defn response [result]
  (-> result
    (assoc :data (map #(map str %) (:data result)))))

(defn teamIDs [conn]
  (map first (q '[:find ?team :where [?x :lahman/teamID ?team]] (db conn))))

(defn team-names [conn]
  (into {} (q '[:find ?teamName ?teamID
                :where [?x :lahman/name ?teamName]
                       [?x :lahman/teamID ?teamID]] (db conn))))

(defn no-join-query [conn wanted constraints]
  " A simple select &wanted from all-tables where &constraints=values query
    (no-join-query conn ['name'] [['state' 'IL'] ['balance' 0]])
    => the names of all people in Illinois with zero balance"
  (let [x (lvar "x")
        wanted-clauses     (for [attr wanted]
                             [x (keywordify attr) (lvar attr)])
        constraint-clauses (for [[attr value] constraints]
                             [x (keywordify attr) value])
        result
      (q {:find (map lvar wanted)
          :where (concat constraint-clauses wanted-clauses)} (db conn))]
    {:data result
     :columns wanted}))


(defn basic-query [conn wanted constraints]
  " Like no-join-query but with only one wanted attribute "
  (let [result (no-join-query conn [wanted] constraints)]
    (assoc result :data (map first (:data result)))))
