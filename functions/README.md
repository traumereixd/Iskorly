# Iskorly Cloud Functions

This directory contains Firebase Cloud Functions for the Iskorly app.

## Functions

### 1. `appStatus` (HTTP Endpoint)

**Type:** HTTP Request Handler  
**URL:** `https://{project}.web.app/appStatus` or `https://{region}-{project}.cloudfunctions.net/appStatus`

Serves the kill-switch status directly from Firestore to the Android app.

**Response Format:**
```json
{
  "disabled": false,
  "message": "App is currently enabled and functioning normally."
}
```

**Features:**
- Reads from Firestore: `app_config/status`
- Sets no-cache headers
- Provides fallback values on error

### 2. `syncKillSwitchToStorage` (Firestore Trigger)

**Type:** Firestore Document Trigger  
**Trigger:** `app_config/status` document write events

Automatically syncs kill-switch status from Firestore to a public Cloud Storage file whenever the admin makes changes.

**Output:** 
- Creates/updates: `app-status.json` in Cloud Storage bucket
- Public URL: `https://storage.googleapis.com/{bucket}/app-status.json`

**Features:**
- Triggers on create, update, or delete
- Makes the file publicly accessible
- Sets proper content-type and cache-control headers
- Ensures the static file is always in sync with Firestore

## Deployment

Deploy all functions:
```bash
firebase deploy --only functions
```

Deploy a specific function:
```bash
firebase deploy --only functions:appStatus
firebase deploy --only functions:syncKillSwitchToStorage
```

## Development

Install dependencies:
```bash
npm install
```

Run linter:
```bash
npm run lint
```

Test locally with Firebase Emulator:
```bash
npm run serve
```

## Configuration

The functions use the default Firebase Admin SDK initialization, which automatically uses:
- Default Firestore database
- Default Storage bucket
- Service account credentials from Firebase project

No additional environment variables or configuration needed.

## Logs

View function logs:
```bash
npm run logs
```

Or in Firebase Console:
- Functions → Dashboard → Select function → Logs tab

## Monitoring

Monitor function execution, errors, and performance in Firebase Console:
- Functions → Dashboard
- Functions → Health tab
- Functions → Logs tab

## Security

- The `appStatus` endpoint is public (required for Android app access)
- The `syncKillSwitchToStorage` trigger runs with admin privileges
- Firestore security rules control who can write to `app_config/status`
- Only authenticated admin users can modify the kill-switch status

## How It Works

1. Admin logs into `admin.html` and changes kill-switch status
2. Admin page writes to Firestore: `app_config/status`
3. `syncKillSwitchToStorage` trigger automatically fires
4. Trigger writes the updated JSON to Cloud Storage
5. Android app can fetch status from either:
   - Cloud Function endpoint: `/appStatus`
   - Cloud Storage file: `app-status.json`

Both endpoints always serve the same data without manual intervention!
