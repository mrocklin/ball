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

