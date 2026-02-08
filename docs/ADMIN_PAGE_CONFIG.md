# Admin Page Configuration Guide

## Overview

This file is a template for configuring the GitHub Gist-based admin page (`admin-gist.html`). 

**⚠️ IMPORTANT: DO NOT commit this file with real credentials to a public repository!**

## Configuration Steps

1. Copy `admin-gist.html` to a secure location (or deploy it privately)
2. Open the file in a text editor
3. Find the `CONFIG` object in the `<script>` section
4. Replace the placeholder values with your actual configuration

## Configuration Template

```javascript
const CONFIG = {
    // Set a strong password for admin access
    // This is client-side only, so consider server-side auth for production
    ADMIN_PASSWORD: 'CHANGE_ME_ADMIN_PASSWORD',
    
    // Your GitHub Gist ID
    // Get this from your Gist URL: https://gist.github.com/username/GIST_ID
    GIST_ID: 'CHANGE_ME_GIST_ID',
    
    // GitHub fine-grained Personal Access Token
    // Generate at: https://github.com/settings/tokens?type=beta
    // Required permission: Gists -> Read and Write
    GITHUB_TOKEN: 'CHANGE_ME_GITHUB_TOKEN',
    
    // Gist filename (must match the filename in your Gist)
    GIST_FILENAME: 'iskorly-status.json'
};
```

## How to Get Your Gist ID

1. Go to https://gist.github.com/
2. Create a new public Gist with:
   - Filename: `iskorly-status.json`
   - Content: `{"disabled": false, "message": "App is running normally"}`
3. After creation, look at the URL: `https://gist.github.com/username/abc123def456`
4. The Gist ID is: `abc123def456`

## How to Generate a GitHub Token

1. Go to https://github.com/settings/tokens?type=beta
2. Click "Generate new token"
3. Configure:
   - Token name: `iskorly-admin-gist-access`
   - Expiration: 90 days (or custom)
   - Repository access: None
   - Account permissions: Gists -> Read and Write
4. Generate and copy the token (it won't be shown again!)

## Example Configuration

```javascript
const CONFIG = {
    ADMIN_PASSWORD: 'MySecurePass123!',
    GIST_ID: 'a1b2c3d4e5f6g7h8i9j0',
    GITHUB_TOKEN: 'github_pat_11AAAAAA0123456789...',
    GIST_FILENAME: 'iskorly-status.json'
};
```

## Security Best Practices

1. **Never commit configured files** - Add to `.gitignore` if needed
2. **Use strong passwords** - At least 12 characters with mixed case, numbers, and symbols
3. **Rotate tokens regularly** - Set expiration and renew periodically
4. **Limit token scope** - Only grant Gist read/write permission
5. **Deploy securely** - Consider hosting admin page on a private server or behind authentication
6. **Monitor access** - Check Gist revision history for unauthorized changes

## For Production Deployment

Consider these alternatives for production:

### Option 1: Environment Variables

Use a build script to inject config from environment variables:

```bash
export ADMIN_PASSWORD="..."
export GIST_ID="..."
export GITHUB_TOKEN="..."
node build-admin-page.js
```

### Option 2: Server-Side Proxy

Keep secrets server-side:
1. Create a backend endpoint that has the GitHub token
2. Admin page authenticates with your backend
3. Backend proxies requests to GitHub API
4. Admin page never sees the GitHub token

### Option 3: Firebase (Hybrid)

Combine Gist storage with Firebase auth:
1. Use Firebase Authentication for admin login
2. Use Cloud Functions to proxy Gist updates (token stored in Functions)
3. Android app still reads public Gist directly

## Testing Your Configuration

1. Open the configured admin page in a browser
2. Enter your admin password
3. You should see the current Gist status loaded
4. Try toggling the status and saving
5. Check your Gist on GitHub to verify the update

## Troubleshooting

**"Configuration incomplete" error:**
- One or more CONFIG values still has CHANGE_ME prefix
- Update all four values

**"Invalid password" error:**
- Entered password doesn't match CONFIG.ADMIN_PASSWORD
- Check for typos

**"GitHub API error: 401" error:**
- Token is invalid or expired
- Regenerate token and update config

**"GitHub API error: 404" error:**
- Gist ID is incorrect
- Verify the Gist exists and ID is correct

**"File not found in Gist" error:**
- GIST_FILENAME doesn't match the actual filename in your Gist
- Check filename in Gist matches exactly (case-sensitive)

## Support

See the full setup guide: `GIST_KILL_SWITCH_SETUP.md`
