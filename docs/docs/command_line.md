---
sidebar_position: 3
sidebar_label: Command line
---

# 📟 Command-Line Usage

You can run the plugin directly from the command line.  
The main required parameter is ``openapi.locations``.  

When this parameter is provided, the plugin automatically creates an [API configuration](configuration.md#apis) which can then be customized with additional parameters.

You can see an example of command line usage in the [quick start page](quick_start.md#-command-line)

### Available Options

``openapi.locations``:  
Defines the packages to scan for REST endpoints.
- **Required** for performance reasons.
- Multiple packages can be provided, separated by commas.
  - (Example: ``-Dopenapi.locations=pkgone,pkgtwo``).
---
``openapi.library``:  
Specifies the framework you are using.  
Supported values:
- ``SPRING_MVC`` *(default)*
- ``JAKARTA_RS``
- ``JAVAX_RS``
---
``openapi.tagAnnotations``:  
Specifies which annotation should be used to detect endpoints. 
- Optional if you are not using Spring.  
- Accepts multiple values, separated by commas.
- Supported values are:
  - ``RestController`` *(default)*
  - ``RequestMapping``
---
``openapi.filename``:  
Specifies the name of the generated documentation file.

---
``openapi.javadoc.locations``:  
Defines the relative paths (from the project root) to Java source files for extracting Javadoc comments.  
- Default: ``src/main/java``.
---
``openapi.javadoc.scanEnabled``:  
Enables or disables Javadoc-based enhancements in the generated documentation.
- Supported values are:
  - ``true`` (default)
  - ``false``
- ⚠️ If a pom.xml configuration is also present, Javadoc scanning will be disabled there as well.
