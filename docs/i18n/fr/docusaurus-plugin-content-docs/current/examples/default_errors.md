---
sidebar_position: 1
sidebar_label: Erreurs par défaut
---

# Erreurs par défaut sur toutes les opérations

This example demonstrates how to configure the plugin to add by default two responses (401 - Unauthorized and 404 - Not Found) on all generated operations.

## Méthode initiale

Cet exemple montre comment configurer le plugin pour ajouter par défaut deux réponses (401 - Unauthorized et 404 - Not Found) sur toutes les opérations générées.

1. Créez un fichier JSON à l’emplacement ``${project.basedir}/src/main/documentation.default-errors.json``  
   Il décrit les réponses ajoutées automatiquement à toutes les opérations.

Son contenu est :

```json
{
  "401": {
    "$ref": "#/components/responses/Unauthorized"
  },
  "404": {
    "$ref": "#/components/responses/NotFound"
  }
}
```

2. Créez un fichier JSON à l’emplacement ``${project.basedir}/src/main/openapi-template.json``  
   Il décrit du contenu additionnel ajouté une seule fois à la documentation générée.
   Il peut servir à ajouter des éléments qui ne sont pas détectables dans le code source (serveurs, sécurité, …).

Son contenu est :

```json
{
  "components": {
    "responses": {
      "Unauthorized": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The called is unauthorized to access this resource."
      },
      "NotFound": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The specified resource was not found."
      }
    },
    "schemas": {
      "Error": {
        "description": "An error object",
        "properties": {
          "code": {
            "description": "A technical error code",
            "type": "string"
          },
          "message": {
            "description": "A human readable error message",
            "type": "string"
          }
        },
        "required": [
          "code",
          "message"
        ],
        "type": "object"
      }
    }
  }
}
```

3. Configurez ensuite le plugin comme suit :

```xml
<plugin>
    <groupId>io.github.kbuntrock</groupId>
    <artifactId>openapi-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>documentation</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <apis>
            <api>
                <locations>
                    <location>io.github.app</location>
                </locations>
                <freeFields>src/main/documentation/openapi-template.json</freeFields>
                <defaultErrors>src/main/documentation/default-errors.json</defaultErrors>
            </api>
        </apis>
    </configuration>
</plugin>
```

---
## Variante

Il est possible de forcer le plugin à analyser une classe Java qui n’est pas explicitement utilisée dans le code.

Dans cet exemple, nous supposons qu’il existe une classe ``Error.java`` avec l’implémentation suivante :

```java
package io.github.app;

public class Error {

    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
```

1. Suivez l’étape 1 de l’exemple précédent.


2. Créez un fichier JSON à l’emplacement ``${project.basedir}/src/main/openapi-template.json``  
   Il décrit du contenu ajouté une seule fois à la documentation générée.
   Cette fois-ci, il ne décrira pas le schéma **Error**.

Son contenu est :

```json
{
  "components": {
    "responses": {
      "Unauthorized": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The called is unauthorized to access this resource."
      },
      "NotFound": {
        "content": {
          "application/json": {
            "schema": {
              "$ref": "#/components/schemas/Error"
            }
          }
        },
        "description": "The specified resource was not found."
      }
    }
  }
}
```

3. Configurez ensuite le plugin comme suit :

```xml
<plugin>
    <groupId>io.github.kbuntrock</groupId>
    <artifactId>openapi-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>documentation</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <apis>
            <api>
                <locations>
                    <location>io.github.app</location>
                </locations>
                <freeFields>src/main/documentation/openapi-template.json</freeFields>
                <defaultErrors>src/main/documentation/default-errors.json</defaultErrors>
                <extraSchemaClasses>
                    <!-- Here we add the Error class to the schema, even if we can't explicitly detect if it is used by an endpoint. -->
                    <class>io.github.app.Error</class>
                </extraSchemaClasses>
            </api>
        </apis>
    </configuration>
</plugin>
```