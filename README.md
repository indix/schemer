# schemer
[![Build Status](https://travis-ci.org/indix/schemer.svg?branch=master)](https://travis-ci.org/indix/schemer) [![Maven](https://maven-badges.herokuapp.com/maven-central/com.indix/schemer-core_2.11/badge.svg)](http://repo1.maven.org/maven2/com/indix/schemer-core_2.11/) [![Docker Pulls](https://img.shields.io/docker/pulls/indix/schemer-registry.svg)](https://hub.docker.com/r/indix/schemer-registry/)

<p align="center">
    <img src="resources/images/schemer-logo-text-wide.png" width="444" height="256"/>
</p>

Schema registry with support for CSV, TSV, AVRO, JSON and Parquet. Has ability to infer schema from a given data source.

## Schemer UI [WIP]

<p align="center">
    <img src="resources/images/001.png" />
</p>

Schemer UI is the wizard based frontend for Schemer. It provides a wizard based schema creation and versioning workflow apart from browsing and search capabilities. It is a work in progress. [More screens](schemer-ui.md)

## Schemer Core

`schemer-core` is the core library that implements most of the logic needed to understand the supported schema types along with the schema inference. To use `schemer-core` directly, just add it to your dependencies:

```
libraryDependencies += "com.indix" %% "schemer" % "v0.2.3"
```

## Schemer Registry

`schemer-registry` is a schema registry for storing the metadata about schema and schema versions. It provides a GraphQL API for adding, viewing and inferring schemas.

Schemer Registry is available as a [docker image at DockeHub](https://hub.docker.com/r/indix/schemer-registry/)

### Running Locally

Local docker based PostgreSQL can be run as follows:

```
docker run -e POSTGRES_USER=schemer -e POSTGRES_PASSWORD=schemer -e PGDATA=/var/lib/postgresql/data/pgdata -e POSTGRES_DB=schemer -v $(pwd)/schemer_db:/var/lib/postgresql/data/pgdata -p 5432:5432 postgres:9.5.0
```

Remove `schmer_db` folder to clear all data and start from scratch.

The registry service can be run using `sbt`:

```bash
sbt "project registry" ~reStart
```


