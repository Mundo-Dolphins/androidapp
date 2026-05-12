# Mundo Dolphins API Contracts

This directory contains the API contracts that the Mundo Dolphins Android app expects from the backend service.

## Structure

- `schemas/`: contains JSON Schema files for each endpoint.
- `examples/`: contains valid JSON examples that comply with the schemas and can be parsed by the app.

## Source of Truth

The canonical JSON Schemas are managed in the backend service repository:
[Mundo-Dolphins/mundo-dolphins.github.io/contracts/schemas](https://github.com/Mundo-Dolphins/mundo-dolphins.github.io/tree/main/contracts/schemas)

This Android repository maintains a copy of these schemas for compatibility testing. The copies are synchronized automatically via a GitHub Actions workflow that creates a Pull Request when changes are detected in the backend.

**Note:** Do not edit the files in `contracts/schemas/` manually in this repository unless it is an emergency. They will be overwritten by the next sync.

## Synchronization

### Automatic Sync
The sync workflow is triggered:
- Manually via "Actions" tab in GitHub.
- Automatically via `repository_dispatch` when the backend repo updates its contracts.

### Manual Local Sync
You can run the sync script locally if needed:
```bash
CONTRACTS_REPO=Mundo-Dolphins/mundo-dolphins.github.io CONTRACTS_REF=main ./scripts/sync-api-contracts.sh
```

## Breaking Changes and Incompatibilities

The following changes are considered **breaking** for the Android app:
- Removing a required field.
- Changing the data type of a field (e.g., from `string` to `integer`).
- Changing the date format (the app expects ISO-8601 `date-time`).
- Changing an array to an object or vice-versa.
- Renaming fields.
- Returning `null` in a field that the app expects to be non-nullable.

If a synchronization PR fails its checks, it means the backend has introduced a change that breaks the Android app's current parsing logic. In this case:
1. **Analyze the failure**: Check the unit test logs to see which model failed to parse the updated schemas/examples.
2. **Coordinate**: 
   - Revert or adapt the change in the web service if it was unintentional.
   - Version the API if both formats must coexist.
   - Update the Android app to support the new format before merging the contract update.

## Non-Breaking Changes

- Adding new optional fields (`additionalProperties` is set to `true`).
- Adding new values to an array if the app is designed to ignore unknown values.

## Validation

To validate that the examples comply with the schemas and that the app can parse them:
- Run the Android unit tests: `./gradlew :app:testDebugUnitTest`
- Run the specific contract validation task: `./gradlew :app:validateApiContracts`
- Schema validation is also integrated into the build process.
