FROM openjdk:17
EXPOSE 8080
ADD /target/feeder-webservice.jar /feeder-webservice.jar
cmd java -Djasypt.encryptor.password=$encryption_password -jar /feeder-webservice.jar
