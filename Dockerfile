FROM maven:3.6.3-jdk-11-slim AS build
COPY src /usr/iemdb-back/src
COPY pom.xml /usr/iemdb-back
RUN mvn -f /usr/iemdb-back/pom.xml clean install

FROM openjdk:17
COPY --from=build /usr/iemdb-back/target/iemdb.jar iemdb.jar
ENTRYPOINT ["java", "-jar", "iemdb.jar"]