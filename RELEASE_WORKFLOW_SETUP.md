# Release Workflow Setup Guide

This document explains how to set up the release workflow for building signed APK and AAB files.

## Overview

The release workflow (`release.yml`) builds signed release artifacts when:
- A version tag is pushed (e.g., `v1.3.0`)
- Manually triggered via workflow_dispatch

## Required GitHub Secrets

To enable the release workflow, configure the following secrets in your GitHub repository:

### 1. Keystore Secrets

- **`KEYSTORE_FILE`**: Base64-encoded keystore file
  - Generate: `base64 your-release-keystore.jks | tr -d '\n' > keystore.txt`
  - Copy the contents of `keystore.txt` to this secret

- **`KEYSTORE_PASSWORD`**: Password for the keystore file

- **`KEY_ALIAS`**: Alias of the signing key within the keystore

- **`KEY_PASSWORD`**: Password for the signing key

### 2. API Key Secrets (Already Configured)

- **`GCLOUD_VISION_API_KEY`**: Google Cloud Vision API key (for OCR)
- **`OCR_SPACE_API_KEY`**: OCR.Space API key (fallback OCR)

## Creating a Keystore

If you don't have a release keystore, create one:

```bash
keytool -genkey -v \
  -keystore release-keystore.jks \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -alias release-key
```

Follow the prompts to set passwords and provide certificate information.

## Configuring Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add each secret listed above

## Triggering a Release

### Option 1: Tag-based Release (Recommended)

```bash
# Tag the commit
git tag -a v1.3.0 -m "Release version 1.3.0"

# Push the tag
git push origin v1.3.0
```

The workflow will automatically build and create a GitHub release with artifacts.

### Option 2: Manual Trigger

1. Go to **Actions** tab in your repository
2. Select **Build Release APK/AAB** workflow
3. Click **Run workflow**
4. Enter the version name
5. Click **Run workflow**

## Artifacts

The workflow produces:

- **app-release.apk**: Signed APK for direct installation
- **app-release.aab**: Android App Bundle for Google Play Store upload

## Workflow Behavior

- **Gated on Secrets**: The workflow only runs if `KEYSTORE_FILE` secret is configured
- **Debug Workflow Unaffected**: The existing debug build workflow (`android-build.yml`) continues to work normally
- **Changelog**: Automatically generated from recent commit messages

## Fallback: Building Locally

If secrets are not configured, you can build releases locally:

```bash
# Build release APK
./gradlew assembleRelease \
  -Pandroid.injected.signing.store.file=path/to/release-keystore.jks \
  -Pandroid.injected.signing.store.password=YOUR_KEYSTORE_PASSWORD \
  -Pandroid.injected.signing.key.alias=YOUR_KEY_ALIAS \
  -Pandroid.injected.signing.key.password=YOUR_KEY_PASSWORD

# Build release AAB
./gradlew bundleRelease \
  -Pandroid.injected.signing.store.file=path/to/release-keystore.jks \
  -Pandroid.injected.signing.store.password=YOUR_KEYSTORE_PASSWORD \
  -Pandroid.injected.signing.key.alias=YOUR_KEY_ALIAS \
  -Pandroid.injected.signing.key.password=YOUR_KEY_PASSWORD
```

## Security Notes

- **Never commit your keystore file** to the repository
- **Never commit keystore passwords** in plain text
- Keep your keystore file backed up securely (losing it means you can't update your app)
- Use different keystores for debug and release builds

## Troubleshooting

### Workflow Skipped

If the workflow is skipped with "Secrets not configured":
- Verify that `KEYSTORE_FILE` secret exists
- Check that the secret value is not empty
- Ensure the base64 encoding is correct (no line breaks)

### Signing Failed

If signing fails during the build:
- Verify keystore password is correct
- Check that key alias exists in the keystore
- Ensure key password is correct
- Confirm keystore file was decoded properly

### APK/AAB Not Found

If artifacts are not uploaded:
- Check the Gradle build logs for errors
- Verify output paths in the workflow file
- Ensure signing completed successfully
