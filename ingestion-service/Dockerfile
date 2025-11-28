FROM eclipse-temurin:17-jdk

COPY target/ingestion-service-1.0-SNAPSHOT.jar /app/ingestion.jar

EXPOSE 5071

WORKDIR /app

ENTRYPOINT ["java", "-jar", "ingestion.jar"]
