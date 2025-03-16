#!/bin/bash

jhipster jdl app.jdl --force
git checkout src/main/resources/config/liquibase
git apply app.patch -v
