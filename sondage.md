# Questionnaire Pom Explorer

Pom Explorer est un logiciel d'aide à la gestion des pom.xml. La situation est en effet compliquée quand les projets sont nombreux (100+) et sont dispersés dans de multiples dépôts.
Problématiques : gestion des versions, propagation des changements, vision de haut niveau difficile à avoir.

Fonctionalités offertes :

- Consultation,
- Visualisation,
- Transformation du graphe des projets,
- Construction,
- Vérifications,
- Utilisation locale ou mutualisée.

## Parmis ces fonctionnalités quelles sont les plus utiles ?

- Points d'entrées pour charger des projets :
	- Un répertoire,
	- Une adresse de dépôt GIT,
	- Télécharger depuis Maven Central.

- Consultation :
	- Vue synthétique d'un projet : chaine des parents, références directes au projet, propriétés (héritées aussi), dépendances déclarées, dépendances transitives.
	- La vue synthétique permet aussi de retrouver instantanément où sont définies les propriétés utilisées, l'emplacement où la dépendance est managée, etc.
	- Informations sur les poms accessibles à tous immédiatement,
	- Dashboard des projets (avec quoi dedans?),
	- Classes Java fournies par un artifact,
	- Export des données du graphe,
	- Inspecter les conventions de nommage et les dépendances entre projets.

- Visualisation (aide à l'appréhension de la structure du graphe) :
	- Diagramme de dépendances (entre projets, entre dépôts)
	- Graphe 3d dynamique.

- Application de transformations sur le graphe des poms :
	- Changement d'un GAV : changer les références à ce GAV (en prenant en compte les propriétés, dependency management, etc),
	- Gestion de multiples dépôts git,
	- Releaser un graphe.

- Détection d'anomalies :
	- Convergence de version des dépendances,
	- Détection des dépendances inutiles (jar non référencé),
	- Détection des dépendances transitives utilisées,
	- Détection de versions multiples.

- Construction :
	- Gestion **entièrement** automatique du pipeline de build,
	- Utilisation locale : recompilation déclenchée par changement sur fichiers sources,
	- Utilisation serveur : recompilation déclenchée par les commits,
	- Construction en fonction des dépendances.

- Utilisation :
	- Locale en développement,
	- Mutualisée sur un serveur,
	- Les deux.

- Futur :
	- Gérer les projets Gradle et NPM,
	- Distribution des builds sur des slaves
	- Git hooks,
	- Montrer les chemins entre deux poms,
	- Interface Web, en deux clics on change les versions et propage les changements.

- Utilisation :
	- en ligne de commande,
	- avec un client web graphique.

- Les questions que vous vous posez sur la structure de vos projets sans trouver de réponse facilement.

- Interface graphique :
	- Vue projets : consultation et changements.
	- Vue graphe : graphe des GA (sans version), dépendances transitives, dépendances déclarées, compile scope dependencies, graphe de build.
	- Vue de construction : visualisation du pipeline de build, demande construction projet.