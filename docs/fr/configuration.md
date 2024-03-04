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

### library

- Type : `string`
- Valeur par défaut : `SPRING_MVC`

Indique quelle librairie d'annotations sera utilisée pour détécter les webservices à documenter.

Les valeurs peuvent être: 
* `SPRING_MVC`
* `JAVAX_RS` (valeur JAXRS également autorisée)
* `JAKARTA_RS`

```xml
<library>JAVAX_RS</library>
```

### tagAnnotations

!> Non pertinent si `JAVAX_RS` ou `JAKARTA_RS` est utilisé.

- Type : `string list`
- Valeur par défaut : `RestController`

Indique quelles sont les annotations repérant les classes repérant un webservice.

Les valeurs peuvent être: 
* `RestController`
* `RequestMapping`

```xml
<tagAnnotations>
	<annotation>RequestMapping</annotation>
</tagAnnotations>
```

?> Si plusieurs annotations sont renseignées, il suffit qu'une seule annotation soit rencontrée sur la classe pour qu'elle soit scannée.

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

### defaultNonNullableFields

- Type: `boolean`
- Valeur par défaut: `false`

Si true, tous les champs sans indication explicite (annotation ou "Optional") sont considérés comme requis.

### nonNullableAnnotation

- Type : `string list`
- Présence : `optionelle`

Liste d'annotations définissant les champs **requis**. Si présente, écrase la liste par défaut suivante :
- jakarta.validation.constraints.NotNull
- jakarta.validation.constraints.NotBlank
- jakarta.validation.constraints.NotEmpty
- javax.validation.constraints.NotNull
- javax.validation.constraints.NotBlank
- javax.validation.constraints.NotEmpty

### nullableAnnotation

- Type : `string list`
- Présence : `optionelle`

Liste d'annotations définissant les champs **non requis**. Si présente, écrase la liste par défaut suivante :
- jakarta.annotation.Nullable
- javax.annotation.Nullable

### pathEnhancement

- Type : `boolean`
- Valeur par défaut : `true`

Applique une correction de chemin (réalisée par spring) entre les classes annotées par @RequestMapping et les méthodes annotées par @RequestMapping.
Ajoute un "/" entre les deux valeurs si il n'y en a pas.
Ajoute un "/" au début du chemin si il n'y en a pas

ex : "api" + "status" donneront le path suivant : "/api/status".

```xml
<pathEnhancement>false</pathEnhancement>
```

### pathPrefix

- Type : `string`
- Valeur par défaut : vide

Applique un préfixe aux urls (car il est possibe dans une application web de préfixer n'importe quelle url par un moyen inconnu du plugin).

```xml
<pathPrefix>/v1</pathPrefix>
```

### operationId

- Type : `string`
- Valeur par défaut : `{class_name}.{method_name}`

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
- Valeur par défaut : `true`

Indique qu'une valeur par défaut sera renseignée si aucune indication sur le type mime produit n'est renseigné.
Cette valeur sera :
* `text/plain` : si l'objet de retour openapi est de type `string`
* `application/json` : pour les autres cas

```xml
<defaultProduceConsumeGuessing>false</defaultProduceConsumeGuessing>
```

### loopbackOperationName

- Type : `boolean`
- Valeur par défaut : `true`

Si vrai, insert un attribut d'extension indiquant le nom de la méthode java (voir https://loopback.io/doc/en/lb4/Decorators_openapi.html).

?> Certains outils pourront utiliser cette valeur, comme par exemple ng-openapi-gen (https://github.com/cyclosproject/ng-openapi-gen) qui s'en servira pour le nom des méthodes typescript appelant le webservice.

```xml
<loopbackOperationName>false</loopbackOperationName>
```

### whiteList

- Type : `string list`
- Présence : `optionelle`

Définit une liste blanche de classes / méthodes / combinaison des deux qu'il est autorisé de documenter.
Chaque entrée est divisée en trois parties : 
- gauche : regex de classe
- séparateur : #, sert uniquement à séparer les parties
- droite : regex de méthode 

!> c'est le nom canonique (avec package) de la classe qui est soumis à la regex

Il n'est pas obligatoire de renseigner les deux parties en même temps. Voir les exemples suivants

```xml
<whiteList>
	<entry>io.github.kbuntrock.AccountController#getCurrentUser</entry> <!-- précis, seule la méthode getCurrentUser de la classe AccountController est autorisée par cette règle -->
	<entry>#.*Session$</entry> <!-- toutes les méthodes se terminant par le mot Session sont autorisées par cette règle -->
	<entry>.*Monitoring.*</entry> <!-- toutes les classes comprenant le mot "Monitoring" sont autorisées par cette règle -->
</whiteList>
```

### blackList

- Type : `string list`
- Présence : `optionelle`

Définit une liste noire de classes / méthodes / combinaison des deux qu'il n'est pas autorisé de documenter.
Chaque entrée est divisée en trois parties : 
- gauche : regex de classe
- séparateur : #, sert uniquement à séparer les parties
- droite : regex de méthode 

!> c'est le nom canonique (avec package) de la classe qui est soumis à la regex

Il n'est pas obligatoire de renseigner les deux parties en même temps. Voir les exemples suivants

```xml
<blackList>
	<entry>io.github.kbuntrock.AccountController#getCurrentUser</entry> <!-- précis, seule la méthode getCurrentUser de la classe AccountController est bannie par cette règle -->
	<entry>#.*Session$</entry> <!-- toutes les méthodes se terminant par le mot Session sont bannies par cette règle -->
	<entry>.*Monitoring.*</entry> <!-- toutes les classes comprenant le mot "Monitoring" sont bannies par cette règle -->
</blackList>
```

### freeFields

- Type : `string`

Permet de renseigner des champs de la spécification qui ne peuvent pas être renseignés par le générateur. La surcharge du nom de la documentation et de sa version est également possible. Par défaut, il s'agît du nom de projet et de la version du projet.
Ajouter des éléments à la section "schemas" est possible via cette configuration.

!> Il est possible de renseigner une string au format json, ou le chemin vers un fichier json (le chemin est relatif par rapport à la racine du projet).

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
	"responses": {
      "NotFound": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The specified resource was not found"
      },
      "Unauthorized": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "Unauthorized"
      }
    },
    "schemas": {
      "Error": {
        "description": "An error object",
        "properties": {
          "code": {
            "description": "A technical error code",
            "type": "string"
          },
          "message": {
            "description": "A human readable error message",
            "type": "string"
          }
        },
        "required": [
          "code",
          "message"
        ],
        "type": "object"
      }
    },
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

### defaultErrors

- Type : `json string`

Ajoute une section erreurs à chaque opération générée dans la documentation. Peut référencer des éléments déclarés dans le champs de configuration "freeFields".

```xml
<defaultErrors>
{
  "401": {
    "$ref": "#/components/responses/Unauthorized"
  },
  "404": {
    "$ref": "#/components/responses/NotFound"
  }
}
</defaultErrors>
```

### tag

- Type: `section`
- Présence: `optionnelle`

Section permettant de définir la configuration des tags. Doit englober les sous-sections qui suivent.

#### substitutions

Permet de définir une liste de substitutions afin d'adapter le nom d'un tag à partir du nom de la classe rencontrée.

Une substitution accepte deux paramètres :
* `regex` : la valeur à rechercher (au format regex)
* `substitute` : la valeur à remplacer

Si plusieurs substitutions sont renseignées, elles seront appliquées séquentiellement.

Exemple : Le nom de classe IImportUserController et la configuration suivante : 

```xml
<substitutions>
	<sub>
		<regex>^I</regex>
		<substitute></substitute>
	</sub>
	<sub>
		<regex>Controller$</regex>
		<substitute>Webservice</substitute>
	</sub>
	<sub>
		<!-- Split with a space each time an uppercase character is found -->
		<regex>(?&lt;!^)[A-Z]</regex>
		<substitute xml:space="preserve"> $0</substitute>
	</sub>
</substitutions>
```

Génèrera le tag suivant : Import User Webservice

### extraSchemaClasses

- Type : `string list`
- Présence : `optionelle`

Définie une liste d'objets (classe java) qui seront à inclure dans le schema, bien qu'ils ne soient pas explicitement référencés par un endpoint.

```xml
<extraSchemaClasses>
	<class>io.github.kbuntrock.TechnicalExceptionDto</class>
	<class>io.github.kbuntrock.SecurityExceptionDto</class>
</extraSchemaClasses>
```

### customResponseTypeAnnotation

- Type : `string`
- Présence : `optionelle`

Nom canonique d'une annotation définie dans votre projet qui permettra de surcharger le type de réponse envoyé par un endpoint.
Utile pour les projets JaxRS ou JakartaRS utilisant l'objet "Response", qui ne peut pas être génériquement typé.

```xml
<customResponseTypeAnnotation>io.github.kbuntrock.resources.endpoint.jaxrs.ResponseType</customResponseTypeAnnotation>
```

Cette annotation doit implémenter une fonction "Class value()".

Exemple : 

```java
@Target(ElementType.METHOD)
public @interface ResponseType {

	Class value();

}
```

Et l'utilisation se fera comme suit : 

```java
@ResponseType(AccountDto.class)
@GET
@Path("/getAccount")
public Response getAccount() {
	// ...
}
```

### openapiModels

- Type : `string`
- Présence : `optionelle`

Chemin relatif vers un fichier yaml définissant ou redéfinissant des "modèles openapi".
Les entrées peuvent surcharger les entrées du fichier par défaut : https://github.com/kbuntrock/openapi-maven-plugin/blob/dev/openapi-maven-plugin/src/main/resources/openapi-model.yml

**Doit être utilisé avec le champs modelsAssociations**

### modelsAssociations

- Type : `string`
- Présence : `optionelle`

Chemin relatif vers un fichier yaml définissant les associations entre les classes java et les modèles openapi.
Les entrées peuvent surcharger les entrées du fichier par défaut : https://github.com/kbuntrock/openapi-maven-plugin/blob/dev/openapi-maven-plugin/src/main/resources/model-association.yml

Les entrées sont divisées en deux groupes :
- equality: Demande l'égalité de classe
- assignability: Demande "l'assignabilité" de classe (via `isAssignableFrom(Class<?> cls)`)

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

### encoding

- Type : `string`
- Valeur par défaut : `UTF-8`

Indique l'encodage des fichiers de code source dont la javadoc sera extraite.


```xml
<encoding>windows-1252</encoding>
```

## apis

- Type: `balise de section`
- Présence: `obligatoire`

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
- Valeur par défaut : `aucune`

Renseigne les packages ou noms de classes complets à documenter.


```xml
<locations>
	<location>io.github.myproject.webservices</location>
	<location>io.github.otherlibrary.webservices</location>
	<location>io.github.myproject.supervision.Autotest</location>
</locations>
```

### filename

- Type : `string`
- Valeur par défaut : `spec-open-api`

Le nom du fichier de documentation généré, sans l'extension.


```xml
<filename>mon-fichier</filename>
```

### mergeFreeFields

- Type : `boolean`
- Valeur par défaut : `false`

True pour activer la fusion du champs "free fields" entre la section commune et la section spécifique de la configuration des apis. Permet d'avoir des valeurs communes tout en en écrasant / ajoutant d'autres.

```xml
<mergeFreeFields>true</mergeFreeFields>
```