# 1. Build stage (utilizza Gradle per creare il jar)
FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

# 2. Run stage (esegue l'applicazione con Java)
FROM eclipse-temurin:21-jre
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/MacroTracking.jar
ENTRYPOINT ["java","-jar","/app/MacroTracking.jar"]