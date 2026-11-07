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
|  | `mediaType` | `String` | Media type of the content (defaults to the endpoint `produces` formats) |
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
| `SecurityScheme` | `name` | `String` | Security Scheme name |
|  | `type` | `SecuritySchemeType` | Type of the security scheme (http, apiKey, oauth2, openIdConnect) |
|  | `description` | `String` | Description of the scheme |
|  | `in` | `SecuritySchemeIn` | Location of the API key (query, header, cookie) |
|  | `scheme` | `String` | HTTP Authorization scheme (e.g., bearer, basic) |
|  | `bearerFormat` | `String` | A hint to the client to identify how the bearer token is formatted |
|  | `openIdConnectUrl` | `String` | OpenId Connect URL to discover OAuth2 configuration values |
| `SecurityRequirement`| `name` | `String` | Security Requirement name |
|  | `scopes` | `String[]` | Array of scopes required for the security requirement |

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
The `@SecurityScheme` annotation can also be placed on standalone configuration classes (such as Spring's `@Configuration` or custom security configuration classes), even if they do not contain any REST endpoints. The plugin will scan the classpath and automatically pick them up!
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

## Documenting a file download from a `void` method

An endpoint can return `void` and write the file directly into the `HttpServletResponse`. Since nothing in the
method signature describes the response body, the plugin documents a `200` response **without content** for it. It
does not guess anything from the `void` return type or from the `HttpServletResponse` parameter.

To document the body, declare it explicitly with `@ApiResponse` and `@Content`. A `byte[]` schema is documented as
`type: string`, `format: binary`:

```java
@ApiResponse(responseCode = "200", description = "The file content",
    content = @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = byte[].class)))
@GetMapping(value = "/files/{id}", produces = "application/octet-stream")
public void download(@PathVariable String id, HttpServletResponse response) throws IOException {
    // write the file into response.getOutputStream()
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

The media type of a documented response is resolved in this order:

1. the `mediaType` of the `@Content` annotation;
2. otherwise, the formats declared by the endpoint: `produces` of Spring's `@RequestMapping` (and its variants such as
   `@GetMapping`), or the JAX-RS / Jakarta REST `@Produces` annotation;
3. otherwise, the default behaviour: a media type guessed from the schema when `defaultProduceConsumeGuessing` is
   enabled, `*/*` if not.

:::info
Only the first `@Content` of an `@ApiResponse` is used; additional ones are ignored with a warning.
:::

:::tip
Alternatively, return a `ResponseEntity<Resource>` (Spring) instead of writing into the `HttpServletResponse`. The
response body is then part of the method signature and is documented without any Swagger annotation.
:::
