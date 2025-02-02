# Stage 1: Build the Java application
FROM maven:3.9.9-eclipse-temurin-21 AS build

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package


# Stage 2: Create the final image Java
FROM eclipse-temurin:21
ARG JAR_FILE=target/*.jar
COPY --from=build $JAR_FILE /app/runner.jar

WORKDIR /app

# Start the Java application
EXPOSE 8080 
ENTRYPOINT ["java", "-jar", "runner.jar"]

#CMD ["sleep", "infinity"]
#RUN echo "Hello from Dockerfile!"

#-------------------------------------
#
# build with: docker build -t serverless-blog .
#
# run it with: 
#
# docker network create sb-network
#
# docker run -d --network sb-network --name sb-mongo -p 27018:27017 mongo:latest
#
# docker run -d -p 8080:8080 --network sb-network --name sb-serverless-blog sasagu/serverless-blog 