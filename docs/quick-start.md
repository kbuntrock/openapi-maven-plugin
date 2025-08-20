# 🚀 Quick Start

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
	<version>0.0.25-SNAPSHOT</version>
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
			<library>SPRING_MVC</library> <!-- Default value, here this tag could be deleted. -->
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