# GitHub Gist Kill-Switch Setup Guide

## Overview

The Iskorly app includes a remote kill-switch feature that allows administrators to remotely disable the app with a custom message to users. This guide describes the **GitHub Gist-based implementation**, which is free and doesn't require Firebase.

## Architecture

The new kill-switch system uses:
- **GitHub Gist**: Free, public JSON store for the kill-switch status
- **Admin Web Page**: Simple HTML/JavaScript page with password authentication
- **GitHub API**: For reading and updating the Gist
- **Android App**: Reads the public Gist URL on startup

## Components

### 1. GitHub Gist (JSON Store)

A public GitHub Gist containing a single JSON file with the kill-switch status.

**File Format:** `iskorly-status.json`
```json
{
  "disabled": false,
  "message": "The app is currently enabled and functioning normally."
}
```

**Fields:**
- `disabled` (boolean): If `true`, the app will be blocked
- `message` (string): Custom message displayed to users when app is disabled

### 2. Admin Web Page

**Location:** `public/admin-gist.html`

A self-contained HTML page that:
- ✅ Authenticates with a simple password (client-side)
- ✅ Fetches current status from the Gist
- ✅ Allows toggling between Enabled/Disabled states
- ✅ Updates the message shown to users
- ✅ Saves changes directly to the Gist via GitHub API
- ✅ No server-side components required

### 3. Android App Integration

The Android app checks the public Gist URL on every startup and caches the result for offline scenarios.

## Setup Instructions

### Step 1: Create a GitHub Gist

1. Go to https://gist.github.com/
2. Sign in to your GitHub account
3. Create a new Gist with:
   - **Description:** "Iskorly App Kill-Switch Status"
   - **Filename:** `iskorly-status.json`
   - **Content:**
   ```json
   {
     "disabled": false,
     "message": "The app is currently enabled and functioning normally."
   }
   ```
4. Make sure the Gist is **Public** (not Secret)
5. Click "Create public gist"
6. Copy the **Gist ID** from the URL (e.g., if the URL is `https://gist.github.com/username/abc123def456`, the Gist ID is `abc123def456`)

### Step 2: Generate a GitHub Personal Access Token

You need a fine-grained Personal Access Token (PAT) with permission to edit your Gist.

1. Go to https://github.com/settings/tokens?type=beta
2. Click "Generate new token" (fine-grained token)
3. Configure the token:
   - **Token name:** `iskorly-admin-gist-access`
   - **Expiration:** 90 days (or custom, remember to renew)
   - **Repository access:** No repository access needed
   - **Account permissions:**
     - Gists: **Read and write**
4. Click "Generate token"
5. **IMPORTANT:** Copy the token immediately (it won't be shown again)
6. Store the token securely (you'll need it for the admin page)

### Step 3: Configure the Admin Page

1. Open `public/admin-gist.html` in a text editor
2. Find the `CONFIG` object near the top of the `<script>` section
3. Update the configuration values:

```javascript
const CONFIG = {
    // Choose a strong admin password
    ADMIN_PASSWORD: 'your-secure-admin-password-here',
    
    // Your Gist ID from Step 1
    GIST_ID: 'abc123def456',
    
    // Your GitHub token from Step 2
    GITHUB_TOKEN: 'github_pat_11AAAAAA...',
    
    // Gist filename (should match the filename from Step 1)
    GIST_FILENAME: 'iskorly-status.json'
};
```

**Security Notes:**
- ⚠️ **DO NOT commit this file with real credentials to a public repository**
- Consider serving the admin page from a private location or implementing server-side authentication
- The password is client-side only and provides basic protection
- Keep your GitHub token secure - it has write access to your Gists
- Use a `.gitignore` entry or environment variables for production deployments

### Step 4: Deploy the Admin Page

You have several options for deploying the admin page:

#### Option A: Firebase Hosting (if already set up)
```bash
# The file is already in public/ directory
firebase deploy --only hosting
# Access at: https://your-project.web.app/admin-gist.html
```

#### Option B: GitHub Pages
```bash
# Copy admin-gist.html to your GitHub Pages repository
# Access at: https://username.github.io/repo/admin-gist.html
```

#### Option C: Local Development
```bash
# Just open the file in a browser
# File path: file:///path/to/Iskorly/public/admin-gist.html
```

#### Option D: Simple HTTP Server
```bash
cd public
python3 -m http.server 8000
# Access at: http://localhost:8000/admin-gist.html
```

### Step 5: Configure Android App

Add the Gist URL to `local.properties`:

```properties
# GitHub Gist Kill-Switch Configuration
# Get the raw URL by clicking "Raw" button on your Gist page
# Format: https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/FILENAME
# Example (without username - also works):
KILL_SWITCH_URL=https://gist.githubusercontent.com/abc123def456/raw/iskorly-status.json
```

**Important:**
- Use the **raw Gist URL** (gist.githubusercontent.com, not gist.github.com)
- The URL format is: `https://gist.githubusercontent.com/{GIST_ID}/raw/{FILENAME}`
- Or with username: `https://gist.githubusercontent.com/{USERNAME}/{GIST_ID}/raw/{FILENAME}`
- You can find this URL by:
  1. Opening your Gist on github.com
  2. Clicking the "Raw" button
  3. Copying the URL from your browser

### Step 6: Build and Test Android App

```bash
./gradlew assembleRelease
```

## How It Works

### Android App Flow

1. **App Startup:** When `MainActivity.onCreate()` is called
2. **Check Configuration:** Verifies `BuildConfig.KILL_SWITCH_URL` is set
3. **Background Request:** Makes HTTP GET request to the public Gist URL
4. **Parse Response:** Reads `disabled` and `message` fields from JSON
5. **Cache Result:** Stores status in `SharedPreferences` for offline use
6. **Show Dialog:** If disabled, displays non-cancelable blocking dialog
7. **Offline Handling:** Uses cached status when network is unavailable

### Admin Page Flow

1. **Login:** Admin enters password (simple client-side check)
2. **Load Status:** Fetches current status from Gist via GitHub API
3. **Toggle/Edit:** Admin can change enabled/disabled state and message
4. **Save:** Updates Gist via GitHub API PATCH request
5. **Propagation:** Changes are immediately available at the public raw URL

## Usage Examples

### Enabling the App

1. Go to your deployed admin page (e.g., `https://your-site.com/admin-gist.html`)
2. Enter the admin password
3. Click "✓ Enable App"
4. (Optional) Update the user message
5. Click "Save Changes to Gist"
6. Changes are immediately available to the Android app

### Disabling the App

1. Go to your deployed admin page
2. Enter the admin password
3. Click "✕ Disable App"
4. Enter a message explaining why (e.g., "Undergoing maintenance. Will be back soon!")
5. Click "Save Changes to Gist"
6. Changes are immediately available to the Android app

**Note:** GitHub's raw Gist CDN may cache for a short time (usually less than 60 seconds). Most apps will see the change on their next startup.

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

## Security Considerations

### Implemented Security Features
✅ Password-protected admin access (client-side)
✅ HTTPS-only Gist access
✅ Fine-grained GitHub token with minimal permissions (Gist read/write only)
✅ Public read-only access for Android app (no authentication needed)
✅ Client-side caching prevents repeated requests
✅ Non-cancelable dialog prevents bypass in Android app

### Security Recommendations
⚠️ **Important:**
- **DO NOT** commit the configured admin page with real credentials to public repositories
- Use a strong, unique admin password
- Rotate the GitHub token periodically (set expiration)
- Keep the GitHub token secure - treat it like a password
- Consider implementing server-side authentication for production
- Monitor your Gist's edit history on GitHub for unauthorized changes
- Use HTTPS for hosting the admin page
- Consider IP-based access restrictions for the admin page

### What's Protected vs. Not Protected

**Protected:**
- ✅ Admin page requires password to access controls
- ✅ GitHub token required to modify the Gist
- ✅ GitHub provides audit log of all Gist changes

**Not Protected (by design):**
- ⚠️ The kill-switch status JSON is publicly readable (required for Android app)
- ⚠️ Client-side password is visible in HTML source (use server-side auth for production)
- ⚠️ Anyone with the Gist URL can read the current status

This design is intentional - the Android app needs to read the status without authentication. The GitHub token protects write access.

## Troubleshooting

### App Not Checking Kill-Switch

**Possible causes:**
1. `KILL_SWITCH_URL` not set in `local.properties`
2. Network connectivity issues
3. Gist URL incorrect
4. Gist returns invalid JSON

**Solution:**
- Check logs with `adb logcat | grep ISA_VISION`
- Verify the Gist URL in a browser
- Check that the Gist is Public
- Ensure JSON format is correct

### Admin Page Not Loading

**Possible causes:**
1. CONFIG values not set correctly
2. Gist ID or GitHub token invalid
3. Network/CORS issues
4. Token expired or lacks permissions

**Solution:**
- Check browser console for errors (F12)
- Verify Gist ID in browser: `https://gist.github.com/{your-gist-id}`
- Verify token has Gist read/write permission
- Regenerate token if expired

### Changes Not Taking Effect

**Possible causes:**
1. GitHub CDN caching (usually < 60 seconds)
2. Android app using cached offline status
3. Wrong Gist URL in Android app
4. Gist update failed

**Solution:**
- Wait 1-2 minutes for CDN propagation
- Check the Gist on github.com to verify changes were saved
- Check admin page console for errors
- Verify Android app uses the correct raw Gist URL
- Clear Android app data to reset cache
- Restart the app to fetch fresh status

### GitHub API Errors

**Error: 401 Unauthorized**
- Token is invalid or expired
- Regenerate token and update admin page

**Error: 404 Not Found**
- Gist ID is incorrect
- Gist was deleted
- Verify Gist ID in admin page config

**Error: 403 Forbidden**
- Token lacks gist write permission
- Rate limit exceeded (60 requests/hour for unauthenticated, 5000/hour for authenticated)

## Monitoring

### Via GitHub
- View Gist edit history: Go to your Gist → Click "Revisions"
- Check all changes, who made them, and when
- Revert to previous versions if needed

### Via Admin Page
- Admin page shows current status on load
- Console logs all save operations
- Success/error messages displayed to admin

### Via Android App
- Check logs: `adb logcat | grep ISA_VISION`
- Logs show kill-switch check results
- Shows cached vs. fresh status

## Comparison: Gist vs. Firebase

| Feature | GitHub Gist | Firebase |
|---------|-------------|----------|
| Cost | Free | Free tier, may incur costs at scale |
| Setup Complexity | Low (just create Gist) | Medium (multiple services to configure) |
| Authentication | GitHub token + client password | Firebase Auth |
| Server Required | No | Yes (Cloud Functions) |
| Admin Page | Single HTML file | HTML + Firebase SDK |
| Android Access | Simple HTTP GET | HTTP GET (Cloud Function or Storage) |
| Latency | Low (GitHub CDN) | Low (Firebase CDN) |
| Reliability | High (GitHub SLA) | High (Firebase SLA) |
| Monitoring | GitHub edit history | Firebase Console + Analytics |

## Migration from Firebase

If you're migrating from the Firebase-based kill-switch:

1. **Create Gist** as described in Step 1
2. **Copy current status** from Firebase to the Gist
3. **Update Android app** with new Gist URL in `local.properties`
4. **Deploy new admin page** (`admin-gist.html`)
5. **Test** that the new system works
6. **Optional:** Keep Firebase admin page as backup during transition
7. **Optional:** Remove Firebase Cloud Functions if no longer needed

## Support

For issues or questions:
- Check logs: `adb logcat | grep ISA_VISION`
- Review browser console (F12) for admin page errors
- Verify Gist configuration and permissions
- Check GitHub token hasn't expired
- Create an issue on GitHub

## Advanced: Environment-Based Configuration

For production deployments, consider using environment variables or a build system:

### Using Environment Variables (Node.js build)

```javascript
const CONFIG = {
    ADMIN_PASSWORD: process.env.ADMIN_PASSWORD || 'default-password',
    GIST_ID: process.env.GIST_ID || '',
    GITHUB_TOKEN: process.env.GITHUB_TOKEN || '',
    GIST_FILENAME: 'iskorly-status.json'
};
```

### Using Firebase Hosting + Cloud Functions

You can keep the admin page simple and move secrets to a Cloud Function:

1. Create a Cloud Function that proxies Gist updates
2. Use Firebase Authentication for admin login
3. Admin page calls the Cloud Function (which has the GitHub token)
4. Android app still reads the public Gist directly

This keeps secrets server-side while maintaining the free Gist storage.

## Appendix: Complete Example

### Example Gist URL Structure

```
Gist Page:    https://gist.github.com/username/abc123def456
Raw URL:      https://gist.githubusercontent.com/abc123def456/raw/iskorly-status.json
              OR
              https://gist.githubusercontent.com/username/abc123def456/raw/iskorly-status.json
```

Both raw URL formats work. The simpler format (without username) is shown above.

### Example local.properties

```properties
# Azure Vision OCR (existing)
AZURE_VISION_KEY=your-azure-key-here
AZURE_VISION_ENDPOINT=https://your-region.api.cognitive.microsoft.com/

# Kill-Switch URL (updated for Gist)
# Get the raw URL by clicking "Raw" on your Gist page
# Format: https://gist.githubusercontent.com/GIST_ID/raw/FILENAME
# Or with username: https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/FILENAME
KILL_SWITCH_URL=https://gist.githubusercontent.com/abc123def456/raw/iskorly-status.json

# Optional: AI re-parser endpoint
REPARSE_ENDPOINT=https://your-ai-endpoint.com/reparse
```

### Example Status JSON

**Enabled:**
```json
{
  "disabled": false,
  "message": "App is running normally. All features available."
}
```

**Disabled:**
```json
{
  "disabled": true,
  "message": "Undergoing scheduled maintenance. Expected completion: 3:00 PM EST. Thank you for your patience!"
}
```
