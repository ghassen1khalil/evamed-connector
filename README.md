# evamed-connector

EVAMED Connector - Projet Spring Boot 4 / Java 21 initialisé avec Maven.

## Démarrage

1. Installer Java 21 et Maven.
2. Construire et lancer les tests :
   ```bash
   mvn clean verify
   ```
3. Lancer l'application :
   ```bash
   mvn spring-boot:run
   ```

Les dépendances principales incluent Spring Web, Actuator, Validation, Spring Data JPA, le driver PostgreSQL, Lombok, Spring DevTools et l'intégration OpenAPI (springdoc) ainsi que le plugin `openapi-generator-maven-plugin`.
