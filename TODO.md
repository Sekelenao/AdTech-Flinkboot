# AdTech Flinkboot - Roadmap & Tasks

## 1. Gestion du statut CAPPED (Option B + Solution 1)
- [ ] **Job Budget (`budget`)** :
  - Dans `SpentBudgetEnricher` & `RemainingBudgetEnricher` : si la campagne est déjà `CAPPED` (ou si `remainingBudget <= 0`), plafonner le débit (`spentBudget` ne dépasse pas `allocatedBudget`, coût facturé = 0).
  - Facturation partielle si le coût d'un clic dépasse le reliquat restant, puis passage immédiat en `CAPPED`.
  - Maintenir le comptage des clics (`clickCount`) comme clics non facturés ("unbilled / over-cap").
- [ ] **Job Attribution (`attribution`)** :
  - Solution 1 : Les conversions issues d'un clic en période `CAPPED` continuent d'être attribuées normalement (l'internaute ayant bien converti suite au clic, coût média = 0$).

## 2. Campagnes & Scénarios Démo (Anglais & Storytelling Réaliste)
- [ ] Refactoriser `flash-sale.json` :
  - Marques : Nike (`nike-air-max-day`), Spotify (`spotify-wrapped-surge`)
  - Rafales de clics (`BURST_CLICKS`) et conversions à fort panier d'achat.
- [ ] Refactoriser `budget-capping-stress.json` :
  - Marque : Uber (`uber-surge-night`) avec budget serré ($4.00)
  - Clics rapides -> seuil 75% -> `CAPPED` -> `TOP_UP_BUDGET` ($15.00) -> réactivation en `ACTIVE`.
- [ ] Refactoriser `showcase-demo.json` :
  - Marques : Apple (`apple-vision-pro`), Airbnb (`airbnb-summer-escape`), Uber (`uber-airport-shuttle`), Google (`google-cloud-summit`)
  - Scénario complet multi-marques : impressions display, clics, pause/reprise, attribution last-touch, capping et alertes.

## 3. Nettoyage Automatique & Reset (Fluss & Kafka)
- [ ] Créer un service de nettoyage (`EnvironmentResetService`) dans `demo` :
  - Nettoyage Kafka : purge des enregistrements via `AdminClient.deleteRecords` sur tous les topics applicatifs (`campaign_budgets`, `ad_impressions`, `ad_clicks`, `conversions`, `budget_alerts`, `attributed_conversions`).
  - Nettoyage Fluss : suppression des enregistrements de la table `campaign_live_budgets` via `UpsertWriter.delete(...)`.
  - Purge des caches mémoire / Spring (`@CacheEvict` sur `ContractService`).
- [ ] Intégrer le nettoyage automatique dans `ScenarioService` au lancement de chaque scénario :
  - Interdire tout nettoyage / lancement si `scenarioService.isRunning() == true`.
- [ ] Ajouter une action manuelle de Reset :
  - Endpoint `POST /scenarios/reset` dans `ScenarioController`.
  - Bouton "Reset Environment" dans la navigation du dashboard avec retour visuel.

## 4. Streaming des Alertes via Server-Sent Events (SSE)
- [ ] Consommateur Kafka pour `budget_alerts` :
  - Écouter le topic Kafka `budget_alerts` émis par le job Flink `BudgetJob`.
- [ ] Service de diffusion SSE (`AlertStreamService`) :
  - Gestion des souscriptions d'écouteurs `SseEmitter`.
  - Diffusion en temps réel des `BudgetExhaustedAlert` reçues de Kafka vers le navigateur.
- [ ] Endpoint HTTP SSE :
  - `GET /api/alerts/stream` dans un controller Spring MVC.
- [ ] Frontend Réactif (UI) :
  - Connecter l'API JavaScript `EventSource` dans la vue `/alerts`.
  - Mise à jour instantanée du tableau des alertes (animation d'insertion de ligne) et du badge de notification sans rechargement de page.
