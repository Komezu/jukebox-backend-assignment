FROM openjdk:17-jdk-slim AS build
WORKDIR /app
COPY . /app
RUN ./mvnw clean install

FROM openjdk:17-jdk-slim AS test
WORKDIR /app
COPY . /app
RUN ./mvnw test

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /app/jukebox.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/jukebox.jar"]
