# F01 — Authentification

## Objectif

Permettre aux administrateurs, medecins et secretaires de se connecter avec un compte actif rattache a un cabinet actif, puis de proteger les API et l'interface selon leur role.

## Criteres d'acceptation

- l'utilisateur se connecte avec son e-mail et son mot de passe ;
- un e-mail inconnu ou un mot de passe incorrect retourne le meme message generique ;
- un compte inactif ou rattache a un cabinet inactif est refuse ;
- le mot de passe est stocke uniquement sous forme BCrypt et n'est jamais retourne ;
- le serveur emet un JWT signe HS256 valable 30 minutes par defaut ;
- toutes les API sont protegees sauf `POST /api/auth/login` et `GET /api/health` ;
- le serveur revalide l'activation du compte et du cabinet a chaque requete ;
- l'interface restaure la session pendant l'onglet courant, affiche le role et permet la deconnexion ;
- les menus sont limites au role connecte.

La deconnexion supprime le JWT de la session du navigateur. Comme l'API est stateless, aucun etat de session n'est conserve sur le serveur.

## Contrats API

### `POST /api/auth/login`

```json
{
  "email": "admin@cabinet.ma",
  "password": "mot-de-passe"
}
```

Reponse `200` : `accessToken`, `tokenType`, `expiresIn` et les informations non sensibles de l'utilisateur.

### `GET /api/auth/me`

Entete obligatoire : `Authorization: Bearer <token>`.

Reponse `200` : identite, role et cabinet de l'utilisateur courant.

## Premier administrateur

Au premier demarrage, definir les variables suivantes. Le compte n'est cree que si les trois valeurs sont presentes et si l'e-mail n'existe pas deja.

```text
BOOTSTRAP_CABINET_NAME=Cabinet Central
BOOTSTRAP_ADMIN_EMAIL=admin@cabinet.ma
BOOTSTRAP_ADMIN_PASSWORD=un-mot-de-passe-fort
JWT_SECRET=une-valeur-aleatoire-d-au-moins-32-octets
```

Apres creation, les variables `BOOTSTRAP_*` peuvent etre retirees. Ne jamais versionner leurs vraies valeurs.

## Tests couverts

- connexion valide et absence du mot de passe dans la reponse ;
- acces refuse sans JWT ;
- lecture de l'utilisateur courant avec JWT ;
- refus d'un mauvais mot de passe ;
- refus d'un compte ou cabinet inactif ;
- validation des champs obligatoires.
