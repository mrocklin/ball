(ns protosite.core
  (:require [ring.adapter.jetty :refer :all]
            [clojure.data.json :as json]
            [compojure.route :as route]
            [compojure.core :refer :all]
            [protosite.models :refer :all]
            [protosite.lahman-datomic :refer [db-uri]])
  (:use [datomic.api :only [db q] :as d]))


(defroutes app
  (GET "/" [] "<h1>Welcome to Fantasy Baseball!</h1>")
  (GET ["/team/:team/:year/" :team #"\w{3}" :year #"\d{4}"] [team year]
         (ready-for-data-tables (team-record  (d/connect db-uri)
                                             team (Integer/parseInt year))))
  (GET "/teamids/" [] (json/write-str (teamIDs (d/connect db-uri))))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

;(run-jetty handler {:port 3000})

(def port 5000)

(defn -main []
  (println "Server Started")
  (run-jetty app {:port port}))
