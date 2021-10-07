FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY moodMSAKey.jks moodMSAKey.jks
COPY target/config-service-0.0.1.jar ConfigServer.jar
ENTRYPOINT ["java","-jar","ConfigServer.jar"]