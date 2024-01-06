FROM openjdk:17
EXPOSE 8080
WORKDIR /app
COPY target/worker-service.jar /app/worker-service.jar
ENTRYPOINT ["java", "-jar", "worker-service.jar"]
