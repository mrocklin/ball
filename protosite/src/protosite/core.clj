(ns protosite.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer :all]
            [clojure.data.json :as json]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [protosite.models :refer :all]
            [protosite.lahman-datomic :refer [db-uri]])
  (:use [datomic.api :only [db q] :as d]))


(defroutes app
  (GET "/" [] "<h1>Welcome to Fantasy Baseball!</h1>")
  (GET "/teamids/" [] (json/write-str (teamIDs (d/connect db-uri))))
  (GET "/teamnames/" [] (json/write-str (team-names (d/connect db-uri))))
  (GET "/query/" request (let [want (get-in request [:params "want"])
                               constraints (get-in request [:params "constraints"])]
          (json/write-str (basic-query (d/connect db-uri) want constraints))))
  (GET "/querys/" request (let [want (get-in request [:params "want"])
                               constraints (get-in request [:params "constraints"])]
          (json/write-str (response (no-join-query (d/connect db-uri) want constraints)))))
  (GET ["/player-name/:pid/" :pid #"\w*"] [pid]
       (->> (no-join-query (d/connect db-uri)
                          ["nameFirst" "nameLast"] [["playerID" pid]])
         :data
         first
         (zipmap ["first" "last"])
         json/write-str))
  (GET ["/player/:pid/" :pid #"\w*"] [pid]
       (json/write-str (response (no-join-query (d/connect db-uri)
            (concat ["yearID" "teamID"] batting-attrs) [["playerID" pid]]))))

  (GET ["/team/:team/:year/" :team #"\w{3}" :year #"\d{4}"] [team year]
       (json/write-str (response (team-record (d/connect db-uri)
                            team (Integer/parseInt year) batting-attrs))))

  (GET ["/player-history/:pid/:attr/" :pid #"\w*" :attr #"\w*"] [pid attr]
       (json/write-str (basic-query (d/connect db-uri) attr [["playerID" pid]])))

  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

;(run-jetty handler {:port 3000})

(def port 5000)

(defn -main []
  (println "Server Started")
  (run-jetty app {:port port}))
