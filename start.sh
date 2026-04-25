#!/bin/bash
echo "Starting Cinephile Database via Docker..."
docker-compose up -d

echo "Building and launching Cinephile Desktop App..."
mvn clean compile exec:java -Dexec.mainClass="com.cinephile.App"
