# Openapi maven plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kbuntrock/openapi-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.kbuntrock%22%20AND%20a:%22openapi-maven-plugin%22)
[![CircleCI](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev.svg?style=shield)](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev)
![GitHub](https://img.shields.io/github/license/kbuntrock/openapi-maven-plugin?color=blue)

Le but de se plugin est de générer la documentation openapi de webservices REST à partir d'un projet Java utilisant les annotations Spring MVC, Javax RS ou Jakarta RS.

Il est compatible du jdk8 jusqu'à au moins jdk17 (les tests d'intégration sont passés sur un jdk8, jdk11 et jdk17).

Le plugin combine les informations obtenues via réflexion sur le code compilé et le parsing des fichiers sources pour en extraire les commentaires javadoc.

La philosophie de ce plugin est de créer la documentation durant la phase de build, sans lancer d'application. Cela a plusieurs avantages comparé à d'autres méthodes: 
- Le code source du projet peut être analysé afin d'en extraire la javadoc et d'enrichir la documentation. Il n'y a pas besoin d'alourdir le code avec des annotations dans le seul but de garder ces informations disponibles au "runtime". Votre code reste pur, sans duplication d'information.
- Pas de librairie supplémentaire packagée avec votre jar/war. Cela veut dire moins de surface d'attaque / pas besoin de garder dans le radar une dépendance à l'affut d'éventuelles failles de sécurité.
- L'implémentation de vos interfaces / classes abstraites déclarant une api REST n'est pas nécessaire. La documentation peut être générée à partir d'un module déclarant uniquement des interfaces. Cela autorise plus de souplesse dans l'architecture de votre application.
- Souvent bien plus rapide que de lancer une application / faire tourner la phase de tests d'intégration.

De nombreuses options de configuration sont disponibles afin d'apporter la plus grande souplesse possible.

On peut notamment mentionner :
- possibilité de générer plusieurs documents avec des configurations différentes
- un système de "white list", "black list" sur les classes et méthodes scannées
- ajouts possibles à la documentation générée (security, licence, ...)
- possibilité de définir un "loopback operation name", utilisé par certains outils de génération de code (voir https://loopback.io/doc/en/lb4/Decorators_openapi.html et https://github.com/cyclosproject/ng-openapi-gen).
...
