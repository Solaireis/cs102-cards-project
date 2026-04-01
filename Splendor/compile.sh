#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "Compiling console game..."
mkdir -p classes classes/Properties

# Compile from the console entrypoint; javac resolves referenced classes from src.
javac -d classes -cp "src:lib/*" src/Test/Game.java

# Keep runtime configuration on the classpath.
cp config.properties classes/config.properties
cp config.properties classes/Properties/config.properties

echo "Compile complete."