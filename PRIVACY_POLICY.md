# Politique de Confidentialité - StopDemarchage

**Dernière mise à jour : 5 février 2026**

## Introduction

StopDemarchage est une application Android conçue pour bloquer les appels téléphoniques indésirables (démarchage téléphonique) en se basant sur des préfixes de numéros configurables.

Cette politique de confidentialité explique comment l'application collecte, utilise et protège vos données.

## Données collectées

L'application collecte les données suivantes, qui restent **exclusivement stockées sur votre appareil** :

### 1. Numéros de téléphone des appels entrants
- **Finalité** : Comparer le numéro appelant avec les préfixes configurés pour déterminer si l'appel doit être bloqué
- **Stockage** : Les numéros des appels bloqués sont enregistrés dans l'historique local de l'application
- **Durée de conservation** : Configurable par l'utilisateur (nettoyage automatique disponible)

### 2. Préfixes de numéros
- **Finalité** : Définir les règles de blocage des appels
- **Stockage** : Base de données locale sur l'appareil

### 3. Statistiques d'utilisation
- **Finalité** : Afficher le nombre d'appels bloqués (aujourd'hui, cette semaine, ce mois)
- **Stockage** : Calculé localement à partir de l'historique

## Données NON collectées

L'application **NE collecte PAS** et **NE transmet PAS** :
- Vos contacts
- Le contenu de vos appels ou messages
- Votre localisation
- Vos identifiants personnels
- Aucune donnée vers des serveurs externes

## Permissions requises

| Permission | Raison |
|------------|--------|
| `READ_PHONE_STATE` | Détecter les appels entrants |
| `READ_CALL_LOG` | Accéder aux informations des appels pour le filtrage |
| `ANSWER_PHONE_CALLS` | Rejeter automatiquement les appels indésirables |
| `POST_NOTIFICATIONS` | Afficher des notifications lors du blocage d'appels (optionnel) |

## Stockage des données

Toutes les données sont stockées **localement** sur votre appareil dans une base de données SQLite chiffrée par le système Android. Aucune donnée n'est envoyée à des serveurs externes.

## Partage des données

**Aucune donnée n'est partagée** avec des tiers. L'application fonctionne entièrement hors ligne.

## Sécurité

- Les données sont protégées par le système de sandboxing d'Android
- Aucune transmission réseau n'est effectuée
- L'application ne contient pas de publicité ni de trackers

## Vos droits

Vous pouvez à tout moment :
- **Consulter** l'historique des appels bloqués dans l'application
- **Supprimer** tout ou partie de l'historique
- **Exporter** vos préfixes configurés
- **Désinstaller** l'application, ce qui supprimera toutes les données associées

## Modifications de cette politique

En cas de modification de cette politique de confidentialité, la date de mise à jour sera modifiée en haut de ce document.

## Contact

Pour toute question concernant cette politique de confidentialité, vous pouvez ouvrir une issue sur le dépôt GitHub du projet :

**https://github.com/julien-gomy/StopDemarchage/issues**

---

© 2026 StopDemarchage - Application open source
