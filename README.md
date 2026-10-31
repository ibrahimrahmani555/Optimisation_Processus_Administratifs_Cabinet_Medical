# Cabinet Medical - Version 2

Reconstruction fiable du systeme de gestion d'un cabinet medical.

## Stack

- Frontend : React 18, TypeScript et Vite
- Backend : Java 17 et Spring Boot 3.5
- Base de donnees : PostgreSQL 17
- Migrations : Flyway
- Infrastructure locale : Docker Compose

## Demarrage rapide

1. Copier `.env.example` vers `.env`.
2. Lancer PostgreSQL : `docker compose up -d postgres`.
3. Lancer le backend : `cd backend && mvn spring-boot:run`.
4. Lancer le frontend : `cd frontend && npm install && npm run dev`.

Le frontend est disponible sur `http://localhost:5173` et l'API sur `http://localhost:8080`.

## Documentation

- [Perimetre fonctionnel](docs/01-perimetre-fonctionnel.md)
- [Cas d'utilisation](docs/02-cas-utilisation.md)
- [Diagramme de classes](docs/03-diagramme-classes.md)
- [Regles metier](docs/04-regles-metier.md)
- [Backlog](docs/05-backlog.md)
- [Strategie Git](docs/06-strategie-git.md)
- [Systeme visuel](docs/07-design-system.md)
- [F01 — Authentification](docs/F01-authentification.md)

## Etat

Sprint 0 : analyse, architecture et initialisation technique.
