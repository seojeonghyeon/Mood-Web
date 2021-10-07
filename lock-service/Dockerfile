FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/lock-service-0.0.1.jar LockService.jar
ENTRYPOINT ["java","-jar","LockService.jar"]