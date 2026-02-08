# Deployment Instructions for Kill-Switch Cloud Functions

This guide explains how to deploy the Cloud Functions that enable automatic sync of kill-switch status.

## Prerequisites

1. Firebase CLI installed: `npm install -g firebase-tools`
2. Firebase project initialized
3. Logged into Firebase: `firebase login`

## One-Time Setup

### 1. Install Function Dependencies

```bash
cd functions
npm install
```

### 2. Configure Storage Bucket CORS (if needed)

To allow the Android app to access the Cloud Storage file directly, you may need to configure CORS:

Create a file `cors.json`:
```json
[
  {
    "origin": ["*"],
    "method": ["GET"],
    "maxAgeSeconds": 3600
  }
]
```

Apply CORS configuration:
```bash
gsutil cors set cors.json gs://YOUR_PROJECT.appspot.com
```

Replace `YOUR_PROJECT` with your Firebase project ID.

## Deployment

### Deploy Everything (Functions + Hosting)

```bash
firebase deploy
```

### Deploy Only Functions

```bash
firebase deploy --only functions
```

### Deploy a Specific Function

```bash
# Deploy just the HTTP endpoint
firebase deploy --only functions:appStatus

# Deploy just the Firestore trigger
firebase deploy --only functions:syncKillSwitchToStorage
```

## Verification

### 1. Check Function Deployment

After deployment, you should see output like:
```
✔  functions[appStatus(us-central1)] Successful create operation.
✔  functions[syncKillSwitchToStorage(us-central1)] Successful create operation.
```

### 2. Test the HTTP Endpoint

```bash
curl https://YOUR_PROJECT.web.app/appStatus
```

Expected response:
```json
{
  "disabled": false,
  "message": "App is currently enabled and functioning normally."
}
```

### 3. Test the Firestore Trigger

1. Go to Firebase Console → Firestore Database
2. Navigate to `app_config/status` document
3. Update the `disabled` or `message` field
4. Check Cloud Storage bucket for `app-status.json`
5. Verify the file is publicly accessible:
   ```bash
   curl https://storage.googleapis.com/YOUR_PROJECT.appspot.com/app-status.json
   ```

### 4. Check Function Logs

```bash
firebase functions:log
```

Or view in Firebase Console:
- Functions → Dashboard → Select function → Logs tab

## Updating Functions

When you make changes to `functions/index.js`:

1. Test locally if possible:
   ```bash
   cd functions
   npm run serve
   ```

2. Deploy the updated functions:
   ```bash
   firebase deploy --only functions
   ```

3. Verify the changes in Firebase Console

## Troubleshooting

### Function Deployment Fails

**Error:** "Billing account not configured"
- **Solution:** Enable billing on your Firebase project
- Go to Firebase Console → Project Settings → Usage and Billing

**Error:** "Permission denied"
- **Solution:** Check IAM permissions for your account
- Required roles: Cloud Functions Developer, Firestore User, Storage Admin

### Trigger Not Firing

**Issue:** Changes to Firestore don't trigger the sync function

**Solutions:**
1. Check function logs for errors: `firebase functions:log`
2. Verify the function is deployed: `firebase functions:list`
3. Check the document path is exactly `app_config/status`
4. Ensure Firestore is in the same project as the function

### Storage File Not Public

**Issue:** Can't access the Storage file via public URL

**Solutions:**
1. Check the function logs to see if `makePublic()` succeeded
2. Verify Storage bucket permissions in Firebase Console
3. Manually make the file public in Storage Console if needed
4. Ensure the Storage bucket exists and is accessible

### CORS Errors

**Issue:** Browser can't access Storage file due to CORS

**Solution:** Configure CORS on the Storage bucket (see One-Time Setup section)

## Cost Considerations

**Cloud Functions Pricing:**
- Free tier: 2M invocations/month
- After free tier: $0.40 per million invocations

**Cloud Storage Pricing:**
- Free tier: 5GB storage, 1GB download/day
- After free tier: $0.026/GB storage, $0.12/GB download

For a typical kill-switch use case with minimal changes and app checks, costs should remain in the free tier.

## Security Notes

1. The `appStatus` HTTP function is **public** (required for Android app)
2. The `syncKillSwitchToStorage` trigger runs with **admin privileges**
3. Control who can modify the status via Firestore security rules
4. The Cloud Storage file is **public** for read access
5. Only authenticated admin users can write to `app_config/status`

## Next Steps

After deployment:
1. Update `KILL_SWITCH_URL` in `local.properties` with your function URL
2. Test the kill-switch in the admin panel
3. Verify the Android app receives the status correctly
4. Set up monitoring and alerts for function errors
