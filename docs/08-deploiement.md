# Deploiement

## Architecture

- Frontend React : GitHub Pages.
- API Spring Boot : Render.
- PostgreSQL : Neon.
- Branche de production : `deployment`.

## 1. Neon

Creer un projet `cabinet-medical`, puis relever les informations de connexion directe : hote, base, utilisateur et mot de passe.

La variable JDBC doit suivre ce format :

```text
jdbc:postgresql://HOST/BASE?sslmode=require
```

Ne jamais enregistrer les vraies valeurs dans Git.

## 2. Render

Creer un Blueprint depuis ce depot. Render detectera `render.yaml` et construira `backend/Dockerfile`.

Renseigner les variables secretes suivantes :

```text
DATABASE_URL=jdbc:postgresql://HOST/BASE?sslmode=require
DATABASE_USERNAME=utilisateur_neon
DATABASE_PASSWORD=mot_de_passe_neon
FRONTEND_URL=https://ibrahimrh555.github.io
```

Verifier ensuite :

```text
https://URL_RENDER/api/health
```

## 3. GitHub Pages

Dans `Settings > Secrets and variables > Actions`, ajouter :

```text
VITE_API_URL=https://URL_RENDER/api
```

Dans `Settings > Pages`, selectionner `GitHub Actions` comme source.

Le workflow `.github/workflows/deploy-frontend.yml` compile et publie automatiquement le frontend a chaque modification de `frontend/` sur la branche `deployment`.

URL attendue :

```text
https://ibrahimrh555.github.io/Optimisation_Processus_Administratifs_Cabinet_Medical/
```

## 4. Verification finale

1. `/api/health` retourne `UP`.
2. Le frontend GitHub Pages s'affiche sans erreur 404.
3. Les appels reseau ciblent l'URL Render.
4. Le navigateur ne signale aucune erreur CORS.
5. Flyway applique les migrations sur Neon.
