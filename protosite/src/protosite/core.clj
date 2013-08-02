(ns protosite.core
  (:require [ring.adapter.jetty :refer :all])
  (:require [compojure.route :as route])
  (:require [compojure.core :refer :all])
  (:require [protosite.models :refer [c-team-record]]))


(defroutes app
  (GET "/" [] "<h1>Welcome to Fantasy Baseball!</h1>")
  (GET ["/team/:team/:year" :team #"\w{3}" :year #"\d{4}"] [team year]
         (str (c-team-record team (Integer/parseInt year))))
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))


;(run-jetty handler {:port 3000})

(defn -main []
  (println "Server Started")
  (run-jetty app {:port 3000}))
