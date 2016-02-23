# lider

lider is the business layer of Lider Ahenk project running on Karaf container. It contains core functionalities (such as LDAP client, task manager, XMPP client) and core services (such as plugin DB service, log service) and provides an API for other plug-ins/bundles.

## Prerequisites

### JDK7

- Download and install [JDK7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)

### Git

- Documentation about installing and configuring git can be found [here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) and [here](https://git-scm.com/book/en/v2/Getting-Started-First-Time-Git-Setup)
- Git [home](http://git-scm.com/) (download, docs)

### Maven 3

- Get [Maven 3](http://maven.apache.org/install.html) (Specifically, at least **version 3.1.1** is needed to use static code analyzers and maven-tycho plugin!).
- Maven [home](https://maven.apache.org/) (download, docs)

## Project Directory Layout

TODO

## Exporting Lider

TODO

## How to Setup Development Environment

1. Install [Eclipse](https://eclipse.org/downloads/) version >=4.4 (Luna or Mars).
2. Clone lider project by running `git clone https://github.com/Pardus-Kurumsal/lider.git`.
3. Change directory to lider/ and run `mvn clean install -DskipTests`.
4. Finally, import the project into Eclipse as 'Existing Maven Projects'.

## Static Analyzers

We also use checkstyle and findbugs plugins to do static analyzing on the changes. Run the following commands to analyze your code to check if it is compatible.

`mvn clean compile -P findbugs`
`mvn clean validate -P checkstyle`
