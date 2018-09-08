(ns example.router
  (:require [io.pedestal.interceptor.chain :as chain]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [reitit.interceptor]
            [reitit.http])
  (:import (reitit.interceptor Executor)))

(def executor
  (reify
    Executor
    (queue [_ interceptors]
      (->> interceptors
           (map (fn [{:keys [::interceptor/handler] :as interceptor}]
                  (or handler interceptor)))
           (map interceptor/interceptor)))
    (enqueue [_ context interceptors]
      (chain/enqueue context interceptors))))

(defn router
  ([http-router]
   (router http-router nil))
  ([http-router default-handler]
   (router http-router default-handler nil))
  ([http-router default-handler {:keys [interceptors]}]
   (interceptor/interceptor
     (reitit.http/routing-interceptor
       http-router
       default-handler
       {:executor executor
        :interceptors interceptors}))))

(defn change-router [router]
  (fn [{:keys [name] :as interceptor}]
    (if (= name ::route/router) router interceptor)))

(defn default-interceptors [spec router]
  (-> spec
      (assoc ::http/routes [])
      (http/default-interceptors)
      (update ::http/interceptors (partial map (change-router router)))))
