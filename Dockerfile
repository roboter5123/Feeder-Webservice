FROM openjdk:8
EXPOSE 8080
ADD /target/feeder-webservice.jar /feeder-webservice.jar
ENTRYPOINT ["java", "-jar", "/feeder-webservice.jar"]