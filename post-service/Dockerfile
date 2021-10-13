FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/post-service-0.0.1.jar PostService.jar
ENTRYPOINT ["java","-jar","PostService.jar"]