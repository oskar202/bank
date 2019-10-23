# RESTful web service for payments processing


## Payment module:
_For all the payment operations and logging._

**Run the application:**
`./gradlew bootRun`

**Swagger UI:**
http://localhost:8080/swagger-ui.html

**Check application health from actuator:**
http://localhost:8081/actuator/health

**"Database" is in hashmap for simplifying the development**

Payment entries can bee seen via actuator:
http://localhost:8081/actuator/database/payments

Failed notifications can be seen via actuator:
http://localhost:8081/actuator/database/failed-notifications

**GeoLite2 database is used to find user location by ip address**

User IP address is found from external api as the application is running in localhost. 
If the project is deployed somewhere, then finding IP address should be replaced with: `request.getRemoteHost()` which comes from `HttpServletRequest`

## Audit module:

_Payment module notifies this module when endpoints of payment module are called._

**Run the application:**
`./gradlew bootRun`
