# Notifications Library - Docker Image
# This Dockerfile packages the library with executable examples

# Stage 1: Build the library
FROM eclipse-temurin:21-jdk-alpine AS builder

LABEL maintainer="notifications-library"
LABEL description="Notifications Library - Framework-agnostic Java notification system"

# Install Maven
RUN apk add --no-cache maven

WORKDIR /build

# Copy pom.xml first (for better layer caching)
COPY pom.xml ./

# Download dependencies (cached if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B || true

# Copy source code
COPY src/ ./src/

# Build the project (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Stage 2: Create runtime image
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="notifications-library"
LABEL description="Notifications Library Runtime"

WORKDIR /app

# Copy the compiled JAR from builder stage
COPY --from=builder /build/target/notifications-library-*.jar /app/notifications-library.jar

# Copy documentation
COPY README.md /app/

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV LOG_LEVEL="INFO"

# Create a non-root user for security
RUN addgroup -g 1001 appuser && \
    adduser -D -u 1001 -G appuser appuser && \
    chown -R appuser:appuser /app

USER appuser

# Expose port (if needed for future HTTP examples)
EXPOSE 8080

# Default command: Run the examples
CMD ["sh", "-c", "java $JAVA_OPTS -cp /app/notifications-library.jar com.notifications.example.NotificationExamples"]

# Health check (optional)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD echo "Notifications Library Container is healthy" || exit 1
