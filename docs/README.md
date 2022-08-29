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