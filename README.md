# RESTful web service for payments processing



## Payment module:
_For all the payment operations and logging._

**Run the application:**
`./gradlew bootRun`

**Swagger UI:**
http://localhost:8080/swagger-ui.html

**Check application health from actuator:**
http://localhost:8081/actuator/health

**"Database" is in hashmap and all the payment entries can bee seen via actuator:**
http://localhost:8081/actuator/database

**GeoLite2 database is used to find user location by ip address**


## Audit module:

_Payment module notifies this module when endpoints of payment module are called._
