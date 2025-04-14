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

## Comments and tradeoffs