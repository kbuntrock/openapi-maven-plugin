# openapi-maven-plugin
Convert annotated SpringMVC classes to an openapi 3.0.3 specification

# How to use it

Import the plugin in your project by adding following configuration in your `plugins` block:

```xml
<build>
  <plugins>
    <plugin>
	  <groupId>com.github.kbuntrock</groupId>
	  <artifactId>openapi-maven-plugin</artifactId>
	  <version>${openapi-maven-plugin.version}</version>
	  <executions>
	    <execution>
		  <id>documentation</id>
		  <goals>
		    <goal>documentation</goal>
		  </goals>
		</execution>
	  </executions>
	  <configuration>
	    <apis>
		  <api>
		    <locations>
			  <location>my.rest.controller.package</location>
			</locations>
			<attachArtifact>true</attachArtifact>
		  </api>
		</apis>
	  </configuration>
	</plugin>
  </plugins>
</build>
```

The `executions` phase cannot be set before 'compile'.

# Configuration for `configuration`

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `outputDirectory` | Set the output directory for generated files. Default is `${project.build.directory}` |
| `apis` **required** | List of `api` elements. One api element will generate one documentation file. At least one element is required. |

# Configuration for `api`

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `locations` **required**| Classes representing a REST controller (via ```@RequestMapping``` and children annotations) or packages containing those classes can be configured here. Each item must be located inside a <location> tag. |
| `filename` | Generated filename of the documentation. **Must not** contain the file extention. Default is `openapi` |
| `tag` | Configuration of a tag element in the generated document. |
| `operation` | Configuration of a operation element in the generated document. |
| `attachArtifact` | If enabled, the generated documentation will be attached as a maven artifact. The filename is used as a classifier. Default is false. |

# Configuration for `tag`

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `substitutions` | List of `substitution` elements. A tag is based on the java class name. A substitution allows to modify the name of the tag. See the substition section |

# Configuration for `operation`

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `substitutions` | List of `substitution` elements. A operation is based on the java method name. A substitution allows to modify the id of the operation. See the substition section |

# Configuration for `substitution`

Substitutions can be chained. The order of execution is the one defined in the configuration.

| **name** | **description** |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `regex` **required** | A java regex to find for replacement |
| `substitute` | The string to substitute. Default is an empty string. |
| `type` | Only avalaible for operations. If set, limit the substitution to a precise type of operation (get, post, delete, ...) |