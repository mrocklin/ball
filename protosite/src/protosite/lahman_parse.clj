(ns protosite.lahman-parse
  (:require [clojure.data.csv :as csv])
  (:require [clojure.java.io :as io])
  (:require [protosite.lahman-schema :refer [types]]))

(defn maps-from-text [text]
  (let [[header & data] text]
    (for [line data] (zipmap header line))))

(defn parse-int [expr] (Integer/parseInt (str expr)))
(defn parse-float [expr] (Double/parseDouble (str expr)))

(defn date-of [year month day]
  (->> [year month day]
      (map parse-int)
      (apply format "#inst \"%d-%02d-%02d\"")
      read-string))

(defn date-of-str [s]
  (let [[month day year] (clojure.string/split s #"/")]
                        (date-of year month day)))

(defn lahman-keywordify [k]
  (keyword (str "lahman/" k)))


(defn replace-ymd-with-date [name m]
  (let [[year month day] (map (partial str name) ["Year" "Month" "Day"])]
    (if (every? identity (map (partial get m) [year month day]))
      (-> m
         (assoc name (apply date-of (map m [year month day])))
         (dissoc year month day))
      m)))

(defn replace-string-with-date [name m]
  (if (empty? (m name))
    m
    (assoc m name (date-of-str (m name)))))

(defn replace-ints [m]
  (let [f (fn [k v]
            (if (and (= (get types k) :db.type/long)
                     (not (empty? v)))
              (parse-int v) v))]
  (into {} (for [[k v] m] [k (f k v)]))))

(defn replace-floats [m]
  (let [f (fn [k v]
            (if (and (= (get types k) :db.type/float)
                     (not (empty? v)))
              (parse-float v) v))]
  (into {} (for [[k v] m] [k (f k v)]))))

(defn keywordify-map [m]
  (zipmap (->> m keys (map lahman-keywordify)) (vals m)))

(defn remove-empty-items [m]
  (let [good-keys (remove (comp empty? m) (keys m))
        good-vals (map m good-keys)]
    (zipmap good-keys good-vals)))


(defn map-to-fact [m]
  (->> m
    remove-empty-items
    (replace-string-with-date "debut")
    (replace-string-with-date "finalGame")
    (replace-ymd-with-date "birth")
    (replace-ymd-with-date "death")
    replace-ints
    replace-floats
    keywordify-map))

(defn facts-from-filename [f]
  (->> f
    io/reader
    csv/read-csv
    maps-from-text
    (map map-to-fact)))
