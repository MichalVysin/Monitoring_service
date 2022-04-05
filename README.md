# Monitoring service
### Java task

Endpoints monitoring service
The task is to create a REST API JSON Java microservice which allows you to monitor particular http/https URLs.
The service should allow you to
create, edit and delete monitored URLs and list them for a particular user (CRUD),
monitor URLs in the background and log status codes + the returned payload,
and to list the last 10 monitored results for each particular monitored URL.

What’s expected:

{

name: "Batman",

email: "batman@example.com",

accessToken: "dcb20f8a-5657-4f1b-9f7f-ce65739b359e"

}

In detail:
design REST endpoints for the management of MonitoredEndpoints
monitor endpoints in the background and create MonitoringResult
implement an endpoint for getting MonitoringResult
implement a microservice created in Java, ideally written in Spring Boot. Use MySQL for the database. Use Spring MVC as a REST framework
authentication: do it in the HTTP header according to your choice, you will get the accessToken in it
authorization: a User can see only MonitoredEndpoints and Result for him/herself only (according to accessToken)
don’t forget model validations (you decide what’s necessary to validate)
write basic tests in JUnit or TestNG
bonus points: create a Dockerfile, add docker-compose and describe how to start and run it in Docker.

