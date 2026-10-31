# Backend

API Spring Boot du projet Cabinet Medical.

## Prerequis

- Java 17
- Maven 3.9+
- PostgreSQL 17 ou Docker Compose

## Lancement

Depuis la racine, lancer PostgreSQL avec `docker compose up -d postgres`, puis executer `mvn spring-boot:run` dans ce dossier. Le backend charge automatiquement le fichier `.env` situe a la racine ; les variables d'environnement du systeme restent prioritaires.

Endpoint public de verification : `GET /api/health`.
