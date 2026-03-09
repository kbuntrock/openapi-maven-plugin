# AGENTS.md — AI Agent Guide for OpenAPI Maven Plugin

This file provides AI coding agents with the context, conventions, and constraints needed to contribute effectively to this project.

---

## Project Overview

**OpenAPI Maven Plugin** is a Maven plugin that analyzes Java REST controller classes and generates **OpenAPI 3.0.3 documentation** automatically during the build lifecycle.

Key characteristics:
- Works with **Spring MVC**, **Jakarta REST**, and **JAX-RS** annotations.
- Uses a **hybrid analysis approach**: inspects compiled bytecode via reflection AND parses source files to extract Javadoc comments — without requiring any extra runtime annotations or dependencies.
- Adds **zero runtime dependencies** to the target project's JAR/WAR.
- Supports **Java 8 through JDK 25**
- Partial support for **Swagger Core v3** annotations (`io.swagger.core.v3/swagger-annotations`).
- Licensed under **MIT**.

---

## Repository Structure

```
/
├── openapi-maven-plugin/       # Main Maven plugin source (the artifact to build)
│   ├── src/main/java/          # Plugin source code
│   ├── src/test/java/          # Unit and integration tests
│   ├── eclipse-formatter.xml   # Eclipse-based code formatter configuration
│   └── pom.xml                 # Plugin build descriptor
├── integration-tests/          # Separate Maven module for end-to-end integration tests
│   └── src/test/               # IT test classes and resources
├── docs/                       # Docusaurus documentation website
│   ├── docs/                   # English documentation pages (source of truth)
│   └── i18n/                   # French translations
├── .circleci/config.yml        # CircleCI CI/CD pipeline
├── .github/
│   ├── workflows/              # GitHub Actions workflows
│   ├── pull_request_template.md
│   └── ISSUE_TEMPLATE/
├── CONTRIBUTING.md
└── README.md
```

The **source of truth for the plugin** is exclusively under `openapi-maven-plugin/`. All build and test commands should be run from that subdirectory unless explicitly noted.

---

## Key Source Packages

All classes live under `io.github.kbuntrock` inside `openapi-maven-plugin/src/main/java/`.

| Package / File | Role |
|---|---|
| `DocumentationMojo.java` | Plugin entry point (Maven Mojo); orchestrates the full generation |
| `JavaClassAnalyser.java` | Analyzes a single Java class via reflection |
| `TagLibrary.java` | Builds the full tag/operation library from scanned classes |
| `ApiResourceScanner.java` | Scans classpath for REST controller classes |
| `configuration/CommonApiConfiguration.java` | Shared configuration parameters across all documents |
| `configuration/ApiConfiguration.java` | Per-document configuration; extends and overrides common config |
| `model/DataObject.java` | Internal model for OpenAPI schema objects |
| `model/Endpoint.java` | Internal model for a single REST endpoint |
| `model/ParameterObject.java` | Internal model for endpoint parameters |
| `yaml/YamlWriter.java` | Serializes the internal model to OpenAPI 3.0.3 YAML |
| `reflection/` | Utilities for reflection, generics resolution, and bean introspection |
| `javadoc/` | Source-level Javadoc parsing (via JavaParser) |

---

## Build & Test Commands

All commands below must be run from the **`openapi-maven-plugin/`** subdirectory.

```bash
# Build including unit tests + integration tests
mvn clean verify

# Apply the Eclipse code formatter
mvn formatter:format

# Validate that code matches the formatter rules (no changes applied)
mvn formatter:validate
```

To preview the documentation website locally (run from the `docs/` directory):

```bash
npm run start
# Preview at http://localhost:3000
# or for the french version:
npm run fr
```

---

## Coding Conventions

- **Java source compatibility**: Java 8 (`maven.compiler.source=1.8`). Do not use any language feature or API introduced after Java 8.
- **Code formatter**: The project uses an Eclipse-based formatter. Always run `mvn formatter:format` before committing, or configure your IDE using `openapi-maven-plugin/eclipse-formatter.xml`. IntelliJ users can use the [Eclipse Code Formatter plugin](https://github.com/krasa/EclipseCodeFormatter#instructions).
- **Tests**: Use JUnit 5. New behavior must be covered by a corresponding test that fails when the runtime change is absent.
- **Approval tests**: YAML output correctness is validated using [ApprovalTests](https://github.com/approvals/ApprovalTests.Java). Approved reference files live alongside the test classes and must be updated when expected output changes.
- **Javadoc**: Update Javadoc on public methods and classes when their contract changes.
- **Commits**: Keep commits atomic. Write clear, descriptive messages explaining *why* the change was made.
- **No comments narrating the code**: Comments must explain non-obvious intent or trade-offs, not describe what the code literally does.
- **Language**: All code (comments, Javadoc, variable names, commit messages) and all documentation must be written in **English**.
---

## Configuration System — Critical Rules

The plugin supports generating multiple OpenAPI documents from a single codebase. Configuration is split into two levels:

- **`CommonApiConfiguration`** — parameters shared across all documents.
- **`ApiConfiguration`** — per-document config; inherits and can override common config.

**When adding a new common configuration parameter**, you MUST update all three locations:

1. **`CommonApiConfiguration.java`**: add the field with its getter and setter.
2. **Copy constructor** `CommonApiConfiguration(final CommonApiConfiguration commonApiConfiguration)`: initialize the new field from the source instance.
3. **`ApiConfiguration.mergeWithCommonApiConfiguration()`**: add the field both where it is copied from the parent *and* where it may be overridden by the per-document value.

Failing to update all three locations will cause silent misconfiguration bugs.

---

## Documentation

- English documentation files are the **source of truth** and are located in `docs/docs/`.
- French translations are in `docs/i18n/`.
- When adding or changing a configuration option, **update the English documentation** at minimum.
- Configuration reference: `docs/docs/configuration.md`.
- The documentation site is built with [Docusaurus](https://docusaurus.io/).

---

## CI / CD

- **CircleCI** runs on every push to `dev`: builds the plugin and runs tests across JDK 8, 11, 17, 21, and 25 using a custom Docker image (`kevinbuntrock/maven-toolchain-jdk-8-11-17`).
- **GitHub Actions** handle the release workflow.
- A PR is considered valid only if `mvn clean verify` passes on all supported JDK versions.

---

## Branching & Pull Request Rules

- The default integration branch is **`dev`**. All pull requests must target `dev`, never `main`/`master`.
- Follow the checklist in `.github/pull_request_template.md` before submitting.
- Keep each PR focused on a single concern.
- Make sure the PR description explains **what**, **how**, and **why**.

---

## What Agents Must NOT Do

- **Do not use Java features above Java 8** (no `var`, no records, no switch expressions, etc.).
- **Do not add runtime dependencies** to the plugin. Dependencies must be scoped `provided` or `test` unless strictly required at plugin runtime, in which case justify it explicitly.
- **Do not modify files under `target/`** — they are generated artifacts.
- **Do not skip the formatter step** — CI will fail on formatting violations.
- **Do not update only `CommonApiConfiguration`** when adding config parameters; `ApiConfiguration` must always be updated consistently (see Configuration System section above).
