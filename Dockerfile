#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
RUN mvn clean package

#
# Package stage
#
FROM openjdk:11
ARG JAR_FILE=risk-web/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]