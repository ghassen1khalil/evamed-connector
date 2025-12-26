# ==============================
# Build stage
# ==============================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests -Pjooq-codegen

# ==============================
# Runtime stage
# ==============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Sécurité : utilisateur non-root
RUN groupadd -r spring && useradd -r -g spring spring
USER spring

# Variables d’environnement par défaut (override possible)
ENV SPRING_PROFILES_ACTIVE=default \
    SPRING_DATASOURCE_URL=jdbc:postgresql://10.10.200.15:5432/evamed \
    SPRING_DATASOURCE_USERNAME=evamed \
    SPRING_DATASOURCE_PASSWORD=evamed \
    JAVA_OPTS=""

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
