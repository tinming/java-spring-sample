FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY config/application.properties config/application.properties
ENTRYPOINT ["java","-jar","/app.jar","-Dspring.config.location=file:\"/config/\""]
