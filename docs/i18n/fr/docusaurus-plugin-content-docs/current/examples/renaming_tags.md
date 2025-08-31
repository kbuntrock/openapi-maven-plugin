---
sidebar_position: 3
sidebar_label: Renommer les tags
---

# Renommer les tags générés

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