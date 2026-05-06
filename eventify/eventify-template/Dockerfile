FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Копируем файлы сборщика
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Копируем спецификацию и исходники
COPY api api
COPY src src

RUN chmod +x gradlew

# Сборка без тестов для ускорения
RUN ./gradlew bootJar -x test

# Этап запуска
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
