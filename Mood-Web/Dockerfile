FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/mood-web-0.0.1.jar MoodWebServer.jar
ENTRYPOINT ["java","-jar","MoodWebServer.jar"]