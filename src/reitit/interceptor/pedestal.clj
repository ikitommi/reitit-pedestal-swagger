(ns reitit.interceptor.pedestal
  (:require [io.pedestal.interceptor.chain :as chain]
            [io.pedestal.interceptor :as interceptor]
            [io.pedestal.http :as http]
            [reitit.interceptor]
            [reitit.http])
  (:import (reitit.interceptor Executor)))

(def pedestal-executor
  (reify
    Executor
    (queue [_ interceptors]
      (->> interceptors
           (map (fn [{:keys [::interceptor/handler] :as interceptor}]
                  (or handler interceptor)))
           (map interceptor/interceptor)))
    (enqueue [_ context interceptors]
      (chain/enqueue context interceptors))))

(defn routing-interceptor
  ([router]
    (routing-interceptor router nil))
  ([router default-handler]
    (routing-interceptor router default-handler nil))
  ([router default-handler {:keys [interceptors]}]
   (interceptor/interceptor
     (reitit.http/routing-interceptor
       router
       default-handler
       {:executor pedestal-executor
        :interceptors interceptors}))))

(defn default-interceptors [spec router]
  (-> spec
      (assoc ::http/routes [])
      (http/default-interceptors)
      (update ::http/interceptors (comp vec butlast))
      (update ::http/interceptors conj router)))
