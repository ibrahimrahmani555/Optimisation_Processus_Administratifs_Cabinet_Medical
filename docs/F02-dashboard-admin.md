# F02 — Dashboard administrateur

## Accès

Les endpoints du dashboard exigent un JWT valide avec le rôle `ADMIN`.

## Endpoints

- `GET /api/admin/dashboard/statistics`
- `GET /api/admin/dashboard/cabinets-evolution?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /api/admin/dashboard/cabinets-status`
- `GET /api/admin/dashboard/users-distribution`
- `GET /api/admin/dashboard/registrations-evolution?from=YYYY-MM-DD&to=YYYY-MM-DD`
- `GET /api/admin/dashboard/recent-activities?limit=10`

Les paramètres de période sont optionnels. Par défaut, les douze derniers mois sont retournés. La limite des activités est comprise entre 1 et 50.

## Données

Les statistiques sont calculées depuis PostgreSQL :

- cabinets : total, actifs et inactifs ;
- utilisateurs : médecins et secrétaires ;
- patients : patients non archivés ;
- séries : regroupement mensuel des dates de création ;
- activités : entrées de `audit_logs`, triées de la plus récente à la plus ancienne.

Lorsqu’aucune activité n’est journalisée, l’API retourne une liste vide afin que l’interface affiche son état vide sans données fictives.
