# evamed-connector

EVAMED Connector - Projet Spring Boot 3.4 / Java 17 initialisé avec Maven.

## Prérequis

- Java 17
- Maven 3.6+
- PostgreSQL (pour la génération jOOQ)

## Démarrage

1. Installer Java 17 et Maven.

2. **Build standard (sans génération jOOQ)** :
   ```bash
   mvn clean install
   ```

3. **Build avec génération du code jOOQ** :
   
   La génération de code jOOQ est désactivée par défaut pour accélérer les builds. 
   Pour activer la génération, utilisez le profil `jooq-codegen` :
   
   ```bash
   mvn clean install -Pjooq-codegen
   ```
   
   ⚠️ **Note** : La génération jOOQ nécessite une connexion à la base de données PostgreSQL configurée dans le `pom.xml`.

4. Lancer l'application :
   ```bash
   mvn spring-boot:run
   ```

## Profils Maven

### Profile `jooq-codegen`

Ce profil active la génération automatique de code jOOQ à partir du schéma de base de données PostgreSQL.

**Quand l'utiliser** :
- Lors du premier build du projet
- Après des modifications du schéma de base de données
- Pour régénérer les classes jOOQ suite à des changements de structure

**Configuration** :
- Base de données : `jdbc:postgresql://10.10.200.15:5432/evamed`
- Schéma : `evamed`
- Package de destination : `fr.has.evamed.domain.entities`
- Répertoire de sortie : `target/generated-sources/evamed`

**Exemples d'utilisation** :
```bash
# Build complet avec génération jOOQ
mvn clean install -Pjooq-codegen

# Génération jOOQ uniquement
mvn generate-sources -Pjooq-codegen

# Build sans tests avec jOOQ
mvn clean install -Pjooq-codegen -DskipTests
```

## Technologies

Les dépendances principales incluent :
- Spring Boot 3.4.1
- Spring Web, Actuator, Validation
- Spring Boot jOOQ Starter
- PostgreSQL Driver
- jOOQ 3.20.9
- Lombok
- Spring DevTools
- SpringDoc OpenAPI 2.5.0
- OpenAPI Generator Maven Plugin 7.8.0
