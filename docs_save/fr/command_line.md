# 📟 Utilisation en ligne de commande

Vous pouvez exécuter le plugin directement depuis la ligne de commande.  
Le paramètre principal requis est ``openapi.locations``.  

Lorsque ce paramètre est fourni, le plugin crée automatiquement une [configuration API](fr/configuration.md#apis) qui peut ensuite être personnalisée avec des paramètres supplémentaires.

Vous pouvez voir un exemple d’utilisation en ligne de commande sur la [page de démarrage rapide](fr/quick-start.md#⚡-command-line)

### Options disponibles

``openapi.locations`` :  
Définit les packages à analyser pour détecter les endpoints REST.
- **Obligatoire** pour des raisons de performance.
- Plusieurs packages peuvent être fournis, séparés par des virgules.
  - (Exemple : ``-Dopenapi.locations=pkgone,pkgtwo``).
---
``openapi.library`` :  
Spécifie le framework que vous utilisez.  
Valeurs supportées :
- ``SPRING_MVC`` *(default)*
- ``JAKARTA_RS``
- ``JAVAX_RS``
---
``openapi.tagAnnotations`` :  
Spécifie quelle annotation doit être utilisée pour détecter les endpoints.
- Optionnel si vous n’utilisez pas Spring.
- Accepte plusieurs valeurs, séparées par des virgules.
- Valeurs supportées :
  - ``RestController`` *(default)*
  - ``RequestMapping``
---
``openapi.filename`` :  
Spécifie le nom du fichier de documentation généré.
---
``openapi.javadoc.locations`` :  
Définit les chemins relatifs (depuis la racine du projet) vers les fichiers Java afin d’extraire les commentaires.
- Valeur par défaut : ``src/main/java``.
---
``openapi.javadoc.scanEnabled`` :  
Active ou désactive l’enrichissement de la documentation à partir des commentaires Javadoc.
- Valeurs supportées :
  - ``true`` (default)
  - ``false``
- ⚠️ Si une configuration``pom.xml`` est également présente, l’analyse Javadoc sera désactivée dans ce cas aussi.
