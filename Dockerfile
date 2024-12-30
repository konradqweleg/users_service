FROM openjdk:17-jdk-slim AS runtime

WORKDIR /app

# Kopiowanie pliku z katalogu user_service/build/libs
COPY build/libs/mychat-user-service.jar mychat-user-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "mychat-user-service.jar"]
