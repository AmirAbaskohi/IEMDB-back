FROM openjdk:17
ADD target/iemdb.jar iemdb.jar
ENTRYPOINT ["java", "-jar", "iemdb.jar"]