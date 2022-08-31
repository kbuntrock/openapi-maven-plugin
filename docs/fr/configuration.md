# Configuration

## outputDirectory

- Type : `string`
- Valeur par défaut : `${project.build.directory}`

Le répertoire de génération des documents openapi.

```xml
<configuration>
	<outputDirectory>${project.build.directory}/somewhere</outputDirectory>
</configuration>
```

## apiConfiguration

- Type : `balise de section`
- Présence : `optionelle`

Configurations générales, qui seront appliquées à chaque document généré. La pluspart des configurations déclarées dans cette section peuvent être ensuite surchargées pour chaque document.

```xml
<configuration>
	<apiConfiguration>
		[...]
	</apiConfiguration>
</configuration>
```


### attachArtifact

- Type : `boolean`
- Valeur par défaut : `true`

Attache la documentation générée en tant qu'artefact maven lors de la phase "install"

```xml
<attachArtifact>false</attachArtifact>
```

?> Le nom du fichier généré sera utilisé en tant que classifier. Exemple : 'mon-projet-0.35.0-SNAPSHOT-spec-open-api.yml'

### defaultSuccessfulOperationDescription

- Type : `string`
- Valeur par défaut : `successful operation`

Inscrit une description par défaut sur les retours code HTTP 200 lorsqu'aucune javadoc n'est trouvée.

```xml
<defaultSuccessfulOperationDescription>Opération réalisée avec succès</defaultSuccessfulOperationDescription>
```

### springPathEnhancement

- Type : `boolean`
- Valeur par défaut : `true`

Applique une correction de chemin (réalisée par spring) entre les classes annotées par @RequestMapping et les méthodes annotées par @RequestMapping.
Ajoute un "/" entre les deux valeurs si il n'y en a pas.
Ajoute un "/" au début du chemin si il n'y en a pas

```xml
<springPathEnhancement>false</springPathEnhancement>
```

### operationId

- Type : `string`
- Valeur par défaut : `{class_name}.{method_name}`

Manière de générer l'id d'opération pour un endpoint (la spécification openapi indique qu'un operationId doit être unique).
Des "places holders" permettent d'insérer les valeurs trouvées dans le code du projet : 
* `{class_name}` : nom de la classe java
* `{tag_name}` : nom du tag du endpoint (généré à partir du nom de la classe, voir doc correspondante)
* `{method_name}` : nom de la méthode java

```xml
<operationId>{tag_name}-{method_name}</operationId>
```

### defaultProduceConsumeGuessing

- Type : `boolean`
- Valeur par défaut : `true`

Indique qu'une valeur par défaut sera renseignée si aucune indication sur le type mime produit n'est renseigné.
Cette valeur sera :
* `text/plain` : si l'objet de retour openapi est de type `string`
* `application/json` : pour les autres cas

```xml
<defaultProduceConsumeGuessing>false</defaultProduceConsumeGuessing>
```

## javadocConfiguration

- Type: `balise de section`
- Présence : `optionelle`

Configurations liées à la javadoc (extraction et interprétation)

### scanLocations

- Type : `string list`
- Valeur par défaut : `aucune`

Renseigne les chemins relatifs à la racine du projet pour lesquels il faudra scanner le code source.


```xml
<scanLocations>
	<location>src/main/java</location>
	<location>src/main/javagen</location>
	<location>../security-commons/src/main/java</location>
</scanLocations>
```

## apis

- Type: `balise de section`
- Présence: `obligatoire`

Configurations spécifiques à chaque document généré. Doit contenir au moins un élément.

```xml
<apis>
	<api>
		<locations>
			<location>io.github.kbuntrock.sample.enpoint</location>
		</locations>
		<attachArtifact>true</attachArtifact>
		<defaultProduceConsumeGuessing>true</defaultProduceConsumeGuessing>
	</api>
	<api>
		<tagAnnotations>
			<annotation>RestController</annotation>
		</tagAnnotations>
		<locations>
			<location>io.github.kbuntrock.sample.implementation</location>
		</locations>
		<tag>
			<substitutions>
				<sub>
					<regex>Impl$</regex>
					<substitute></substitute>
				</sub>
			</substitutions>
		</tag>
		<attachArtifact>true</attachArtifact>
		<defaultProduceConsumeGuessing>true</defaultProduceConsumeGuessing>
		<filename>spec-open-api-impl</filename>
	</api>
</apis>
```

### locations

- Type : `string list`
- Valeur par défaut : `aucune`

Renseigne les packages ou noms de classes complets à documenter.


```xml
<locations>
	<location>io.github.myproject.webservices</location>
	<location>io.github.otherlibrary.webservices</location>
	<location>io.github.myproject.supervision.Autotest.java</location>
</locations>
```

### filename

- Type : `string`
- Valeur par défaut : `spec-open-api`

Le nom du fichier de documentation généré, sans l'extension.


```xml
<filename>mon-fichier</filename>
```