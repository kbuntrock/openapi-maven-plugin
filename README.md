# Openapi maven plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kbuntrock/openapi-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.kbuntrock/openapi-maven-plugin)
[![CircleCI](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev.svg?style=shield)](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev)
![GitHub](https://img.shields.io/github/license/kbuntrock/openapi-maven-plugin?color=blue)

The openapi maven plugin analyse Rest controller java classes and generate the corresponding openapi 3.0.3 documentation. It supports Spring MVC, Javax RS and Jakarta RS annotations.

It is compatible from jdk8 to at least jdk21 (IT tests are run on jdk 8, 11, 17 and 21).

The plugin generate the documentation using the compiled classes + the source code of the project.

Doing this process during the building phase of a project has several advantages compared to other methods:
- the source code of the project can be parsed to extract javadoc comments and enrich the generated documentation. There is no need of a third party annotation library for the sole purpose of keeping comments available at runtime. Your code stay pure and you don't duplicate information.
- no extra dependency in your jar/war. No need to monitor another library for vulnerabilities.
- implementating interfaces or abstract classes is not a requirement. The documentation can be generated from a module referencing only interfaces; which will be later in the build chain implemented in one or several other modules.
- usually faster than launching an app / running the integration test phase

Numerous configuration options are available.

One can mention : 
- possibility to generate several documentations with different configurations
- "white list" / "black list" mecanism on scanned classes / methods
- addition of infos not found in the code (security, licence, ...)
- defining a "loopback operation name", used by some code generation tools (see [here](https://loopback.io/doc/en/lb4/Decorators_openapi.html) and [here](https://github.com/cyclosproject/ng-openapi-gen) ).
...

Full documentation is located [here](https://kbuntrock.github.io/openapi-maven-plugin) and is available in english or french.
