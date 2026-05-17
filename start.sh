#!/bin/sh
set -e

echo "=== Starting Iyal Library Application ==="
echo "Java version:"
java -version

echo ""
echo "Looking for JAR file..."
ls -lh /app/

echo ""
echo "Starting application..."
exec java $JAVA_OPTS -jar /app/app.jar
