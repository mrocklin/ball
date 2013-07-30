(ns protosite.lahman-test
  (:require [midje.sweet :refer :all]
            [protosite.lahman-parse :refer :all])
  (:use [datomic.api :only [db q] :as d]))

(fact "parse-int parses ints robustly"
      (parse-int 5) => 5
      (parse-int "05") => 5
      (parse-int "5") => 5
      (parse-int 5.252) => (throws Exception))

(fact "date-of produces times for a variety of inputs"
      (type (date-of 1939 8 5)) => java.util.Date
      (type (date-of "1939" "08" "05")) => java.util.Date
      (type (date-of "1939" "8" "5")) => java.util.Date)

(fact "date-of-str produces times"
      (type (date-of-str "09/30/2001")) => java.util.Date)

(fact "replace-ymd-with-date works"
      (replace-ymd-with-date "birth" {"birthYear" 2000 "birthMonth" 01 "birthDay" 05 :key :value}) 
      => {"birth" (date-of 2000 1 5) :key :value})

(fact "replace-string-with-date works"
      (replace-string-with-date "birth" {"birth" "01/05/2000" :key :value}) 
      => {"birth" (date-of 2000 1 5) :key :value})

(fact "map-to-fact works"
      (map-to-fact {"playerID" "joe" "bats" "L" "AB" "100"})
      => {:lahman/playerID "joe" :lahman/bats "L" :lahman/AB 100})

(fact "replace-ints works"
      (replace-ints {"ABCD" "B" "AB" "100"}) => {"ABCD" "B" "AB" 100})

(fact "keywordify-map works"
      (keywordify-map {"A" "B"}) => {:lahman/A "B"})

(fact "remove-empty-items works"
       (remove-empty-items {1 "" 2 "two"}) => {2 "two"})

(def master-facts (facts-from-filename "resources/lahman2012/Master.csv"))

(facts "deathday/month/year and birth/day/month/year do not occur in master facts"
      (not-any? #(contains? % "birthDay") master-facts) => true
      (not-any? #(contains? % "deathDay") master-facts) => true)

