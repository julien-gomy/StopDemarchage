# Journal de développement - StopDemarchage

## Session du 5 février 2026

### Objectif initial
Créer une application Android (API 31+, Kotlin) pour bloquer les appels de démarchage téléphonique basés sur des préfixes de numéros.

### Implémentation complète

#### 1. Structure du projet
- Création de l'architecture MVVM avec Repository pattern
- Configuration Gradle avec Kotlin DSL
- Intégration de Hilt pour l'injection de dépendances
- Configuration de Room pour la persistance
- Mise en place de Jetpack Compose avec Material Design 3

#### 2. Fonctionnalités développées
- **CallBlockerService** : Service de filtrage d'appels utilisant `CallScreeningService`
- **Gestion des préfixes** : CRUD complet avec activation/désactivation
- **Historique** : Enregistrement et affichage des appels bloqués
- **Statistiques** : Compteurs jour/semaine/mois
- **Import/Export** : Sérialisation JSON des préfixes
- **Paramètres** : Activation globale, notifications, nettoyage automatique

#### 3. Problèmes rencontrés et solutions

##### Blocage des appels non fonctionnel
- **Problème** : `respondToCall()` était appelé dans une coroutine `launch{}`, causant un retour prématuré de la méthode
- **Solution** : Utilisation de `runBlocking{}` pour garantir une réponse synchrone

##### Préfixes désactivés par défaut
- **Problème** : Les préfixes par défaut étaient créés avec `isEnabled = false`
- **Solution** : Changement à `isEnabled = true`

##### Crash de l'écran de debug
- **Problème** : Problème de thread-safety avec `MutableStateFlow`
- **Solution** : Utilisation de `_logs.update {}` et ajout d'un ID unique pour les clés LazyColumn

##### Appels non détectés
- **Problème** : L'application n'était pas configurée comme service de filtrage
- **Solution** : Ajout d'instructions claires et demande du rôle `ROLE_CALL_SCREENING`

### Publication Play Store

#### Génération du keystore
```bash
keytool -genkey -v -keystore release-keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias stopdemarchage
```

#### Configuration de signature
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("release-keystore.jks")
        storePassword = "stopdemarchage"
        keyAlias = "stopdemarchage"
        keyPassword = "stopdemarchage"
    }
}
```

#### Tentative d'ajout des symboles de débogage natifs
- **Problème** : Avertissement Play Console sur les symboles natifs manquants
- **Configuration testée** :
  ```kotlin
  ndkVersion = "26.1.10909125"
  ndk {
      debugSymbolLevel = "FULL"
  }
  ```
- **Résultat** : Configuration causait des problèmes avec le bundle
- **Solution finale** : Retrait de la configuration NDK. L'avertissement est ignoré car :
  - Les bibliothèques natives proviennent de dépendances AndroidX tierces
  - Elles sont distribuées sans symboles de débogage
  - Ce n'est pas bloquant pour la publication

#### Politique de confidentialité
- **Exigence** : Permission `READ_PHONE_STATE` nécessite une politique de confidentialité
- **Solution** : Création de `PRIVACY_POLICY.md` et publication sur GitHub
- **URL** : https://github.com/julien-gomy/StopDemarchage/blob/master/PRIVACY_POLICY.md

### Versions générées

| Version | versionCode | versionName | Notes |
|---------|-------------|-------------|-------|
| 1 | 1 | 1.0 | Première version uploadée |
| 2 | 2 | 1.0.1 | Tentative avec config NDK |
| 3 | 3 | 1.0.2 | Version finale sans config NDK |

### Fichiers de sortie

- **APK Release** : `app/build/outputs/apk/release/app-release.apk` (~2.6 MB)
- **App Bundle** : `app/build/outputs/bundle/release/app-release.aab` (~3.4 MB)
- **Mapping R8** : `app/build/outputs/mapping/release/mapping.txt`

### Dépôt GitHub
- **URL** : https://github.com/julien-gomy/StopDemarchage
- **Créé le** : 5 février 2026
- **Visibilité** : Public

### Commandes utiles

```bash
# Build debug
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Build App Bundle
./gradlew bundleRelease

# Clean build
./gradlew clean bundleRelease

# Simuler un appel (via ADB)
adb shell am start -a android.intent.action.CALL -d tel:+33162123456
```

### Configuration finale build.gradle.kts

```kotlin
android {
    namespace = "com.stopdemarchage"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stopdemarchage"
        minSdk = 31
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.2"
    }

    signingConfigs {
        create("release") {
            storeFile = file("release-keystore.jks")
            storePassword = "stopdemarchage"
            keyAlias = "stopdemarchage"
            keyPassword = "stopdemarchage"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### Leçons apprises

1. **CallScreeningService** doit répondre de manière synchrone - ne pas utiliser de coroutines asynchrones
2. **Les symboles natifs** des dépendances tierces ne peuvent pas être inclus - l'avertissement peut être ignoré
3. **La configuration NDK** sans NDK installé peut corrompre le bundle
4. **Toujours tester** le bundle généré avant de modifier la configuration
