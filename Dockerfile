FROM gradle:6.7.1-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle clean build --no-daemon

FROM amazoncorretto:11-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
