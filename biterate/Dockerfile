# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime 
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

# Copia o application-prod.properties para o classpath
COPY --from=build /app/src/main/resources/application.properties ./config/application.properties

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Dspring.config.additional-location=optional:file:/app/config/", \
  "-jar", "app.jar"]
