---
sidebar_position: 1
sidebar_label: Swagger annotations
---

# 🔌 Swagger Core v3 annotations supports

## Supported annotations and fields

Supported annotations and fields currently are:

| Annotation | Field | Type | Description |
|---|---|---|---|
| `Operation` | `operationId` | `String` | Unique identifier of the operation |
|  | `summary` | `String` | Short summary of the operation |
|  | `description` | `String` | Detailed description of the operation |
|  | `parameters` | `Parameter[]` | List of operation parameters |
|  | `responses` | `ApiResponse[]` | List of possible responses |
| `ApiResponse` | `responseCode` | `String` | HTTP response code |
|  | `description` | `String` | Description of the response |
|  | `content` | `Content[]` | Response content definitions |
| `Content` | `schema` | `Schema` | Schema of the content |
| `Schema` | `implementation` | `Class<?>` | Implementation class of the schema |
|  | `description` | `String` | Description of the schema |
|  | `example` | `String` | Example value |
| `Parameters` | `value` | `Parameter[]` | List of parameters |
| `Parameter` | `name` | `String` | Parameter name |
|  | `in` | `ParameterIn` | Parameter location (query, path, header, etc.) |
|  | `description` | `String` | Parameter description |
|  | `required` | `boolean` | Whether the parameter is required |
|  | `schema` | `Schema` | Parameter schema |
|  | `example` | `String` | Example value |

## Examples

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