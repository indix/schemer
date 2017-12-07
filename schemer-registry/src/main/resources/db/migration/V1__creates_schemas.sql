-- SchemaRegistry
CREATE TABLE "schemas"(
    "id" UUID NOT NULL,
    "name" VARCHAR NOT NULL,
    "namespace" VARCHAR NOT NULL,
    "type" VARCHAR NOT NULL,
    "created_on" TIMESTAMP NOT NULL,
    "created_by" VARCHAR NOT NULL

);
ALTER TABLE "schemas" ADD CONSTRAINT "schemas_id" PRIMARY KEY("id");