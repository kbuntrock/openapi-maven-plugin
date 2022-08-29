# Configuration

## Principales sections de configuration

| **Nom** | **Type** | **Description** |
|------------------------|--------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| `outputDirectory` | string | Indique le répertoire de génération des documentations. Par défaut : Set the output directory for generated files. Default is `${project.build.directory}` |
| `apiConfiguration` | balise de section | Configurations générales, qui seront appliquées à chaque document généré. La pluspart des configurations déclarées dans cette section peuvent être ensuite surchargées pour chaque document. |
| `javadocConfiguration` | balise de section | Configurations liées à la javadoc (extraction et interprétation) |
| `apis` **required** | balise de section | Configurations spécifiques à chaque document généré. Obligatoire, et doit contenir au moins un élément |

## Section "apiConfiguration"

A continuer