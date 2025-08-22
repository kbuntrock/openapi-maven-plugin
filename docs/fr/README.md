# OpenAPI Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kbuntrock/openapi-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.kbuntrock/openapi-maven-plugin)
[![CircleCI](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev.svg?style=shield)](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev)
![Coverage](../badges/jacoco.svg)
![GitHub](https://img.shields.io/github/license/kbuntrock/openapi-maven-plugin?color=blue)

L'**OpenAPI Maven Plugin** analyse les classes de contrôleurs REST Java et génère la documentation **OpenAPI 3.0.3**.
Il est conçu pour s’intégrer de manière fluide au cycle de vie Maven, rendant la génération de documentation **automatisée, rapide et fiable**.

---
## ✨ Fonctionnalités principales

- **Large support d'annotations**  
  Compatible avec:
    - Spring MVC
    - Javax RS
    - Jakarta RS


- **Analyse hybride**  
  Utilise à la fois les **classes compilées** et le **code source** pour créer la documentation :
    - Extrait directement les commentaires de la Javadoc, sans annotations supplémentaires.
    - Évite les bibliothèques tierces au runtime, ajoutées uniquement pour conserver les commentaires.
    - Garde votre code **propre et sans dépendances inutiles**.


- **Léger & Sécurisé**
    - Aucune dépendance supplémentaire dans votre JAR/WAR.
    - Réduit la surface d’exposition aux vulnérabilités.


- **Rapide et flexible**
    - La documentation peut être générée à partir de modules ne contenant que des interfaces.
    - Fonctionne plus rapidement que les méthodes nécessitant le lancement de l’application ou l’exécution de tests d’intégration.


- **Large compatibilité de JDKs**  
  Vérifié avec JDK 8, 11, 17 et 21 (des tests d’intégration sont exécutés sur ces versions).

---
## ⚙️ Options de configuration

Le plugin propose de nombreuses options pour affiner la documentation générée :
- Générer **plusieurs documentations** avec des configurations différentes.
- Appliquer des **listes blanches / listes noires** sur les classes et méthodes scannées.
- Enrichir la documentation avec des métadonnées supplémentaires (ex : schémas de sécurité, licences).
- Définir des **"loopback operation names"**, utiles pour certains outils de génération de code (ex : ng-openapi-gen).
- Et bien plus encore...

--- 
## 📚 Documentation

La documentation complète est disponible en anglais et en français :  
👉 [Project Documentation](https://kbuntrock.github.io/openapi-maven-plugin)

## 🔌 Prise en charge des annotations Swagger Core v3

En plus de sa prise en charge native des annotations Spring MVC, Javax RS et Jakarta RS, le plugin offre également une prise en charge partielle des annotations Swagger Core v3 (``io.swagger.core.v3/swagger-annotations``).  
Bien que toutes les annotations ne soient pas prises en charge, un sous-ensemble des plus couramment utilisées est reconnu. Offrant ainsi une flexibilité supplémentaire aux équipes qui s’appuient déjà sur ``swagger-annotations`` dans leur base de code.
La prise en charge détaillée est décrite dans la documentation.

---
## ✅ Pourquoi ce plugin?

- **Développement rigoureux** : les tests automatisés, sur plusieurs JDKs, garantissent une stabilité à long terme.
- **Accent mis sur la maintenabilité** : élimine les dépendances redondantes et la duplication d’informations.
- **Gain de productivité** : la génération de documentation est automatisée pendant la phase de build, sans étapes manuelles.

---
## 🤝 Contribuer

Les contributions sont les bienvenues !  
Merci de consulter notre documentation [CONTRIBUTING.md](https://github.com/kbuntrock/openapi-maven-plugin/blob/dev/CONTRIBUTING.md)

---
## 📜 License

Ce projet est distribué sous la [licence MIT](https://github.com/kbuntrock/openapi-maven-plugin?tab=MIT-1-ov-file#readme)