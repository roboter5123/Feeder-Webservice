FROM openjdk:17
EXPOSE 8080
ADD /target/feeder-webservice.jar /feeder-webservice.jar
ENV encryption_password = None
ENTRYPOINT "java", "-jar", "/feeder-webservice.jar"