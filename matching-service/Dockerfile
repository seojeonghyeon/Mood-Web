FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/matching-service-0.0.1.jar MatchingService.jar
ENTRYPOINT ["java","-jar","MatchingService.jar"]