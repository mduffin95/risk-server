#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
COPY risk-cli /risk/risk-cli
COPY risk-lib /risk/risk-lib
COPY risk-web /risk/risk-web
COPY pom.xml /risk
RUN mvn -f /risk/pom.xml clean package

#
# Package stage
#
FROM openjdk:11
ARG JAR_FILE=/risk/risk-web/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]