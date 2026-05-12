#!/bin/bash

set -euo pipefail

# Configuration with defaults
CONTRACTS_REPO="${CONTRACTS_REPO:-Mundo-Dolphins/mundo-dolphins.github.io}"
CONTRACTS_REF="${CONTRACTS_REF:-main}"
DEST_DIR="contracts/schemas"
TEMP_DIR="temp_contracts_sync"

echo "Syncing API contracts from ${CONTRACTS_REPO} at ref ${CONTRACTS_REF}..."

# Clean up any previous temp directory
rm -rf "$TEMP_DIR"
mkdir -p "$TEMP_DIR"

# Initialize a new git repository in the temp directory
cd "$TEMP_DIR"
git init -q
git remote add origin "https://github.com/${CONTRACTS_REPO}.git"

# Configure sparse-checkout to only pull contracts/schemas
git config core.sparseCheckout true
echo "contracts/schemas/*" >> .git/info/sparse-checkout

# Fetch and checkout the target ref
git fetch -q --depth 1 origin "$CONTRACTS_REF"
git checkout -q FETCH_HEAD

# Verify source directory exists
if [ ! -d "contracts/schemas" ]; then
    echo "Error: Source directory 'contracts/schemas' not found in the remote repository."
    cd ..
    rm -rf "$TEMP_DIR"
    exit 1
fi

cd ..

# Clean destination directory
mkdir -p "$DEST_DIR"
rm -rf "${DEST_DIR:?}"/*

# Copy schemas to destination
cp -r "$TEMP_DIR/contracts/schemas/"* "$DEST_DIR/"

# Cleanup temp directory
rm -rf "$TEMP_DIR"

# Validation
echo "Validating synchronized schemas..."
SYNCED_FILES=$(find "$DEST_DIR" -type f \( -name "*.json" -o -name "*.schema.json" \))

if [ -z "$SYNCED_FILES" ]; then
    echo "Error: No schema files found after sync."
    exit 1
fi

# Check if jq is available for JSON validation
HAS_JQ=$(command -v jq >/dev/null 2>&1 && echo "true" || echo "false")

for FILE in $SYNCED_FILES; do
    if [ "$HAS_JQ" = "true" ]; then
        if ! jq empty "$FILE" 2>/dev/null; then
            echo "Error: File $FILE is not a valid JSON."
            exit 1
        fi
    else
        # Fallback to python if jq is missing
        if ! python3 -m json.tool "$FILE" >/dev/null 2>&1; then
            echo "Error: File $FILE is not a valid JSON."
            exit 1
        fi
    fi
    echo "  - Validated: $(basename "$FILE")"
done

echo "Successfully synchronized $(echo "$SYNCED_FILES" | wc -l | xargs) schemas to $DEST_DIR."
