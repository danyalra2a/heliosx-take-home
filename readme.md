## About this repo
HeliosX's brand new consultation backend service to replace the one they can't reuse.

It serves endpoints related to the "Consultation" phase of the user journey.

It is a Java21 application that uses Javalin framework to serve endpoints and a MySQL database.

## Running locally

Dependencies:
- Docker runtime environment, e.g supplied by Docker Desktop or Rancher.
- (Optional) Database tool to interact with the database, e.g. DBeaver

To build and run the project with Docker:
- To start the services run `./gradlew start`
- To stop the services run `./gradlew stop`

- The database service is available on port 3036, and has configuration in the `.env` file.
- The server is available on port 8088.

To connect to the database:
- Using your DB tool of choice use the following log in details:
  - URL: `jdbc:mysql://localhost:3306/?allowPublicKeyRetrieval=true&useSSL=false`
  - username: `root`
  - password: `Password!23`

Troubleshooting:
- Currently only logging to the containers while running, so the following may be of use: `docker compose logs`, and `docker logs <container_id>`.

## Endpoints
- I figured it wasn't worth spending time setting up a redoc/swagger page, instead created a Postman collection [here](https://danyalraza-4374908.postman.co/workspace/Danyal-Raza's-Workspace~deff8857-f18d-496e-8fd9-c7449571a049/collection/44046121-abf848ea-ce28-4405-8f04-be7e77c89274?action=share&creator=44046121).
- In summary there are 3 endpoints (for which there are examples in the postman collection):
  1. GET `/consultation/{consultationId}` which returns the list of questions and answers for a consultation flow
  2. POST `/consultation/{consultationId}` to submit answers to questions, it returns a `submissionId` on success
  3. GET `/consultation/{submissionId}/result` to check the result of a submission

## Comments and tradeoffs
- Architecture
  - Chose Javalin and containers because it's what I've been using most recently and often, Spring Boot may have simplified some things, but I haven't used it in a while - though there are all the usual benefits to using containers, and it is nice to see what is going on in the code explicitly.
  - Controller, service, repository layered architecture - the names are quite generic and there could be an argument for having multiple of each, but for now I believe it would be overengineering the code
  - Database
    - I spend a decent amount of time coming up with a schema, initially I wanted to have classes of answers to reduce duplication (e.g. of yes_no questions), but the SQL queries would be more complex and time was a limiting factor, especially since it is quite normalised.
  - The project only runs locally and would need modification (e.g. security, health checks, etc) to be run in production.
  - Due to simplicity there is no distinction between objects (e.g. DTO, DAO), since there isn't a need for the layers of abstraction between the layers.
- Validation/Exception handling and Logging
  - There should be more of this (e.g. when submitting questions, you could argue that the FE should primarily be responsible for this), and they should be more granular to produce more useful errors, but I believe I included important ones.
  - There is no persistent/easy to follow logging, it is only rudimentary.
- Endpoints
  - There is currently no proper endpoint to list all available consultation types.
  - The POST endpoint doesn't return the result immediately (even though it could), since in the future it would make sense to use queuing, this also ties into how the service would work with others, e.g. the frontend calling both this and payments service 
  - Ideally there would be some form of authorisation for the `/result` endpoint, especially if questions have sensitive information, Javalin supports access management which is one way to do this, but I did not have the time to add this.
    - The verification is inefficient due to multiple DB calls, these could be coalesced in the future though, but I like that the logic is kept in the service layer.
  - It should be relatively straightforward to add endpoints that allow doctors to approve submissions, this should definitely have authentication.
  - Ideally there should be some kind of time/logging so there's an audit trail.
- Testing
  - Unfortunately testing is minimal and only consists of unit tests
    - In the ServiceTest file I tested the `calculateConsultationStatus` method which is a bit subtle, the tests would ensure a refactor/extension goes well.
    - In the ServiceTest file I excluded tests for the other methods since their flows are simple and would be tested when running locally, though they should definitely be added if time permitted.
    - In the ControllerTest file I tested the happy/unhappy paths for one endpoint, the other endpoints would be fairly similar, and were excluded due to time.
    - There are no Repository tests due to time, but also due to them not being incredibly value since a lot of the logic is in the SQL, integration tests and e2e testing would be super valuable here.
  - The controller uses the `exceptionResponse` method to simplify testing.
  - Integration tests and e2e tests were skipped due to time, they require more scaffolding, and for this simple repo manual testing was (hopefully) sufficient.

