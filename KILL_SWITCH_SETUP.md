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

**Location:** `public/app-status.json`

JSON endpoint that the Android app checks on startup:

```json
{
  "disabled": false,
  "message": "The app is currently enabled and functioning normally."
}
```

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
4. Firebase Realtime Database enabled (for status storage)

### Step 1: Configure Firebase

1. Create a Firebase project at https://console.firebase.google.com
2. Enable Firebase Hosting
3. Enable Firebase Authentication (Email/Password provider)
4. Enable Firebase Realtime Database
5. Update `public/admin.html` with your Firebase configuration:

```javascript
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "YOUR_PROJECT.firebaseapp.com",
    databaseURL: "https://YOUR_PROJECT-default-rtdb.firebaseio.com",
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

### Step 3: Configure Database Rules

In Firebase Console → Realtime Database → Rules:

```json
{
  "rules": {
    "app-status": {
      ".read": true,
      ".write": "auth != null && auth.token.email == 'admin@iskorly.app'"
    }
  }
}
```

### Step 4: Deploy to Firebase Hosting

```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize (if not already done)
firebase init hosting

# Deploy
firebase deploy --only hosting
```

### Step 5: Configure Android App

Add the kill-switch endpoint URL to `local.properties`:

```properties
# Kill-Switch Configuration
KILL_SWITCH_URL=https://YOUR_PROJECT.web.app/app-status.json
```

**Note:** If `KILL_SWITCH_URL` is not set or is blank, the app will skip the kill-switch check and continue normally.

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
3. **Load Status:** Fetches current status from Firebase Realtime Database
4. **Toggle/Edit:** Admin can change enabled/disabled state and message
5. **Save:** Updates Firebase Realtime Database
6. **Analytics:** Logs all actions for monitoring

## Usage

### Enabling the App

1. Go to `https://YOUR_PROJECT.web.app/admin.html`
2. Log in with admin credentials
3. Click "✓ Enable App"
4. (Optional) Update the user message
5. Click "Save Changes"
6. Update `public/app-status.json` manually or via Cloud Functions

### Disabling the App

1. Go to `https://YOUR_PROJECT.web.app/admin.html`
2. Log in with admin credentials
3. Click "✕ Disable App"
4. Enter a message explaining why (e.g., "Undergoing maintenance. Will be back soon!")
5. Click "Save Changes"
6. Update `public/app-status.json` manually or via Cloud Functions

**Important:** Currently, changes in the admin panel update the Realtime Database. To update the static JSON file served to the app, you need to either:
- Manually update `public/app-status.json` and redeploy
- Implement a Cloud Function to sync Database → JSON file

## Security Considerations

✅ **Implemented:**
- Password-protected admin access
- CORS headers for app-status endpoint
- Database rules restrict write access to admin only
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
1. Static JSON file not updated
2. App using cached status
3. User offline when change was made

**Solution:**
- Redeploy Firebase Hosting after updating JSON
- Clear app data to reset cache
- Wait for next app restart when online

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

## Cloud Function (Optional)

To automatically sync Realtime Database changes to the static JSON file:

```javascript
const functions = require('firebase-functions');
const admin = require('firebase-admin');
const fs = require('fs');

exports.syncAppStatus = functions.database.ref('/app-status')
    .onWrite((change, context) => {
        const status = change.after.val();
        const jsonContent = JSON.stringify(status, null, 2);
        
        // Write to hosting directory
        fs.writeFileSync('./public/app-status.json', jsonContent);
        
        // Trigger redeployment or use Cloud Storage
        return null;
    });
```

## Support

For issues or questions, please:
- Check logs: `adb logcat | grep ISA_VISION`
- Review Firebase Console for errors
- Create an issue on GitHub
