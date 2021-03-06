# lider

**Lider Ahenk** is an open source project which provides solutions to manage, monitor and audit unlimited number of different systems and users on a network.

lider is the business layer of [Lider Ahenk](http://liderahenk.org/) project running on [Karaf](http://karaf.apache.org/) container. It contains core functionalities (such as LDAP client, task manager, XMPP client), core services (such as plugin DB service, log service) and provides an API for other plug-ins/bundles.

## Features

* Scalability
* Adaptability
* Integrability

## Documentation

* See [Lider wiki](https://github.com/Pardus-Kurumsal/lider/wiki) to get started!
* See how to [setup development environment](https://github.com/Pardus-Kurumsal/lider/wiki/01.-Setup-Development-Environment)
* Learn how to [build & run](https://github.com/Pardus-Kurumsal/lider/wiki/02.-Building-&-Running) lider.
* Create [Lider distribution](https://github.com/Pardus-Kurumsal/lider/wiki/03.-Lider-Distribution) as custom Karaf container.

## Contribution

We encourage contributions to the project. To contribute:

* Fork the project and create a new bug or feature branch.
* Make your commits with clean, understandable comments
* Perform a pull request

## Other Lider Ahenk Projects

* [Lider Console](https://github.com/Pardus-Kurumsal/lider-console): Administration console built as Eclipse RCP project.
* [Ahenk](https://github.com/Pardus-Kurumsal/ahenk): Agent service running on remote machines.
* [Lider Ahenk Installer](https://github.com/Pardus-Kurumsal/lider-ahenk-installer): Installation wizard for Ahenk and Lider (and also its LDAP, database, XMPP servers).
* [Lider Ahenk Archetype](https://github.com/Pardus-Kurumsal/lider-ahenk-archetype): Maven archetype for easy plugin development.

## Changelog

See [changelog](https://github.com/Pardus-Kurumsal/lider/wiki/Changelog) to learn what we have been up to.

## Roadmap

#### Today

* 30+ plugins
* Linux agent service written in Python
* Administration console built as Eclipse RCP
* Open sourced, easy to access and setup, stable Lider Ahenk v1.0.0

#### 2016

* Scalable infrastructure suitable for million+ users & systems
* 10+ new plugins (such as file distribution via torrent, remote installation)
* New reporting module & dashboard

#### 2017

* Agents for Windows and mobile platforms
* Platform-independent administration console
* Inventory scan & management
* Printer management

## License

Lider Ahenk and its sub projects are licensed under the [LGPL v3](https://github.com/Pardus-Kurumsal/lider/blob/master/LICENSE).
