FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY applications/app-service/build/libs/loan-service.jar loan-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "loan-service.jar"]
