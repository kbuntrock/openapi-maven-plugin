# Comment démarrer

La première chose à faire est de configurer la compilation java de son projet afin de conserver le nom des paramètres de fonctions. 
Sans ce réglage, les noms des paramètres seraient de la forme "arg0", "arg1", ...

Pour ce faire, il suffit d'ajouter les lignes suivantes dans la section plugins de votre projet maven:

```xml
<plugin>
	<artifactId>maven-compiler-plugin</artifactId>
	<!-- Potentiellement à adapter pour rester sur la version déjà utilisée par votre projet -->
	<version>3.10.1</version>
	<configuration>
		<compilerArgs>
			<arg>-parameters</arg>
		</compilerArgs>
	</configuration>
</plugin>
```

A la suite, ajoutez dans votre pom.xml les lignes suivantes en les adaptant au besoin à l'aide des commentaires.

```xml

<!-- Déclaration du plugin -->
<plugin>
	<groupId>io.github.kbuntrock</groupId>
	<artifactId>openapi-maven-plugin</artifactId>
	<version>0.0.18</version>
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

Lancez maintenant une compilation (mvn compile). Votre documentation sera générée dans "target/spec-open-api.yml".
Si vous passez à la phase d'installation du projet, un artefact maven avec le classifier "spec-open-api" sera généré dans votre repository.