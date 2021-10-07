FROM openjdk:17-ea-11-slim
VOLUME /tmp
COPY target/apigateway-service-0.0.1.jar ApigatewayServer.jar
ENTRYPOINT ["java","-jar","ApigatewayServer.jar"]