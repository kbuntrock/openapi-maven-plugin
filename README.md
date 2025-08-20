# OpenAPI Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/io.github.kbuntrock/openapi-maven-plugin.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.kbuntrock/openapi-maven-plugin)
[![CircleCI](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev.svg?style=shield)](https://circleci.com/gh/kbuntrock/openapi-maven-plugin/tree/dev)
![Coverage](../badges/jacoco.svg)
![GitHub](https://img.shields.io/github/license/kbuntrock/openapi-maven-plugin?color=blue)

The **OpenAPI Maven Plugin** analyzes Java REST controller classes and generates **OpenAPI 3.0.3 documentation**.
It is designed to integrate seamlessly into the Maven build lifecycle, making API documentation generation **automated, fast, and reliable**.

---
## ✨ Key Features

- **Broad Annotation Support**  
Works with:
  - Spring MVC
  - Javax RS
  - Jakarta RS


- **Hybrid Analysis Approach**  
Uses both **compiled classes** and **source code** to enrich the generated specification:
  - Extracts Javadoc comments directly, without extra annotations.
  - Avoids third-party runtime libraries just to keep comments available.
  - Keeps your code **clean and dependency-free**.


- **Lightweight & Secure**
  - No extra dependencies added to your JAR/WAR.
  - Reduces the surface for security vulnerabilities.


- **Flexible Project Structures**
  - Documentation can be generated from modules that only contain interfaces.
  - Works faster than methods requiring a running application or full integration test execution.


- **Wide JDK Compatibility**  
  Verified with JDK 8, 11, 17, and 21 (integration tests are run across these versions).
---
## ⚙️ Configuration Options

The plugin provides numerous options to fine-tune the generated documentation:
- Generate **multiple specifications** with different configurations.
- Apply **whitelists / blacklists** on scanned classes and methods.
- Enrich documentation with extra metadata (e.g., security schemes, licenses).
- Define **loopback operation names**, useful for code generation tools (ex: ng-openapi-gen).
- And more...

--- 
## 📚 Documentation

Full documentation is available in both English and French:  
👉 [Project Documentation](https://kbuntrock.github.io/openapi-maven-plugin)

---
## 🔌 Swagger Core v3 annotations supports

In addition to its native support for Spring MVC, Javax RS, and Jakarta RS annotations, the plugin also provides partial support for Swagger Core v3 annotations (``io.swagger.core.v3/swagger-annotations``).  
While not all annotations are covered, a subset of the most commonly used ones is recognized and processed, offering extra flexibility for teams already relying on ``swagger-annotations`` in their codebase.  
Detailed support is listed in the documentation.

---
## ✅ Why This Plugin?

- **Rigorous development**: CI tests across multiple JDKs ensure long-term stability.
- **Focus on maintainability**: Eliminates redundant dependencies and duplicated information.
- **Productivity boost**: Documentation generation is automated during the build phase, without manual steps.

---
## 🤝 Contributing

Contributions are welcome!  
Please check our [CONTRIBUTING.md](CONTRIBUTING.md)

---
## 📜 License

This project is licensed under the [MIT licence](https://github.com/kbuntrock/openapi-maven-plugin?tab=MIT-1-ov-file#readme)