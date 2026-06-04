# syntax=docker/dockerfile:1

# ===== Dependencies =====
FROM maven:3.9-eclipse-temurin-17 AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline

# ===== Build =====
FROM dependencies AS builder
WORKDIR /app
COPY src ./src
RUN mvn -B clean package -DskipTests

# ===== Runtime =====
FROM eclipse-temurin:17-jre
WORKDIR /app

RUN groupadd --system printlabel \
    && useradd --system --gid printlabel --home-dir /app --shell /usr/sbin/nologin printlabel

COPY --from=builder /app/target/*.jar app.jar
RUN chown -R printlabel:printlabel /app

USER printlabel
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
