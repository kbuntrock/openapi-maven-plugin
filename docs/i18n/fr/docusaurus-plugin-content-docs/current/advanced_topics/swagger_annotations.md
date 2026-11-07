---
sidebar_position: 1
sidebar_label: Annotations Swagger
---

# 🔌 Support des annotations Swagger Core v3

## Annotations et champs supportés

Les annotations et champs actuellement pris en charge sont :

| Annotation | Field | Type | Description                                           |
|---|---|---|-------------------------------------------------------|
| `Operation` | `operationId` | `String` | Identifiant unique de l'opération                     |
|  | `summary` | `String` | Court résumé de l'opération                           |
|  | `description` | `String` | Description détaillée de l'opération                  |
|  | `parameters` | `Parameter[]` | Liste des paramètres                                  |
|  | `responses` | `ApiResponse[]` | Liste des réponses possibles                          |
| `ApiResponse` | `responseCode` | `String` | Code http de la réponse                               |
|  | `description` | `String` | Description de la réponse                             |
|  | `content` | `Content[]` | Définition du contenu de la réponse                   |
| `Content` | `schema` | `Schema` | Schema du contenu                                     |
|  | `mediaType` | `String` | Media type du contenu (par défaut, les formats `produces` de l'endpoint) |
| `Schema` | `implementation` | `Class<?>` | Classe implémentant le shéma                          |
|  | `description` | `String` | Description du schema                                 |
|  | `example` | `String` | Valeur d'exemple                                      |
| `Parameters` | `value` | `Parameter[]` | Liste des paramètres                                  |
| `Parameter` | `name` | `String` | Nom du paramètre                                      |
|  | `in` | `ParameterIn` | Localisation du paramètre (query, path, header, etc.) |
|  | `description` | `String` | Description du paramètre                              |
|  | `required` | `boolean` | Indique si ce paramètre est requis                    |
|  | `schema` | `Schema` | Schéma du paramètre                                   |
|  | `example` | `String` | Valeur d'exemple                                      |
| `SecurityScheme` | `name` | `String` | Nom du schéma de sécurité                            |
|  | `type` | `SecuritySchemeType` | Type du schéma de sécurité (http, apiKey, oauth2, openIdConnect) |
|  | `description` | `String` | Description du schéma                                |
|  | `in` | `SecuritySchemeIn` | Localisation de la clé API (query, header, cookie)   |
|  | `scheme` | `String` | Schéma d'authentification HTTP (ex: bearer, basic)   |
|  | `bearerFormat` | `String` | Indication sur le format des tokens bearer            |
|  | `openIdConnectUrl` | `String` | URL OpenId Connect pour découvrir la configuration OAuth2 |
| `SecurityRequirement`| `name` | `String` | Nom de l'exigence de sécurité                        |
|  | `scopes` | `String[]` | Liste des scopes requis pour l'exigence de sécurité   |

## Exemples

```java
@Operation(summary = "Swagger summary",
        operationId = "my-operation-id",
        responses = {
                @ApiResponse(description = "This is a successful operation"),
                @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorEntity.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorEntity.class)))
        })
@GetMapping("/some-api")
public ResponseEntity<String> myFunction() {
  return ResponseEntity.ok("returnValue");
}
```
---
```java
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/recipes")
public class SecurityAnnotationResource {

	@GetMapping
	public String getRecipes() {
		return "Recipes";
	}

	@SecurityRequirement(name = "basicAuth")
	@GetMapping("/basic")
	public String getRecipesBasicAuth() {
		return "Recipes";
	}
}
```

:::info
L'annotation `@SecurityScheme` peut également être placée sur des classes de configuration standalone (comme `@Configuration` de Spring ou des classes de configuration de sécurité personnalisées), même si elles ne contiennent aucun point de terminaison REST. Le plugin va scanner le classpath et les récupérer automatiquement !
:::

---
```java
public class ErrorDto {
    
	@Schema(description = "Timestamp of the error", example = "2023-10-01T12:00:00")
	private LocalDateTime timestamp;
	@Schema(description = "Session ID associated with the error", example = "session-12345")
	private String sessionId;
}
```

## Documenter un téléchargement de fichier depuis une méthode `void`

Un endpoint peut retourner `void` et écrire le fichier directement dans la `HttpServletResponse`. Comme rien dans la
signature de la méthode ne décrit le corps de la réponse, le plugin documente une réponse `200` **sans contenu**. Il ne
déduit rien du type de retour `void` ni du paramètre `HttpServletResponse`.

Pour documenter le corps de la réponse, déclarez-le explicitement avec `@ApiResponse` et `@Content`. Un schéma
`byte[]` est documenté en `type: string`, `format: binary` :

```java
@ApiResponse(responseCode = "200", description = "The file content",
    content = @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = byte[].class)))
@GetMapping(value = "/files/{id}", produces = "application/octet-stream")
public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
    // écriture du fichier dans response.getOutputStream()
}
```

```yaml
responses:
  "200":
    description: The file content
    content:
      application/octet-stream:
        schema:
          type: string
          format: binary
```

Le media type d'une réponse documentée est déterminé dans cet ordre :

1. le `mediaType` de l'annotation `@Content` ;
2. à défaut, les formats déclarés par l'endpoint : `produces` du `@RequestMapping` de Spring (et de ses variantes comme
   `@GetMapping`), ou l'annotation `@Produces` de JAX-RS / Jakarta REST ;
3. sinon, le comportement par défaut : un media type déduit du schéma si `defaultProduceConsumeGuessing` est activé,
   `*/*` dans le cas contraire.

:::info
Seule la première annotation `@Content` d'une `@ApiResponse` est prise en compte ; les suivantes sont ignorées avec un
avertissement.
:::

:::tip
Alternativement, retournez une `ResponseEntity<Resource>` (Spring) au lieu d'écrire dans la `HttpServletResponse`. Le
corps de la réponse fait alors partie de la signature de la méthode et est documenté sans aucune annotation Swagger.
:::
