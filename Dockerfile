ARG BUILDPLATFORM
ARG TARGETPLATFORM

# ===== build stage =====
FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Descarcă dependențele separat, ca layer-ul să fie cache-uit
RUN ./mvnw -q dependency:go-offline

COPY src/ src/

RUN ./mvnw -q -DskipTests package

# ===== runtime stage =====
FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jre

RUN groupadd --system app \
 && useradd --system --gid app --create-home app

USER app
WORKDIR /app

COPY --from=build --chown=app:app /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]