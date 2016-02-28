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

### Karaf 4

- Download and extract [Karaf 4](https://karaf.apache.org/index/community/download.html)

## Project Directory Layout

    lider/
      lider-core/                     --> Provides core functionalities and API for other plug-ins
      lider-authorization-impl/       --> Shiro-based authorization implementation
      lider-cache-impl/               --> Provides cache mechanism
      lider-config/                   --> Contains configuration manager and default config files.
      lider-datasource-mariadb/       --> MariaDB datasource definition for Karaf
      lider-karaf/                    --> Contains Karaf features definition file
      lider-ldap-impl/                --> Provides LDAP client and search functionalities
      lider-log-impl/                 --> Provides system-wide logging mechanism
      lider-messaging-xmpp/           --> XMMP client implementation
      lider-persistence-mariadb/      --> MariaDB persistence layer
      lider-rest-impl/                --> Processes incoming requests for auth and LDAP operations
      lider-router-impl/              --> Responsible for bridging incoming requests to related plugin
      lider-service-impl/             --> Provides factory methods and command context for plugins
      lider-taskmanager-impl/         --> Task manager implementation manages agent tasks
      lider-web/                      --> Provides REST web services

## How to Setup Development Environment

1. Install [Eclipse](https://eclipse.org/downloads/) version >=4.4 (Luna or Mars).
2. Clone Lider project by running `git clone https://github.com/Pardus-Kurumsal/lider.git`.
3. Navigate to project directory and run `mvn clean install -DskipTests`.
4. Finally, import the project into Eclipse as 'Existing Maven Projects'.

## How to Import Lider Into Karaf

1. Navigate to project directory and run `mvn clean install`.
2. Start Karaf and add project repository via `feature:repo-add  mvn:tr.org.liderahenk/lider-features/1.0.0-SNAPSHOT/xml/features`.
3. Again, in Karaf shell, install project via `feature:install lider`.

## How to Export Lider as Custom Karaf Distribution?

TODO

## Static Analyzers

We also use checkstyle and findbugs plugins to do static analyzing on the changes. Run the following commands to analyze your code to check if it is compatible.

`mvn clean compile -P findbugs`
`mvn clean validate -P checkstyle`
