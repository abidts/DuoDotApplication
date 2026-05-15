#!/bin/sh
set -e

# Decode Firebase credentials from base64 env var if provided
if [ -n "$FIREBASE_CREDENTIALS_JSON_BASE64" ]; then
    echo "$FIREBASE_CREDENTIALS_JSON_BASE64" | base64 -d > /tmp/firebase-credentials.json
    export FIREBASE_CREDENTIALS_PATH=/tmp/firebase-credentials.json
fi

exec java -Xmx512m -jar /app/app.jar
