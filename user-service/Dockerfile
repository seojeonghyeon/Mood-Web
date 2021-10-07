FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/user-service-0.0.1.jar UserService.jar
ENTRYPOINT ["java","-jar","UserService.jar"]