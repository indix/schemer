#!/usr/bin/env bash

set -ex

sbt "project core" +publishSigned
sbt sonatypeReleaseAll

echo "Released"