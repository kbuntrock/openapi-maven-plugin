---
sidebar_position: 1
sidebar_label: Comment démarrer
---

# 🚀 Comment démarrer

Ce plugin peut être utilisé de deux manières différentes, selon vos besoins :

**Mode ligne de commande** : La façon la plus rapide de découvrir ce plugin est de l’invoquer directement depuis la ligne de commande.  
Ce mode est idéal pour tester rapidement si le plugin s’intègre correctement à votre projet.
Il n’offre cependant que des possibilités limitées de configuration.
Pour une personnalisation complète et une utilisation plus fluide, il est recommandé de configurer le plugin directement dans votre ``pom.xml``.

**Configuration via le pom.xml** : Définir le plugin directement dans votre pom.xml est l’approche la plus courante et la plus pratique pour une utilisation quotidienne.
Cette méthode offre un ensemble complet d’options de configuration, ce qui facilite l’adaptation du plugin aux besoins de votre projet.

---
## ⚡ Ligne de commande

La façon la plus rapide de générer une documentation est d’invoquer directement le plugin via la ligne de commande :

```
mvn clean compile io.github.kbuntrock:openapi-maven-plugin:0.0.26-SNAPSHOT:documentation \
"-Dmaven.compiler.parameters=true" \
"-Dopenapi.library=SPRING_MVC" \
"-Dopenapi.tagAnnotations=RequestMapping" \
"-Dopenapi.locations=your.base.package"
```

🔎 Décomposition de la commande :
- ``clean compile "-Dmaven.compiler.parameters=true"``:  
  Nettoie les sorties de build précédentes et recompile le projet en conservant les noms des paramètres (nécessaire pour la réflexion).
- ``-Dopenapi.library=SPRING_MVC``:  
  Spécifie le framework que vous utilisez. Valeurs possibles :
  - ``SPRING_MVC`` (par défaut)
  - ``JAKARTA_RS``
  - ``JAVAX_RS``
- ``-Dopenapi.locations=your.base.package``:  
  Définit les packages à scanner pour détecter les endpoints REST.
  - Obligatoire pour des raisons de performance.
  - Plusieurs packages peuvent être fournis, séparés par des virgules (ex. -Dopenapi.locations=pkgone,pkgtwo).
- ``-Dopenapi.tagAnnotations=RequestMapping``:  
  Spécifie quelle annotation doit être utilisée pour détecter les endpoints. Cette propriété peut être omise si vous n’utilisez pas Spring.  
  Valeurs possibles :
  - ``RestController`` (par défaut)
  - ``RequestMapping``

:::tip
Une liste détaillée des paramètres disponibles est documentée [ici](command_line.md)
:::

:::warning
Le mode ligne de commande est idéal pour tester rapidement si le plugin s’intègre correctement à votre projet.
Cependant, il offre des possibilités de configuration limitées. Pour une personnalisation complète et un usage simplifié, il est recommandé de configurer le plugin directement dans votre ``pom.xml`` (voir la section suivante).
:::

---
## 🍏 Configuration de votre pom.xml

Pour commencer, il est nécessaire de configurer votre build Maven afin de conserver les noms des paramètres des méthodes Java.  
Sans cette étape, les paramètres apparaîtront dans la documentation sous forme de ``arg0``, ``arg1``, etc.

---
1. **Activer la conservation des noms de paramètres lors de la compilation**

Ajoutez la configuration suivante au plugin ``maven-compiler-plugin`` dans la section ``<plugins>`` de votre:

```xml
<plugin>
  <artifactId>maven-compiler-plugin</artifactId>
  <!-- Potentiellement à adapter pour rester sur la version déjà utilisée par votre projet -->
  <version>3.14.0</version>
  <configuration>
    <compilerArgs>
      <arg>-parameters</arg>
    </compilerArgs>
  </configuration>
</plugin>
```

---
2. **Configurer l'OpenAPI Maven Plugin**

Ensuite, ajoutez le plugin **openapi-maven-plugin** à votre ``pom.xml`` et adaptez la configuration en fonction de vos besoins (consultez la documentation détaillée pour voir toutes les options disponibles):


```xml

<!-- Déclaration du plugin -->
<plugin>
  <groupId>io.github.kbuntrock</groupId>
  <artifactId>openapi-maven-plugin</artifactId>
  <version>0.0.26-SNAPSHOT</version>
  <executions>
    <execution>
      <id>documentation</id>
      <goals>
        <goal>documentation</goal>
      </goals>
    </execution>
  </executions>
  <configuration>
    <!-- Cette section défini des configurations 'générales', qui peuvent être surchargées pour chaque document généré. -->
    <apiConfiguration>
      <library>SPRING_MVC</library> <!-- Valeur par défaut, la balise peut être supprimée en l'état -->
      <tagAnnotations> <!-- Balise uniquement utile pour Spring MVC -->
        <!-- RestController est la valeur par défaut mais peut être remplacée par RequestMapping -->
        <annotation>RestController</annotation>
      </tagAnnotations>
    </apiConfiguration>
    <!-- Cette section indique quels sont les répertoires dans lesquelles les fichiers de code sources devront être lus afin d'en extraire la javadoc -->
    <javadocConfiguration>
      <scanLocations>
        <!-- D'autres balises 'location' peuvent être ajoutées afin de référencer de la javadoc présente dans d'autres modules. -->
        <!-- Le chemin est relatif au répertoire du projet. -->
        <location>src/main/java</location>
      </scanLocations>
    </javadocConfiguration>
    <!-- Cette section définie enfin une liste de documents à générer. Dans cet exemple, un seul est généré, avec la configuration par défaut. -->
    <apis>
      <api>
        <!-- Pour chaque api (= document) généré, on indique quels sont les packages / noms de classe complets à scanner -->
        <locations>
          <!-- Remplacer ici le nom de package pour correspondre à votre projet. -->
          <location>io.github.kbuntrock.sample.endpoint</location>
        </locations>
      </api>
    </apis>
  </configuration>
</plugin>
```

---
3. **Générer la documentation**

Exécutez la commande suivante : ``mvn compile``  
La spécification OpenAPI sera générée dans le fichier : ``target/spec-open-api.yml``

Si vous lancez ensuite la phase **install** : ``mvn install``  
La spécification générée sera également installée dans votre dépôt Maven local comme un artefact, avec un classifier basé sur le nom de fichier.