# Java Spring Sample

## Software Prerequisites

- Java
- Maven
- Docker
- Docker Compose

## Frameworks Used

- Spring Boot

## Local Development

First, install software prerequisities.

### Configuration

Edit `src/main/application.properties` and update the database, xero and log configuration. You'll need set up a PostgreSQL database before you can run project. See below to run project with `docker-compose`.

### Run Project

```console
mvn spring-boot:run
```

### Run Unit Tests

```console
mvn test
```

### Package JAR

```console
mvn package
```

## Inside Docker

### Run Unit Tests

```console
docker run -v "$(pwd)":/workspace -ti --rm -u 1000 -e MAVEN_CONFIG=/workspace/.m2 -w /workspace maven:latest mvn test -Duser.home=/workspace/
```

### Package JAR

```console
docker run -v "$(pwd)":/workspace -ti --rm -u 1000 -e MAVEN_CONFIG=/workspace/.m2 -w /workspace maven:latest mvn package -Duser.home=/workspace/
```

Notes:

- Your working directory will be mounted to `/workspace` inside the container.
- Non-root user is used to run and generate files.
- Non-root user is used so we need to set `user.home` when running the mvn command. See https://github.com/carlossg/docker-maven#running-as-non-root.

## Run project with Docker-Compose

This will create and start a PostgreSQL database and a server to run the JAR. Note: docker-compose will use the configuration in `/config/application.properties`.

```console
docker-compose up --build
```

Congratulations! The API is up and running. Go to `http://localhost:9000` and login with your Xero Account!
After logging in, go to `http://localhost:9000/accounts` to get all accounts or go to `http://localhost:9000/vendors` to get all vendors.

## Running on Production

On production, run `java -jar <jar_filename>.jar -Dspring.config.location=file:"<config_directory_with_trail>"`.

## Thoughts

Initially, I wanted to created a Restful API application that can help me call Xero API. As the application grew, I realize using Spring Boot and creating a Restful API application was too ambitious so I had to cut scope and create a prototype application that is not ready for customer use.

### Assumptions

- Xero API: I didn't find "Vendors" so I assumed it was "Contacts".

### TODOs
Here is a list of TODOs to make application "production ready":

- Add Async for 3rd party API and database calls
- Create user model for users to login to my application. A user may have one or many Tenant IDs
- Associate Tenant IDs with Accounts and Vendors
- Each user should have their own Oauth Token to Xero API
- Add more test cases. I've only created a few for demonstration purposes
- Better error handling. Exceptions are not being handled properly
- Add caching either with Memcached or Redis
- Create a front-end application to consume the API I created. I was thinking of using React or Angular
- Docker production deployment
- Versioning for release
- Instructions for loading project into SpringToolSuite
...and many more
