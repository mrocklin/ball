(ns protosite.lahman-parse
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io]))

(def text (-> "resources/lahman2012/Master.csv"
              io/reader
              csv/read-csv))

(def maps (let [[header & data] text]
            (for [line data] (zipmap header line))))

(defn parse-int [expr] (Integer/parseInt (str expr)))

(defn date-of [year month day]
  (->> [year month day]
      (map parse-int)
      (apply format "#inst \"%d-%02d-%02d\"")
      read-string))

(defn date-of-str [s]
  (let [[month day year] (clojure.string/split s #"/")]
                        (date-of year month day)))

(defn lahman-keywordify [k]
  (keyword (str "lahman." k)))

(def int-fields #{"lahmanID" "height" "weight"})
(def del-fields #{"birthYear" "birthMonth" "birthDay" "deathYear" "deathMonth" "deathDay"})
(defn map-to-fact [m]
  (let [birth (let [[y m d] (map m ["birthYear" "birthMonth" "birthDay"])]
                (if (some empty? [y m d]) nil (date-of y m d)))
        death (let [[y m d] (map m ["deathYear" "deathMonth" "deathDay"])]
                (if (some empty? [y m d]) nil (date-of y m d)))
        debut (let [d (m "debut")] (if (empty? d) nil (date-of-str (m "debut"))))
        final (let [d (m "finalGame")] (if (empty? d) nil (date-of-str (m "finalGame"))))
        with-new (assoc m "birth" birth "death" death "debut" debut "finalGame" final)
        without-old (apply dissoc with-new del-fields)]
    (apply hash-map (flatten (for [[k v] without-old]
              [(lahman-keywordify k) (if (and (contains? int-fields k) (not (empty? v))) (parse-int v) v)])))))

(def player-facts (map map-to-fact maps))

; (eq (count player-facts) (count maps))
