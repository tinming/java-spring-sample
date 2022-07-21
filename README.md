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
