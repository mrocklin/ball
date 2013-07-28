(ns protosite.core
  (:require [ring.adapter.jetty :refer :all])
  (:require [compojure.route :as route])
  (:require [compojure.core :refer :all]))


(defroutes app
  (GET "/" [] "<h1>Welcome to Fantasy Baseball!</h1>")
  (route/resources "/")
  (route/not-found "<h1>Page not found</h1>"))


;(run-jetty handler {:port 3000})
