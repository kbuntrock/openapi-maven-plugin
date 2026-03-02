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
public class ErrorDto {

  @Schema(description = "Timestamp of the error", example = "2023-10-01T12:00:00")
  private LocalDateTime timestamp;
  @Schema(description = "Session ID associated with the error", example = "session-12345")
  private String sessionId;
}
```