# Openapi maven plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kbuntrock/openapi-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.kbuntrock%22%20AND%20a:%22openapi-maven-plugin%22)
[![CircleCI](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev.svg?style=shield)](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev)
![GitHub](https://img.shields.io/github/license/kbuntrock/openapi-maven-plugin?color=blue)

The openapi maven plugin analyse Rest controller classes at compile time and generate the corresponding openapi 3.0.3 documentation.

It supports SpringMVC annotations.

Doing this process during the building phase of a project has several advantages compared to some other methods:
- the source code of the project can be parsed to extract javadoc comments and enrich the generated documentation. There is no need of a third party annotation library for the sole purpose of keeping comments available at runtime. Your code stay pure and you don't duplicate informations.
- no extra dependency in your jar/war. Meaning less attack surface / no need to monitor another library for vulnerabilities on your app
- Implementating interfaces or abstract classes is not a requirement. The documentation can be generated from a module referencing only interfaces which will be later in the build chain implemented in one or several other modules.
- usually faster than launching an app / running the integration test phase

# How to use it

Import the plugin in your project by adding following configuration in your `plugins` block:

```xml
<!-- This section tells the maven-compiler-plugin to keep the parameters names. Elsewhere, all our parameters will be names "arg0, arg1, ...". -->
<plugin>
	<artifactId>maven-compiler-plugin</artifactId>
	<version>${maven-compiler-plugin.version}</version>
	<configuration>
		<compilerArgs>
			<arg>-parameters</arg>
		</compilerArgs>
	</configuration>
</plugin>
<!-- Plugin declaration -->
<plugin>
	<groupId>io.github.kbuntrock</groupId>
	<artifactId>openapi-maven-plugin</artifactId>
	<version>0.0.7</version>
	<executions>
		<execution>
			<id>documentation</id>
			<goals>
				<goal>documentation</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<apiConfiguration>
			<tagAnnotations>
				<!-- RestController is the default value but it can be replaced by RequestMapping -->
				<annotation>RestController</annotation>
			</tagAnnotations>
		</apiConfiguration>
		<javadocConfiguration>
			<scanLocations>src/main/java</scanLocations>
		</javadocConfiguration>
		<apis>
			<api>
				<locations>
					<location>io.github.kbuntrock.sample.endpoint</location>
				</locations>
			</api>
		</apis>
	</configuration>
</plugin>
```

The `execution` phase cannot be set before 'compile'.

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
| `attachArtifact` | If enabled, the generated documentation will be attached as a maven artifact. The filename is used as a classifier. Default is true. |
| `defaultProduceConsumeGuessing` | If a produce or consume value is not defined, try to guess a value depending of the parameter/return type. Default is false. |
| `springPathEnhancement` | Apply the spring enhancement to path value between a class @RequestMapping and a method @RequestMapping : add a "/" between the two values if there is none and add a "/" at the beginning of the operation path if there is none . Default is true. |

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



# Thank-you section

Many thanks to : 

Karl Heinz Marbaise and his work on the amazing "maven-it-extension" plugin (+ really well documented) : https://github.com/khmarbaise/maven-it-extension

- The "java parser" team, which made the comments parsing so easy! 
https://github.com/javaparser/javaparser

-Ronmamo and his "Reflections" library which helped numerous projects : 
https://github.com/ronmamo/reflections