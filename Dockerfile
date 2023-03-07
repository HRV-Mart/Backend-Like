FROM openjdk:19
ARG JAR_FILE=build/libs/Backend-Like-0.0.1-SNAPSHOT.jar
ARG MONGODB_URI=mongodb://localhost:27017
ARG APPLICATION_PORT=8080
ENV MONGODB_URI=$MONGODB_URI
ENV APPLICATION_PORT=$APPLICATION_PORT
EXPOSE APPLICATION_PORT 27017
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
