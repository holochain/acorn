(ns auth.hoplon
 (:require
  [hoplon.core :as h]
  [javelin.core :as j]
  hoplon-auth0.state
  hoplon-auth0.hoplon))

(hoplon-auth0.api/login-from-url)

(defn login
 []
 (h/button
  :login! {:connection "github"}
  "Login with github"))

(j/cell= (prn hoplon-auth0.state/token))
