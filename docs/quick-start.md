# Quick start

First thing to do is to configure the java compilation process in order to keep method parameters names.
Without this, parameters would be named in the documentation "arg0", "arg1", ...

One should add the following lines in the plugin section of the maven project:

```xml
<plugin>
	<artifactId>maven-compiler-plugin</artifactId>
	<!-- Potentially adapt to stay on the version already used by your project -->
	<version>3.10.1</version>
	<configuration>
		<compilerArgs>
			<arg>-parameters</arg>
		</compilerArgs>
	</configuration>
</plugin>
```

Then, add the openapi-maven-plugin configuration and adapt the present values with the help of the detailed configuration section:

```xml

<!-- Plugin declaration -->
<plugin>
	<groupId>io.github.kbuntrock</groupId>
	<artifactId>openapi-maven-plugin</artifactId>
	<version>0.0.16</version>
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

Launch now a compilation (mvn compile). Your documentation will be generated in "target/spec-open-api.yml".
If you execute the install phase, a maven artifact with the classifier based on the filename will be installed in your repository.