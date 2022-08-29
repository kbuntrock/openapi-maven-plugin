# Quick start

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