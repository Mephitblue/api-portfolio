# syntax=docker/dockerfile:1

# maven:3.9-eclipse-temurin-17 is the official Maven image built on the same
# eclipse-temurin JDK distribution used in production. Pinning the Maven major
# version (3.9) keeps the build reproducible without locking to a patch version.
FROM maven:3.9-eclipse-temurin-17 AS test-runner

# Set a predictable working directory inside the container
WORKDIR /app

# Copy the POM first so Docker can cache the dependency download layer.
# Maven re-downloads dependencies only when pom.xml actually changes.
COPY pom.xml .

# Download all dependencies without running any tests.
# --batch-mode suppresses interactive prompts and keeps CI logs readable.
RUN mvn dependency:go-offline --batch-mode -q

# Copy source after the dependency layer is cached
COPY src ./src

# Default command: run the full test suite.
# Override at runtime to run a single class: docker run ... mvn test -Dtest=PostsTest
CMD ["mvn", "test", "--batch-mode"]
