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

(def s "want=AB&constraints%5B0%5D%5BplayerID%5D=strawda01")
(def s "want=C&constraints%5B0%5D%5B%5D=A&constraints%5B0%5D%5B%5D=B")
(clojure.pprint/pprint (form-decode s))

(defroutes _app
  (GET "/" [] "<h1>Welcome to Fantasy Baseball!</h1>")
  (GET "/teamids/" [] (json/write-str (teamIDs (d/connect db-uri))))
  (GET "/teamnames/" [] (json/write-str (team-names (d/connect db-uri))))
  (POST "/query/" request
       (let [params (request :params)
             body   (request :body)
             query  (request :query-string)]
                 (clojure.pprint/pprint request)
                 (clojure.pprint/pprint params)
                 (clojure.pprint/pprint body)
                 (clojure.pprint/pprint (json/read-str body))
          (json/write-str (basic-query (d/connect db-uri) (params :want) (params :constraints)))))
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
       (let [result (no-join-query (d/connect db-uri)
                                   ["yearID" attr] [["playerID" pid]])
             data (sort (:data result))
             years (map first data)
             values (map second data)]
          (json/write-str {:data values :rows years :columns [attr]})))

  (GET ["/year-attribute/:year/:attr/" :year #"\d{4}" :attr #"\w*"] [year attr]
       (let [result (no-join-query (d/connect db-uri)
                      ["playerID" attr] [["yearID" (Integer/parseInt year)]])
             result2 {:data (map second (:data result))
                      :columns [attr]}]
         (json/write-str result2)))

  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))


(def app (handler/wrap-nested-params (handler/api _app)))

;(run-jetty handler {:port 3000})

(def port 5000)

(defn -main []
  (println "Server Started")
  (run-jetty app {:port port}))
