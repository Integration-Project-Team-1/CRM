# Stage 1: Install Maven stage
FROM debian:bullseye AS maven_installer

WORKDIR /tmp

# Install curl
RUN apt-get update && apt-get install -y curl

# Download Maven
RUN curl -O https://downloads.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz

# Extract Maven
RUN tar -zxvf apache-maven-3.8.5-bin.tar.gz

# Export Maven bin directory to PATH
ENV PATH="/tmp/apache-maven-3.8.5/bin:${PATH}"

# Verify Maven installation
RUN mvn -v

# Stage 2: Build stage
FROM maven_installer AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B dependency:go-offline

# Copy the entire local directory to the container
COPY . .

RUN mvn clean package

# Stage 3: Runtime stage
FROM openjdk:22-jdk AS runtime

WORKDIR /app

# Copy the JAR file built in the previous stage
COPY --from=build /app/target/CRM_Groep1-1.0-SNAPSHOT.jar .

# Command to run your application when the container starts
CMD ["java", "-jar", "CRM_Groep1-1.0-SNAPSHOT.jar"]
