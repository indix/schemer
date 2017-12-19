CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE "namespaces"(
    "id" UUID NOT NULL DEFAULT uuid_generate_v4(),
    "name" VARCHAR NOT NULL
);

ALTER TABLE "namespaces" ADD CONSTRAINT "namespaces_id" PRIMARY KEY("id");
CREATE UNIQUE INDEX "namespaces_name" ON "namespaces"("name");

INSERT INTO "namespaces"("name") VALUES('default');

CREATE TABLE "schemas"(
    "id" UUID NOT NULL DEFAULT uuid_generate_v4(),
    "name" VARCHAR NOT NULL,
    "namespace" VARCHAR NOT NULL,
    "type" VARCHAR NOT NULL,
    "created_on" TIMESTAMP NOT NULL,
    "created_by" VARCHAR NOT NULL

);

ALTER TABLE "schemas" ADD CONSTRAINT "schemas_id" PRIMARY KEY("id");
CREATE UNIQUE INDEX "schemas_name_namespace" ON "schemas"("name","namespace");
ALTER TABLE "schemas" ADD CONSTRAINT "schemas_namespace_fk" FOREIGN KEY("namespace") REFERENCES "namespaces"("name");