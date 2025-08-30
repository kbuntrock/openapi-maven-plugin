---
sidebar_position: 2
sidebar_label: Quick start
---

# 🚀 Quick Start

This plugin can be used in two different ways, depending on your needs:

**Command-Line Mode**: The fastest way to have a first contact with this plugin is to invoke it directly from the command line.
This mode is ideal for quickly testing whether the plugin integrates correctly with your project.
However, it provides only limited configuration options. For full customization and smoother usage, we recommend configuring the plugin in your pom.xml.

**Pom.xml configuration**: Defining the plugin directly in your pom.xml is the most common and convenient approach for everyday use.
This method offers a complete set of configuration options, making it easier to adapt the plugin to your project’s needs.

---
## ⚡ Command Line

The quickest way to generate a documentation is to directly invoke the plugin via the command line:

```
mvn clean compile io.github.kbuntrock:openapi-maven-plugin:0.0.26-SNAPSHOT:documentation \
"-Dmaven.compiler.parameters=true" \
"-Dopenapi.library=SPRING_MVC" \
"-Dopenapi.tagAnnotations=RequestMapping" \
"-Dopenapi.locations=your.base.package"
```

🔎 Command Breakdown:
- ``clean compile "-Dmaven.compiler.parameters=true"``:  
cleans previous build outputs and recompiles the project with parameter names preserved (required for reflection).
- ``-Dopenapi.library=SPRING_MVC``:  
Specifies the framework you are using. Supported values:
  - ``SPRING_MVC`` (default)
  - ``JAKARTA_RS``
  - ``JAVAX_RS``
- ``-Dopenapi.locations=your.base.package``:  
Defines the packages to scan for REST endpoints.  
  - Required for performance reasons.
  - Multiple packages can be provided, separated by commas (e.g. -Dopenapi.locations=pkgone,pkgtwo).
- ``-Dopenapi.tagAnnotations=RequestMapping``:  
Specifies which annotation should be used to detect endpoints. This property can be omitted if you don't rely on Spring.  
Supported values:
  - ``RestController`` (default)
  - ``RequestMapping``

?> A detailed list of available parameters is documented [here](command_line.md)

!> The command line mode is ideal for quickly testing if the plugin integrates properly with your project.
However, it offers limited configuration options. For full customization and ease of use, it is recommended to configure the plugin directly in your ``pom.xml`` (see the next section).

---
## 🍏 Configure your pom.xml

The usual way to generate a documentation is to add the plugin to your pom.xml.
The plugin main goal is **documentation** and is bound to the compile phase by default. It cannot be executed earlier because a successfully compiled project is required.

To get started, you need to configure your Maven build so that Java method parameter names are preserved.  
Without this step, parameters in the generated documentation will appear as ``arg0``, ``arg1``, etc.

---
1. **Enable Parameter Names in Compilation**

Add the following configuration to the ``maven-compiler-plugin`` inside the ``<plugins>`` section of your ``pom.xml``:

```xml
<plugin>
	<artifactId>maven-compiler-plugin</artifactId>
	<!-- Potentially adapt to stay on the version already used by your project -->
	<version>3.14.0</version>
	<configuration>
		<compilerArgs>
			<arg>-parameters</arg>
		</compilerArgs>
	</configuration>
</plugin>
```

---
2. **Configure the OpenAPI Maven Plugin**

Next, add the openapi-maven-plugin and adjust the configuration values as needed (see the detailed configuration section):

```xml

<!-- Plugin declaration -->
<plugin>
	<groupId>io.github.kbuntrock</groupId>
	<artifactId>openapi-maven-plugin</artifactId>
	<version>0.0.26-SNAPSHOT</version>
	<executions>
		<execution>
			<id>documentation</id>
			<goals>
				<goal>documentation</goal>
			</goals>
		</execution>
	</executions>
	<configuration>
		<!-- This section defines the general configuration, which can be overriden for each generated document. -->
		<apiConfiguration>
			<library>SPRING_MVC</library> <!-- SPRING_MVC is the default value. Here this tag could be deleted. Other possible values are JAKARTA_RS and JAVAX_RS -->
			<tagAnnotations> <!-- Only useful if you use Spring MVC -->
				<!-- RestController is the default value, but can be replaced by RequestMapping -->
				<annotation>RestController</annotation>
			</tagAnnotations>
		</apiConfiguration>
		<!-- This section defines which folders contains the source code to be read to extract the javadoc. -->
		<javadocConfiguration>
			<scanLocations>
				<!-- Other 'location' tag can be added to reference javadoc in other modules. -->
				<!-- Path is relative to the project root path. -->
				<location>src/main/java</location>
			</scanLocations>
		</javadocConfiguration>
		<!-- This section defines a list of documentations to generate. In this exemple, only one is generated. -->
		<apis>
			<api>
				<locations>
					<!-- Replace here by a package relevant for your project. -->
					<location>io.github.kbuntrock.sample.endpoint</location>
				</locations>
			</api>
		</apis>
	</configuration>
</plugin>
```

---
3. **Generate Documentation**

Run the following command: ``mvn compile``  
The OpenAPI specification will be generated at: ``target/spec-open-api.yml``

If you execute the install phase: ``mvn install``  
The generated specification will also be installed in your local Maven repository as an artifact, with a classifier based on the filename.