#!/bin/bash

jhipster jdl app.jdl --force
git checkout src/main/resources/config/liquibase
git diff -R > app.patch1
mv app.patch1 app.patch
git apply app.patch -v
