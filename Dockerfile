# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Cache dependencies
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Copy source and build application using the production profile
COPY src ./src
RUN mvn -Pproduction package

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
