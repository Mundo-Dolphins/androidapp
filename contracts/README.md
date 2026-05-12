# Mundo Dolphins API Contracts

This directory contains the API contracts that the Mundo Dolphins Android app expects from the backend service.

## Structure

- `schemas/`: contains JSON Schema files for each endpoint.
- `examples/`: contains valid JSON examples that comply with the schemas and can be parsed by the app.

## Purpose

1. **Source of Truth**: This repository is the initial source of truth for what the Android app expects currently.
2. **Backend Validation**: The backend service (in its own CI) should validate its generated JSON against these schemas.
3. **App Resilience**: Unit tests in this project ensure that the app can parse these examples and remains compatible with schema-compliant responses, even if new fields are added.

## Breaking Changes

The following changes are considered **breaking** for the Android app:
- Removing a required field.
- Changing the data type of a field (e.g., from `string` to `integer`).
- Changing the date format (the app expects ISO-8601 `date-time`).
- Changing an array to an object or vice-versa.
- Renaming fields.
- Returning `null` in a field that the app expects to be non-nullable.

## Non-Breaking Changes

- Adding new optional fields (`additionalProperties` is set to `true`).
- Adding new values to an array if the app is designed to ignore unknown values.

## How to Update

When adding a new feature that requires a new API field:
1. Update the corresponding schema in `contracts/schemas/`.
2. Update or add a valid example in `contracts/examples/`.
3. Ensure the unit tests in the Android project still pass.

## Validation

To validate that the examples comply with the schemas and that the app can parse them:
- Run the Android unit tests: `./gradlew :app:testDebugUnitTest`
- Schema validation is also integrated into the build process.
