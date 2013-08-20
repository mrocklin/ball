(ns protosite.core
  (:gen-class)
  (:require [ring.adapter.jetty :refer :all]
            [ring.util.codec :refer [form-decode]]
            [clojure.data.json :as json]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [compojure.core :refer :all]
            [protosite.models :refer :all]
            [protosite.lahman-datomic :refer [db-uri]])
  (:use [datomic.api :only [db q] :as d]))


(defn handle-query [request f]
   (let [params (request :params)
         body   (request :body)
         want   (if (contains? params "want")
                  (params "want")
                  (-> request :query-string form-decode json/read-str (get "want")))
         constraints (if (contains? params "constraints")
                       (params "constraints")
                       (-> request :query-string form-decode json/read-str (get "constraints")))]
      (json/write-str (f (d/connect db-uri) want constraints))))

(defroutes app
  (GET "/" [] "<h1>Welcome to Fantasy Baseball!</h1>")
  (GET "/query/" request
       (handle-query request basic-query))
  (GET "/lahman-table/" request
       (handle-query request no-join-query))
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

  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))

;(run-jetty handler {:port 3000})

(def port 5000)

(defn -main []
  (println "Server Started")
  (run-jetty app {:port port}))
