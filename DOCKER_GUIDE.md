# Docker Guide - Notifications Library

## Overview
This guide explains how to build and run the Notifications Library as a Docker container.

## Docker Image Details

- **Base Images:**
  - Build Stage: `eclipse-temurin:21-jdk-alpine` (with Maven)
  - Runtime Stage: `eclipse-temurin:21-jre-alpine` (lightweight)
  
- **Image Size:** ~298MB

- **Security:**
  - Runs as non-root user (appuser:1001)
  - Minimal alpine-based image

## Building the Docker Image

```bash
cd /tmp/notifications-library
docker build -t notifications-library:latest .
```

The build process:
1. Downloads dependencies (cached for faster rebuilds)
2. Compiles the source code
3. Creates a fat JAR with all dependencies
4. Packages the JAR in a minimal runtime image

## Running the Container

### Default Run (with examples)
```bash
docker run --rm notifications-library:latest
```

This will execute the `NotificationExamples` class, demonstrating:
- Email notifications (SendGrid simulation)
- SMS notifications (Twilio simulation)
- Push notifications (Firebase simulation)
- Async notifications
- Batch notifications

### Custom Java Options
```bash
docker run --rm -e JAVA_OPTS="-Xmx256m -Xms128m" notifications-library:latest
```

### Custom Log Level
```bash
docker run --rm -e LOG_LEVEL="DEBUG" notifications-library:latest
```

### Interactive Shell
```bash
docker run --rm -it notifications-library:latest sh
```

### Custom Java Class Execution
```bash
docker run --rm notifications-library:latest java -cp /app/notifications-library.jar com.your.CustomClass
```

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | JVM memory settings |
| `LOG_LEVEL` | `INFO` | Logging level (INFO, DEBUG, WARN, ERROR) |

## Container Contents

```
/app/
├── notifications-library.jar   (3.2MB - Fat JAR with all dependencies)
└── README.md                    (Documentation)
```

## Example Output

When you run the container, you'll see output like:

```
22:56:33.538 [main] INFO  c.n.example.NotificationExamples - === Notifications Library - Demo Examples ===
22:56:33.541 [main] INFO  c.n.example.NotificationExamples - ✅ NotificationService created successfully

📧 Example 1: Sending Email Notification
✅ SUCCESS - Channel: EMAIL | Provider ID: sendgrid-xxx | Timestamp: 2026-02-15T22:56:33.659Z

📱 Example 2: Sending SMS Notification
✅ SUCCESS - Channel: SMS | Provider ID: twilio-xxx | Timestamp: 2026-02-15T22:56:33.795Z

🔔 Example 3: Sending Push Notification
✅ SUCCESS - Channel: PUSH | Provider ID: firebase-xxx | Timestamp: 2026-02-15T22:56:34.018Z

=== All Examples Completed Successfully! ===
```

## Docker Commands Cheat Sheet

```bash
# List images
docker images notifications-library

# Run container
docker run --rm notifications-library:latest

# Run with custom environment
docker run --rm -e JAVA_OPTS="-Xmx1g" -e LOG_LEVEL="DEBUG" notifications-library:latest

# Get shell access
docker run --rm -it notifications-library:latest sh

# Remove image
docker rmi notifications-library:latest

# Inspect image
docker inspect notifications-library:latest

# View image layers
docker history notifications-library:latest
```

## Health Check

The container includes a basic health check that runs every 30 seconds:
- Interval: 30s
- Timeout: 3s
- Start Period: 5s
- Retries: 3

## Port Exposure

Port 8080 is exposed for future HTTP-based examples, though not used in the current implementation.

## Troubleshooting

### Container won't start
- Check Docker daemon is running: `docker info`
- Verify image exists: `docker images notifications-library`

### Out of Memory errors
- Increase heap size: `-e JAVA_OPTS="-Xmx1g"`

### Permission issues
- The container runs as non-root user (appuser:1001)
- Files in /app are owned by appuser

## Next Steps

1. **Integrate with CI/CD:** Add to GitHub Actions or Jenkins
2. **Push to Registry:** Upload to Docker Hub or private registry
3. **Production Configuration:** Add real provider credentials as environment variables
4. **Monitoring:** Add health check endpoints for production deployments

## Notes

- Tests are skipped during Docker build for faster builds (-DskipTests)
- All dependencies are included in the fat JAR (no external dependencies needed)
- The image uses multi-stage build for optimal size
- Build cache is optimized (pom.xml changes trigger only dependency download)
