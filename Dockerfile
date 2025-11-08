# ============================
#   1. Build Stage
# ============================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn -q -B dependency:go-offline

# Copy the rest of the source code
COPY src ./src

# Build the application (skip tests for faster CI builds)
RUN mvn -q -B package -DskipTests

# ============================
#   2. Runtime Stage
# ============================
FROM eclipse-temurin:21-jre-alpine

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose default Spring Boot port
EXPOSE 8080

# Optimize JVM for container environments
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Start the Spring Boot app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
