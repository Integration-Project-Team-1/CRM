# Stage 1: Build stage
FROM maven:latest AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -B dependency:go-offline

# Copy the entire local directory to the container
COPY . .

# Add the submodule
RUN apt-get update && apt-get install -y git \
    && git clone https://github.com/Integration-Project-Team-1/xmlxsd.git tmp_validation \
    && mv tmp_validation/* src/main/validation \
    && rm -rf tmp_validation \
    && apt-get remove -y git \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN mvn clean package

# Stage 2: Runtime stage
FROM openjdk:22-jdk

WORKDIR /app

# Copy the JAR file built in the previous stage
COPY --from=build /app/target/CRM_Groep1-1.0-SNAPSHOT.jar .

# Command to run your application when the container starts
CMD ["java", "-jar", "CRM_Groep1-1.0-SNAPSHOT.jar"]
