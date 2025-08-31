---
sidebar_position: 5
sidebar_label: Annotations Swagger
---

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
  - description (``String``)
  - example (``String``)

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