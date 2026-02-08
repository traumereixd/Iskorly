# Kill-Switch Setup Guide

## Overview

The Iskorly app now includes a remote kill-switch feature that allows administrators to remotely disable the app with a custom message to users. This is useful for maintenance, emergency situations, or controlled rollouts.

## Components

### 1. Firebase Hosting Admin Page

**Location:** `public/admin.html`

A modern, user-friendly web interface for controlling the app status:

![Admin Page Login](https://github.com/user-attachments/assets/89a682e2-72d5-4415-9f4f-84ae430e1347)

**Features:**
- 🔐 Secure password-based authentication
- 🎨 Modern gradient UI with smooth animations
- ✅ Toggle between Enabled/Disabled states
- 💬 Custom message editor for user notifications
- 📊 Real-time status indicators
- ⚡ Loading states and error handling
- 📈 Firebase Analytics event tracking

**Analytics Events:**
- `login_success` / `login_failure`
- `toggle_change` (enable/disable actions)
- `save_status` (when changes are saved)
- `logout`
- `page_view`

### 2. Kill-Switch Endpoint

**Location:** Firebase Cloud Functions at `functions/index.js`

The kill-switch status is stored in **Firestore** and automatically synced to multiple endpoints:

**Primary Storage:**
- **Firestore Collection:** `app_config`
- **Document:** `status`
- **Fields:** `disabled` (boolean), `message` (string)

**Access Points:**
1. **Cloud Function Endpoint:** `/appStatus` - Serves status directly from Firestore
2. **Cloud Storage File:** `app-status.json` - Auto-synced public file in Storage bucket

**Automatic Sync:**
A Firestore trigger (`syncKillSwitchToStorage`) automatically updates the Cloud Storage file whenever the Firestore document changes. This ensures both endpoints always serve the same data without manual intervention.

**Example Firestore Document:**
```json
{
  "disabled": false,
  "message": "The app is currently enabled and functioning normally."
}
```

**Legacy Hosting File:** `public/app-status.json` remains for backward compatibility but is not automatically updated. For automatic sync, use the Cloud Storage file or Cloud Function endpoint.

**Fields:**
- `disabled` (boolean): If `true`, the app will be blocked
- `message` (string): Custom message displayed to users when app is disabled

### 3. Android Implementation

The Android app checks this endpoint on every app startup and caches the result for offline scenarios.

## Setup Instructions

### Prerequisites

1. Firebase project created
2. Firebase Hosting enabled
3. Firebase Authentication enabled (for admin login)
4. Firebase Firestore enabled (for status storage)
5. Firebase Cloud Functions deployed

### Step 1: Configure Firebase

1. Create a Firebase project at https://console.firebase.google.com
2. Enable Firebase Hosting
3. Enable Firebase Authentication (Email/Password provider)
4. Enable Firebase Firestore
5. Enable Firebase Cloud Functions
6. Update `public/admin.html` with your Firebase configuration (using modular SDK):

```javascript
// Your web app's Firebase configuration
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "YOUR_PROJECT.firebaseapp.com",
    projectId: "YOUR_PROJECT",
    storageBucket: "YOUR_PROJECT.appspot.com",
    messagingSenderId: "YOUR_MESSAGING_ID",
    appId: "YOUR_APP_ID",
    measurementId: "YOUR_MEASUREMENT_ID"
};
```

### Step 2: Create Admin User

In Firebase Console → Authentication → Users:
1. Add a new user with email: `admin@iskorly.app`
2. Set a strong password
3. This account will be used to log into the admin panel

### Step 3: Configure Firestore Security Rules

In Firebase Console → Firestore Database → Rules:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /app_config/status {
      allow read: if true;
      allow write: if request.auth != null && request.auth.token.email == 'admin@iskorly.app';
    }
  }
}
```

### Step 4: Deploy Cloud Functions and Hosting

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize (if not already done)
firebase init functions hosting

# Deploy functions and hosting
firebase deploy
```

### Step 5: Configure Android App

Add the kill-switch endpoint URL to `local.properties`:

```properties
# Kill-Switch Configuration
# Option 1: Use Cloud Function endpoint (recommended - always fresh from Firestore)
KILL_SWITCH_URL=https://YOUR_PROJECT.web.app/appStatus

# Option 2: Use Cloud Storage file (auto-synced by Cloud Function)
# KILL_SWITCH_URL=https://storage.googleapis.com/YOUR_PROJECT.appspot.com/app-status.json
```

**Note:** 
- The Cloud Function serves the status at `/appStatus` directly from Firestore.
- A Firestore trigger (`syncKillSwitchToStorage`) automatically syncs changes to a public Cloud Storage JSON file at `app-status.json` for alternative access.
- If `KILL_SWITCH_URL` is not set or is blank, the app will skip the kill-switch check and continue normally.

### Step 6: Build Android App

```bash
./gradlew assembleRelease
```

## How It Works

### Android App Flow

1. **App Startup:** When `MainActivity.onCreate()` is called
2. **Check Configuration:** Verifies `BuildConfig.KILL_SWITCH_URL` is set
3. **Background Request:** Makes HTTP request to kill-switch endpoint
4. **Parse Response:** Reads `disabled` and `message` fields
5. **Cache Result:** Stores status in `SharedPreferences` for offline use
6. **Show Dialog:** If disabled, displays non-cancelable blocking dialog
7. **Offline Handling:** Uses cached status when network is unavailable

### Admin Page Flow

1. **Login:** Admin enters password
2. **Authentication:** Firebase Auth validates credentials
3. **Load Status:** Fetches current status from Firebase Firestore (`app_config/status` document)
4. **Toggle/Edit:** Admin can change enabled/disabled state and message
5. **Save:** Updates Firestore document
6. **Automatic Sync:** Firestore trigger (`syncKillSwitchToStorage`) automatically syncs to Cloud Storage
7. **Cloud Function:** Automatically serves updated status to Android app via `/appStatus` endpoint
8. **Analytics:** Logs all actions for monitoring

**Note:** Changes made in the admin page are **immediately available** to the Android app through both the Cloud Function endpoint and the auto-synced Cloud Storage file. No manual deployment or file editing required!

## Usage

### Enabling the App

1. Go to `https://YOUR_PROJECT.web.app/admin.html`
2. Log in with admin credentials
3. Click "✓ Enable App"
4. (Optional) Update the user message
5. Click "Save Changes"
6. Changes are **immediately** and **automatically** available to Android app

### Disabling the App

1. Go to `https://YOUR_PROJECT.web.app/admin.html`
2. Log in with admin credentials
3. Click "✕ Disable App"
4. Enter a message explaining why (e.g., "Undergoing maintenance. Will be back soon!")
5. Click "Save Changes"
6. Changes are **immediately** and **automatically** available to Android app

**Automatic Sync:** Changes are saved to Firestore and automatically:
- Served to the Android app via the Cloud Function (`/appStatus` endpoint)
- Synced to Cloud Storage file (`app-status.json`) by the `syncKillSwitchToStorage` trigger
- **No manual file updates or redeployment needed!**


## Security Considerations

✅ **Implemented:**
- Password-protected admin access
- CORS headers for app-status endpoint
- Firestore security rules restrict write access to admin only
- Cloud Function serves status with proper cache control headers
- Client-side caching prevents repeated requests
- Non-cancelable dialog prevents bypass

⚠️ **Recommendations:**
- Use Firebase Security Rules to protect data
- Rotate admin password regularly
- Monitor Firebase Analytics for suspicious activity
- Use HTTPS only (enforced by Firebase Hosting)
- Consider adding 2FA for admin account

## Troubleshooting

### App Not Checking Kill-Switch

**Possible causes:**
1. `KILL_SWITCH_URL` not set in `local.properties`
2. Network connectivity issues
3. Endpoint returning invalid JSON

**Solution:** Check logs with `adb logcat | grep ISA_VISION`

### Admin Page Not Loading

**Possible causes:**
1. Firebase configuration incorrect
2. Firebase Hosting not deployed
3. Network/CORS issues

**Solution:** Check browser console for errors

### Changes Not Taking Effect

**Possible causes:**
1. Cloud Function not deployed
2. Firestore document not updated
3. App using cached status
4. User offline when change was made

**Solution:**
- Deploy Cloud Functions: `firebase deploy --only functions`
- Verify Firestore document in Firebase Console
- Clear app data to reset cache
- Wait for next app startup when online

## Monitoring

View analytics in Firebase Console → Analytics → Events:
- Track admin logins
- Monitor toggle changes
- Audit save operations
- Detect unusual patterns

## Example Messages

**Maintenance:**
```
The app is currently undergoing scheduled maintenance. 
Please try again in a few hours. Thank you for your patience!
```

**Emergency:**
```
Service temporarily unavailable due to technical issues. 
We are working to resolve this as quickly as possible.
```

**Deprecation:**
```
This version of the app is no longer supported. 
Please update to the latest version from the Play Store.
```

## Cloud Function Implementation

The project includes two Cloud Functions at `functions/index.js`:

### 1. HTTP Endpoint: `appStatus`

Serves the kill-switch status directly from Firestore:

```javascript
exports.appStatus = functions.https.onRequest(async (req, res) => {
  try {
    const snap = await admin.firestore()
      .collection("app_config")
      .doc("status")
      .get();

    const data = snap.exists ? snap.data() : { disabled: false, message: "" };

    res.set("Cache-Control", "no-cache, no-store, must-revalidate");
    res.json({
      disabled: !!data.disabled,
      message: data.message || "App temporarily disabled."
    });
  } catch (e) {
    res.status(500).json({ disabled: false, message: "" });
  }
});
```

This function:
- Reads the status from Firestore (`app_config/status`)
- Serves it via HTTP endpoint (`/appStatus`)
- Sets cache control headers to prevent caching
- Provides fallback values on error

### 2. Firestore Trigger: `syncKillSwitchToStorage`

Automatically syncs Firestore changes to Cloud Storage:

```javascript
exports.syncKillSwitchToStorage = functions.firestore
  .document("app_config/status")
  .onWrite(async (change, context) => {
    // Reads the updated status from Firestore
    // Writes it to a public Cloud Storage file: app-status.json
    // Makes the file publicly accessible
  });
```

This trigger:
- Activates whenever `app_config/status` is created, updated, or deleted
- Creates/updates a public JSON file in Cloud Storage bucket
- Makes the file accessible at: `https://storage.googleapis.com/{bucket}/app-status.json`
- Ensures the static file is always in sync with Firestore
- Runs automatically without manual intervention

**Result:** Admin page changes propagate automatically to both the Cloud Function endpoint and the Cloud Storage file, eliminating the need for manual file updates or redeployment.

## Support

For issues or questions, please:
- Check logs: `adb logcat | grep ISA_VISION`
- Review Firebase Console for errors
- Create an issue on GitHub
