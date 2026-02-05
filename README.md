# StopDemarchage

Application Android pour bloquer automatiquement les appels de démarchage téléphonique en France.

## Fonctionnalités

- **Blocage automatique** des appels basé sur les préfixes de numéros
- **Préfixes français préconfigurés** : 0162, 0163, 0270, 0271, 0377, 0378, 0424, 0425 (formats national et international)
- **Gestion des préfixes** : ajout, modification, suppression, activation/désactivation
- **Historique des appels bloqués** avec statistiques (jour, semaine, mois)
- **Import/Export** de la configuration en JSON
- **Mode silencieux** : les appels bloqués sont envoyés directement sur la messagerie vocale

## Captures d'écran

L'application comprend 4 écrans principaux :
- **Accueil** : statut de protection et statistiques
- **Préfixes** : gestion des préfixes à bloquer
- **Historique** : liste des appels bloqués
- **Paramètres** : configuration de l'application

## Prérequis

- Android 12 (API 31) ou supérieur
- Permissions requises :
  - `READ_PHONE_STATE` : détecter les appels entrants
  - `READ_CALL_LOG` : accéder aux informations des appels
  - `ANSWER_PHONE_CALLS` : rejeter les appels indésirables
  - `POST_NOTIFICATIONS` : notifications de blocage (Android 13+)

## Stack technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose + Material Design 3
- **Architecture** : MVVM avec Repository pattern
- **Base de données** : Room
- **Injection de dépendances** : Hilt
- **Préférences** : DataStore
- **Sérialisation** : Kotlinx Serialization

## Installation

### Depuis les sources

```bash
# Cloner le dépôt
git clone https://github.com/julien-gomy/StopDemarchage.git
cd StopDemarchage

# Générer l'APK de debug
./gradlew assembleDebug

# Générer l'APK de release
./gradlew assembleRelease

# Générer l'App Bundle pour le Play Store
./gradlew bundleRelease
```

### Configuration requise pour le build

- JDK 17
- Android SDK avec compileSdk 35
- Gradle 8.12+

## Structure du projet

```
app/src/main/java/com/stopdemarchage/
├── StopDemarchageApp.kt          # Application Hilt
├── MainActivity.kt                # Activity principale
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt        # Room Database
│   │   ├── PrefixDao.kt          # DAO préfixes
│   │   └── BlockedCallDao.kt     # DAO appels bloqués
│   ├── model/
│   │   ├── Prefix.kt             # Entity préfixe
│   │   └── BlockedCall.kt        # Entity appel bloqué
│   └── repository/
│       └── CallRepository.kt      # Repository
├── service/
│   └── CallBlockerService.kt     # CallScreeningService
├── ui/
│   ├── theme/                    # Material 3 theme
│   ├── screens/
│   │   ├── HomeScreen.kt         # Écran principal
│   │   ├── PrefixListScreen.kt   # Gestion préfixes
│   │   ├── HistoryScreen.kt      # Historique
│   │   └── SettingsScreen.kt     # Paramètres
│   └── navigation/
│       └── NavGraph.kt           # Navigation Compose
└── viewmodel/
    ├── PrefixViewModel.kt
    ├── HistoryViewModel.kt
    └── SettingsViewModel.kt
```

## Comment ça fonctionne

1. L'application s'enregistre comme service de filtrage d'appels (`CallScreeningService`)
2. À chaque appel entrant, le numéro est comparé aux préfixes actifs
3. Si le numéro correspond à un préfixe bloqué :
   - L'appel est rejeté silencieusement (envoyé sur messagerie)
   - L'appel est enregistré dans l'historique
4. Les appels normaux passent sans interruption

## Configuration initiale

1. Installer l'application
2. Accorder les permissions demandées
3. Définir l'application comme service de filtrage d'appels par défaut
4. Les préfixes de démarchage français sont activés par défaut

## Politique de confidentialité

Voir [PRIVACY_POLICY.md](PRIVACY_POLICY.md)

**Points clés :**
- Toutes les données restent sur l'appareil
- Aucune donnée n'est transmise à des serveurs externes
- Aucun tracker ni publicité

## Licence

Ce projet est open source.

## Auteur

Julien Gomy - [@julien-gomy](https://github.com/julien-gomy)
