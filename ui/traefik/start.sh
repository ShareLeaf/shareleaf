#!/usr/bin/env bash
docker-compose down -v
docker-compose up --force-recreate --build -d
