FROM openjdk:17
EXPOSE 8080
ADD /target/feeder-webservice.jar /feeder-webservice.jar
ENTRYPOINT ["java", "-jar", "/feeder-webservice.jar"]