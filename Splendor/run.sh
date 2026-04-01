#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# Always compile so runtime matches latest source edits.
./compile.sh

echo "Running console game..."
java -cp "classes:lib/*" Test.Game