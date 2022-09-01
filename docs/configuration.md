# Configuration

## outputDirectory

- Type : `string`
- Default value : `${project.build.directory}`

Directory where the documents will be generated.

```xml
<configuration>
	<outputDirectory>${project.build.directory}/somewhere</outputDirectory>
</configuration>
```

## apiConfiguration

- Type : `configuration section`
- Required : `false`

General configuration section for documented APIs. Will be applied to each generated spec file. Most of the configuration done here can be overrided locally for a specific doc file.

```xml
<configuration>
	<apiConfiguration>
		[...]
	</apiConfiguration>
</configuration>
```

### tagAnnotations

- Type : `string list`
- Default value : `RestController`

Tells the plugin which annotations are declaring a REST controller class.

Values can be: 
* `RestController`
* `RequestMapping`

```xml
<tagAnnotations>
	<annotation>RequestMapping</annotation>
</tagAnnotations>
```

?> If several annotations are declared, a class only need one to be documented.

### attachArtifact

- Type : `boolean`
- Default value : `true`

Attach the generated document as a maven artifact during the "install" phase.

```xml
<attachArtifact>false</attachArtifact>
```

?> The generated filename will be used as classifier. Example : 'my-projet-0.35.0-SNAPSHOT-spec-open-api.yml'

### defaultSuccessfulOperationDescription

- Type : `string`
- Default value : `successful operation`

Default success description if no javadoc is found.

```xml
<defaultSuccessfulOperationDescription>Great success my friends!</defaultSuccessfulOperationDescription>
```

### springPathEnhancement

- Type : `boolean`
- Default value : `true`

Applique une correction de chemin (réalisée par spring) entre les classes annotées par @RequestMapping et les méthodes annotées par @RequestMapping.
Ajoute un "/" entre les deux valeurs si il n'y en a pas.
Ajoute un "/" au début du chemin si il n'y en a pas

```xml
<springPathEnhancement>false</springPathEnhancement>
```

### operationId

- Type : `string`
- Default value : `{class_name}.{method_name}`

Manière de générer l'id d'opération pour un endpoint (la spécification openapi indique qu'un operationId doit être unique).
Des "places holders" permettent d'insérer les valeurs trouvées dans le code du projet : 
* `{class_name}` : nom de la classe java
* `{tag_name}` : nom du tag du endpoint (généré à partir du nom de la classe, voir doc correspondante)
* `{method_name}` : nom de la méthode java

!> Rappel : selon la spécification openapi, l'operation id doit être unique dans le document.

```xml
<operationId>{tag_name}-{method_name}</operationId>
```

### defaultProduceConsumeGuessing

- Type : `boolean`
- Default value : `true`

Indique qu'une valeur par défaut sera renseignée si aucune indication sur le type mime produit n'est renseigné.
Cette valeur sera :
* `text/plain` : si l'objet de retour openapi est de type `string`
* `application/json` : pour les autres cas

```xml
<defaultProduceConsumeGuessing>false</defaultProduceConsumeGuessing>
```

### loopbackOperationName

- Type : `boolean`
- Default value : `true`

Si vrai, insert un attribut d'extension indiquant le nom de la méthode java (voir https://loopback.io/doc/en/lb4/Decorators_openapi.html).

?> Certains outils pourront utiliser cette valeur, comme par exemple ng-openapi-gen qui s'en servira pour le nom des méthodes typescript appelant le webservice.

```xml
<loopbackOperationName>false</loopbackOperationName>
```

### freeFields

- Type : `json string`

Permet de renseigner des champs de la spécification qui ne peuvent pas être renseignés par le générateur. La surcharge du nom de la documentation et de sa version est également possible. Par défaut, il s'agît du nom de projet et de la version du projet.

```xml
<freeFields>
{
  "info": {
    "title": "This is a title",
    "description": "This is a sample server.",
    "termsOfService": "http://example.com/terms/",
    "contact": {
      "name": "API Support",
      "url": "http://www.example.com/support",
      "email": "support@example.com"
    },
    "license": {
      "name": "Apache 2.0",
      "url": "https://www.apache.org/licenses/LICENSE-2.0.html"
    }
  },
  "servers": [
    {
      "url": "https://development.test.com/v1",
      "description": "Development server"
    }
  ],
  "security": [
    {
      "jwt": []
    }
  ],
  "externalDocs": {
    "description": "Find more info here",
    "url": "https://example.com"
  },
  "components": {
    "securitySchemes": {
      "jwt": {
        "type": "http",
        "scheme": "bearer",
        "bearerFormat": "JWT"
      }
    }
  }
}
</freeFields>
```

### tag

- Type : `section`

Permet de définir une liste de substitutions afin d'adapter le nom d'un tag à partir du nom de la classe rencontrée.

Une substitution accepte deux paramètres :
* `regex` : la valeur à rechercher (au format regex)
* `substitute` : la valeur à remplacer

Si plusieurs substitutions sont renseignées, elles seront appliquées séquentiellement.

Exemple : Le nom de classe IImportController et la configuration suivante : 

```xml
<substitutions>
	<sub>
		<regex>^I</regex>
		<substitute></substitute>
	</sub>
	<sub>
		<regex>Controller$</regex>
		<substitute>_ws</substitute>
	</sub>
</substitutions>
```

Génèrera le tag suivant : Import_ws

## javadocConfiguration

- Type: `configuration section`
- Required : `false`

Configurations liées à la javadoc (extraction et interprétation)

### scanLocations

- Type : `string list`
- Default value : `none`

Renseigne les chemins relatifs à la racine du projet pour lesquels il faudra scanner le code source.


```xml
<scanLocations>
	<location>src/main/java</location>
	<location>src/main/javagen</location>
	<location>../security-commons/src/main/java</location>
</scanLocations>
```

## apis

- Type: `configuration section`
- Required: `obligatoire`

Configurations spécifiques à chaque document généré. Doit contenir au moins un élément.

!> Tous les paramètres de la section "apiConfiguration" peuvent être re-définis par api, comme le présente l'exemple suivant :

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
- Default value : `none`

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
- Default value : `spec-open-api`

Le nom du fichier de documentation généré, sans l'extension.


```xml
<filename>mon-fichier</filename>
```
