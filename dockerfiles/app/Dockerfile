FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app

COPY build/libs/mychat-user-service.jar mychat-user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "mychat-user-service.jar"]
