# 📖 Exemples d’utilisation

Cette section fournit des exemples pratiques montrant comment utiliser l'**OpenAPI Maven Plugin** dans des scénarios concrets.
Alors que la documentation principale détaille toutes les options de configuration disponibles, ces exemples ont pour but de vous donner des **modèles prêts à l’emploi** que vous pouvez rapidement adapter à vos propres projets.

Les objectifs de cette section sont de :
- ✅ Illustrer les configurations les plus courantes du plugin.
- ✅ Montrer comment intégrer le plugin dans différentes structures de projet.
- ✅ Mettre en avant les bonnes pratiques pour générer des spécifications OpenAPI via votre build Maven.
- ✅ Fournir des points de départ personnalisables selon vos besoins.

Chaque exemple comprend :
- Une description du cas d’usage.
- Un extrait de ``pom.xml`` montrant la configuration du plugin.

Ces exemples sont destinés à aider aussi bien les **nouveaux utilisateurs** à démarrer rapidement que les **utilisateurs avancés** à découvrir des configurations plus puissantes.

---
## Ajoût d’erreurs par défaut sur toutes les opérations

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
## Variante : générer des erreurs par défaut sur toutes les opérations

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


---
## Générer deux documentations d'API: une publique et une privée

Cet exemple montre comment configurer le plugin pour générer deux spécifications OpenAPI distinctes à partir du même projet :
- une pour l’API **privée**,
- une autre pour l’API **publique**,

Le tout en gardant le processus rapide (une seule exécution du plugin).

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
                <whiteList>
                    <!-- All controllers in the "admin" and "healthcheck" packages  -->
                    <entry>io.github.app.api.endpoint.admin.*</entry>
                    <entry>io.github.app.api.endpoint.healthcheck.*</entry>
                </whiteList>
                <filename>private-documentation.yml</filename>
            </api>
            <api>
                <locations>
                    <location>io.github.app</location>
                </locations>
                <blackList>
                    <!-- All controllers not in the "admin" and "healthcheck" packages  -->
                    <entry>io.github.app.api.endpoint.admin.*</entry>
                    <entry>io.github.app.api.endpoint.healthcheck.*</entry>
                </blackList>
                <filename>public-documentation.yml</filename>
            </api>
        </apis>
    </configuration>
</plugin>
```

---
## Renommer les tags générés

Par défaut, les tags sont générés à partir du nom des classes.  
Mais vous pouvez vouloir des noms plus lisibles dans votre documentation.

Cet exemple montre comment :
- supprimer le suffixe ``Controller`` à la fin de chaque tag,
- ajouter un espace entre chaque mot délimité par une majuscule.

Par exemple, la classe ``AdminPanelController.java`` sera traduite en un tag nommé ``Admin Panel``.

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
                <tag>
                    <substitutions>
                        <sub>
                            <regex>Controller$</regex>
                            <substitute></substitute>
                        </sub>
                        <sub>
                            <!-- Split with a space each time an uppercase character is found -->
                            <regex>(?&lt;!^)[A-Z]</regex>
                            <substitute xml:space="preserve"> $0</substitute>
                        </sub>
                    </substitutions>
                </tag>
            </api>
        </apis>
    </configuration>
</plugin>
```