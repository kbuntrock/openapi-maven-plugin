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
- separator : double underscore, used only to split the left and right part ("__")
- right : method regex 

!> It is the class canonical name (with package) which is given to the regex pattern matcher.

It is not mandatory to fill both entry parts. See the following examples.

```xml
<whiteList>
	<entry>io.github.kbuntrock.AccountController__getCurrentUser<entry> <!-- Very precise. Only the method getCurrentUser in the AccountController class are authorized to be documented by this rule -->
	<entry>__.*Session$<entry> <!-- All methods ending with the word Session are authorized to be documented by this rule -->
	<entry>.*Monitoring.*<entry> <!-- All classes with the word "Monitoring" are authorized to be documented by this rule -->
</whiteList>
```

### blackList

- Type: `string list`
- Required: `false`

Define a black list of classes / methods / both which are autorised to be documented.
Each entry is divided in 3 parts
- left : class regex
- separator : double underscore, used only to split the left and right part ("__")
- right : method regex 

!> It is the class canonical name (with package) which is given to the regex pattern matcher.

It is not mandatory to fill both entry parts. See the following examples.

```xml
<blackList>
	<entry>io.github.kbuntrock.AccountController__getCurrentUser<entry> <!-- Very precise. Only the method getCurrentUser in the AccountController class is banned from the documentation by this rule -->
	<entry>__.*Session$<entry> <!--  All methods ending with the word Session are banned from the documentation by this rule -->
	<entry>.*Monitoring.*<entry> <!-- All classes with the word "Monitoring" are banned from the documentation by this rule -->
</blackList>
```

### freeFields

- Type : `json string`

Allow to add some documentation values which can not be filled by the project code scanning. Overroding the documentation name and it's version is possible. By default, it is the project name and version.

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

- Type: `section`
- Required: `false`

Define a substitution list to adapt the tag name, based on the class name.

A substitution accept two parameters:
* `regex` : value to find (regex format)
* `substitute` : the replacement value

If several substitutions are configured, they will be applied sequentially.

Ex: The class name "IImportController" with the following configuration:

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

Will give the tag name : "Import_ws"

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

!> At least one entry should be configured if you want this plugin to fulfil its purpose.

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

!> At least one entry should be configured if you want this plugin to fulfil its purpose.

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