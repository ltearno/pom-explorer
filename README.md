# pom-explorer
The Maven's swiss knife

## Description

It is often cumbersome to manage a lot of maven projects distributeds amongst several repositories. This tool helps you to update you pom graph easily.

Main functions :

- release a graph : release a pom and its dependencies and updates all dependent poms
- change a gav : updates a project's gav and all the project which depends on it

PomExplorer manages the properties, dependency management, and so on. You just have to say what you want.

## Build and run

To build :

	mvn install

To run :

	java -jar target/pom-explorer-1.0-SNAPSHOT-jar-with-dependencies.jar
	
Then go with your browser on this address

	http://localhost:90

This is the console to the application.

## Default script

There is a default script that can be executed when a new client connects. The file is called `welcome.commands` and and example already exists in the repository. The program searches it in the working directory.

## Default configuration

If a `config.properties` file is found in the working directory it is used to configure sessions when created. Here is the list of the possible flags :

- **defaultMavenSettingsFile** : if you want to use a specific Maven configuration file, you can set its path here.