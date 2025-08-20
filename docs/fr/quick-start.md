# 🚀 Comment démarrer

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
	<version>0.0.25-SNAPSHOT</version>
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