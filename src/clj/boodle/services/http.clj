(ns boodle.services.http
  (:require [boodle.api.routes :as api]
            [boodle.services.configuration :as config]
            [boodle.templates :as templates]
            [compojure
             [core :as compojure]
             [route :as route]]
            [mount.core :as mount]
            [org.httpkit.server :as server]
            [ring.middleware.reload :as reload]
            [ring.util.http-response :as response]))

(compojure/defroutes app
  (-> (compojure/routes
       (route/resources "/")
       (compojure/GET "/" [] (response/ok (templates/index-html)))
       (compojure/GET "/aims" [] (response/ok (templates/index-html)))
       (compojure/GET "/report" [] (response/ok (templates/index-html)))
       api/routes)
      reload/wrap-reload))

(defonce server (atom nil))

(defn stop-server! []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server! []
  (let [port (get-in config/config [:http :port])]
    (reset! server (server/run-server app {:port port}))))

(mount/defstate http-server
  :start (start-server!)
  :stop (stop-server!))
