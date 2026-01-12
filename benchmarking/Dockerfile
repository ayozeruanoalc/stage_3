FROM eclipse-temurin:17-jdk

COPY target/benchmarking-1.0-SNAPSHOT.jar /app/benchmark.jar

EXPOSE 5704

WORKDIR /app

ENTRYPOINT ["java", "-jar", "benchmark.jar"]