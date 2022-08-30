# Configuration

## outputDirectory

- Type: `string`
- Default: `${project.build.directory}`

Le répertoire de génération des documents openapi.

```xml
<configuration>
	<outputDirectory>${project.build.directory}/somewhere</outputDirectory>
</configuration>
```

## apiConfiguration

- Type: `balise de section`

Configurations générales, qui seront appliquées à chaque document généré. La pluspart des configurations déclarées dans cette section peuvent être ensuite surchargées pour chaque document.

```xml
<configuration>
	<apiConfiguration>
		[...]
	</apiConfiguration>
</configuration>
```


### attachArtifact

- Type: `boolean`
- Default: `true`

Attache la documentation générée en tant qu'artefact maven lors de la phase "install"

```xml
<apiConfiguration>
	<attachArtifact>false</attachArtifact>
</apiConfiguration>
```

## javadocConfiguration

- Type: `balise de section`

Configurations liées à la javadoc (extraction et interprétation) |

## apis

- Type: `balise de section`

Configurations spécifiques à chaque document généré. Obligatoire, et doit contenir au moins un élément
