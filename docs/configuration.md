# Configuration

## outputDirectory

- Type: `string`
- Default value: `${project.build.directory}`

Directory where the documents will be generated.

```xml
<configuration>
	<outputDirectory>${project.build.directory}/somewhere</outputDirectory>
</configuration>
```

## apiConfiguration

- Type: `configuration section`
- Required: `false`

General configuration section for documented APIs. Will be applied to each generated spec file. Most of the configuration done here can be overrided locally for a specific doc file.

```xml
<configuration>
	<apiConfiguration>
		[...]
	</apiConfiguration>
</configuration>
```

### library

- Type: `string`
- Default value: `SPRING_MVC`

Annotation library to use.

Values can be: 
* `SPRING_MVC`
* `JAVAX_RS` (value JAXRS is also accepted)
* `JAKARTA_RS`

```xml
<library>JAVAX_RS</library>
```

### tagAnnotations

!> Not relevant if a `JAVAX_RS` or `JAKARTA_RS` is used.

- Type: `string list`
- Default value: `RestController`

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

- Type: `string`
- Default value: `successful operation`

Default success description if no javadoc is found.

```xml
<defaultSuccessfulOperationDescription>Great success my friends!</defaultSuccessfulOperationDescription>
```

### defaultNonNullableFields

- Type: `boolean`
- Default value: `false`

If true, all fields without an explicit indication (annotation or optionally wrapped) are considered "required".

### nonNullableAnnotation

- Type : `string list`
- Required: `false`

List of annotations defining **required** fields. If present, override the following default values:
- jakarta.validation.constraints.NotNull
- jakarta.validation.constraints.NotBlank
- jakarta.validation.constraints.NotEmpty
- javax.validation.constraints.NotNull
- javax.validation.constraints.NotBlank
- javax.validation.constraints.NotEmpty

### nullableAnnotation

- Type : `string list`
- Required: `false`

List of annotations defining **non required** fields. If present, override the following default values:
- jakarta.annotation.Nullable
- javax.annotation.Nullable

### pathEnhancement

- Type: `boolean`
- Default value: `true`

Apply a path "fix" (done automatically by spring) between paths declared by the class and the method.
Add a "/" between two values if there is no separator. 
Add a "/" at the beginning of the path if there is no.

ex: "api" + "status" will give the following path : "/api/status".

```xml
<pathEnhancement>false</pathEnhancement>
```

### pathPrefix

- Type: `string`
- Default value: empty

Apply a prefix to all path urls (since it's possible for a webapp to prefix any url in a way not known by the plugin).

```xml
<pathPrefix>/v1</pathPrefix>
```

### operationId

- Type: `string`
- Default value: `{class_name}.{method_name}`

Way of generating the operation id for an endpoint (openapi specification tells this id must be unique in the document).
"Places holders" allows to use values found during the scan of the project:
* `{class_name}` : java class name
* `{tag_name}` : endpoint tag (see documentation about possible configuration on the class name to generate a custom tag name)
* `{method_name}` : java method name

!> Reminder : According to openapi specification, the operation id must be unique in the document.

```xml
<operationId>{tag_name}-{method_name}</operationId>
```

### defaultProduceConsumeGuessing

- Type: `boolean`
- Default value: `true`

Configure a default value which will be written in the return "mime type" section if no precise indication is given in the code.
Values will be :
* `text/plain` : if the openapi return type is a `string`
* `application/json` : for other return types

```xml
<defaultProduceConsumeGuessing>false</defaultProduceConsumeGuessing>
```

### loopbackOperationName

- Type : `boolean`
- Default value: `true`

If true, insert this extension attribute with the java method name (see https://loopback.io/doc/en/lb4/Decorators_openapi.html).

?> Some tools will use this value. For example, ng-openapi-gen (https://github.com/cyclosproject/ng-openapi-gen) will generate the typescript method names based on this value if it exists.

```xml
<loopbackOperationName>false</loopbackOperationName>
```

### whiteList

- Type: `string list`
- Required: `false`

Define a white list of classes / methods / both which are autorised to be documented.
Each entry is divided in 3 parts
- left : class regex
- separator : #, used only to split the left and right part
- right : method regex 

!> It is the class canonical name (with package) which is given to the regex pattern matcher.

It is not mandatory to fill both entry parts. See the following examples.

```xml
<whiteList>
	<entry>io.github.kbuntrock.AccountController#getCurrentUser</entry> <!-- Precise: only the method getCurrentUser in the AccountController class are authorized to be documented by this rule -->
	<entry>#.*Session$</entry> <!-- All methods ending with the word Session are authorized to be documented by this rule -->
	<entry>.*Monitoring.*</entry> <!-- All classes with the word "Monitoring" are authorized to be documented by this rule -->
</whiteList>
```

### blackList

- Type: `string list`
- Required: `false`

Define a black list of classes / methods / both which are autorised to be documented.
Each entry is divided in 3 parts
- left : class regex
- separator : #, used only to split the left and right part
- right : method regex 

!> It is the class canonical name (with package) which is given to the regex pattern matcher.

It is not mandatory to fill both entry parts. See the following examples.

```xml
<blackList>
	<entry>io.github.kbuntrock.AccountController#getCurrentUser</entry> <!-- Very precise. Only the method getCurrentUser in the AccountController class is banned from the documentation by this rule -->
	<entry>#.*Session$</entry> <!--  All methods ending with the word Session are banned from the documentation by this rule -->
	<entry>.*Monitoring.*</entry> <!-- All classes with the word "Monitoring" are banned from the documentation by this rule -->
</blackList>
```

### freeFields

- Type : `string`

Allow to add some documentation values which can not be filled by the project code scanning. Overriding the documentation name and it's version is possible. By default, it is the project name and version.
Adding extra elements in the "schemas" section is also possible.

!> It is possible to configure a json string, or a path to a json file (path is relative to the project base directory).

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

Add an error section to every operation generated in the documentation. Can reference elements declared in the "freeFields" configuration.

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
- Required: `false`

Section defining tag configuration. We will find there the following sub-sections

#### substitutions

Define a substitution list to adapt the tag name, based on the class name.

A substitution accept two parameters:
* `regex` : value to find (regex format)
* `substitute` : the replacement value

If several substitutions are configured, they will be applied sequentially.

Ex: The class name "IImportUserController" with the following configuration:

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

Will give the tag name : "Import User Webservice"

### extraSchemaClasses

- Type: `string list`
- Required: `false`

Define a list of java objects not referenced by a documented endpoint, but which will still be included in the schema section

```xml
<extraSchemaClasses>
	<class>io.github.kbuntrock.TechnicalExceptionDto</class>
	<class>io.github.kbuntrock.SecurityExceptionDto</class>
</extraSchemaClasses>
```

### customResponseTypeAnnotation

- Type : `string`
- Required : `false`

Canonical name of an annotation defined in your project. It will allow you to override the response type of a endpoint.
Useful for JaxRS or JakartaRS project using the "Response" class (this class can't be generically typed).

```xml
<customResponseTypeAnnotation>io.github.kbuntrock.resources.endpoint.jaxrs.ResponseType</customResponseTypeAnnotation>
```

This annotation have to implement a function "Class value()".

Example : 

```java
@Target(ElementType.METHOD)
public @interface ResponseType {

	Class value();

}
```

And then you can use it like this:

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
- Required : `false`

Relative path to a custom openapi model definitions yaml file.
Entries can override entries of the default file: https://github.com/kbuntrock/openapi-maven-plugin/blob/dev/openapi-maven-plugin/src/main/resources/openapi-model.yml

**Must be used in conjunction with modelsAssociations**

### modelsAssociations

- Type : `string`
- Required : `false`

Relative path to a custom yaml file defining the association between java class and openapi models.
Entries can override entries of the default file: https://github.com/kbuntrock/openapi-maven-plugin/blob/dev/openapi-maven-plugin/src/main/resources/model-association.yml

Entries are divided in two groups:
- equality: require class equality
- assignability: require class assignability (`isAssignableFrom(Class<?> cls)`)

## javadocConfiguration

- Type: `section`
- Required: `false`

Configuration linked to the code javadoc reading.

### scanLocations

- Type: `string list`
- Required: `false`

!> Must be configured if one want to have the javadoc read to fill the description value on endpoint / parameters / response / ...

Configure the relative paths (from the projet root) to folders to read. The most common value to configure is "src/main/java".


```xml
<scanLocations>
	<location>src/main/java</location>
	<location>src/main/javagen</location>
	<location>../security-commons/src/main/java</location>
</scanLocations>
```

### encoding

- Type : `string`
- Default value: `UTF-8`

Encoding of the source code.


```xml
<encoding>windows-1252</encoding>
```

## apis

- Type: `section`

Specific configuration for each generated openapi documentation.

!> At least one entry should be configured if you want this plugin to fulfill its purpose.

!> All parameters from the "apiConfiguration" section can be overriden here. Ex:

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

Package names or canonical classes names to document.

!> At least one entry should be configured if you want this plugin to fulfill its purpose.

```xml
<locations>
	<location>io.github.myproject.webservices</location>
	<location>io.github.otherlibrary.webservices</location>
	<location>io.github.myproject.supervision.Autotest</location>
</locations>
```

### filename

- Type : `string`
- Default value: `spec-open-api.yml`

File name of the generated documentation, with its extension.

```xml
<filename>my_file.yaml</filename>
```

### mergeFreeFields

- Type : `boolean`
- Default value : `false`

True to activate "free fields" merging between the common and the specific api configuration. Allow to keep commons values and to override / add other values.

```xml
<mergeFreeFields>true</mergeFreeFields>
```