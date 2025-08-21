# 🔌 Support des annotations Swagger Core v3

## Annotations et champs supportés

Les annotations et champs actuellement pris en charge sont :
- io.swagger.v3.oas.annotations.Operation
  - operationId (``String``)
  - summary (``String``)
  - description (``String``)
  - responses (``io.swagger.v3.oas.annotations.responses.ApiResponse[]``)
- io.swagger.v3.oas.annotations.responses.ApiResponse
  - responseCode (``String``)
  - description (``String``)
  - content (``io.swagger.v3.oas.annotations.media.Content[]``)
- io.swagger.v3.oas.annotations.media.Content
  - schema (``io.swagger.v3.oas.annotations.media.Schema``)
- io.swagger.v3.oas.annotations.media.Schema
  - implementation (``Class<?>``)

## Exemple

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