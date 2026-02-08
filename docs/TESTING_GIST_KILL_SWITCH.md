# Testing the GitHub Gist Kill-Switch

## Manual Testing Checklist

Follow these steps to test the GitHub Gist-based kill-switch implementation:

### 1. Create a Test Gist

- [ ] Go to https://gist.github.com/
- [ ] Sign in to your GitHub account
- [ ] Create a new public Gist:
  - Filename: `iskorly-status.json`
  - Content: `{"disabled": false, "message": "Test message"}`
- [ ] Copy the Gist ID from the URL
- [ ] Click "Raw" and copy the raw URL

### 2. Generate a GitHub Token

- [ ] Go to https://github.com/settings/tokens?type=beta
- [ ] Generate a new fine-grained token
- [ ] Grant "Gists" read and write permission
- [ ] Copy the token

### 3. Configure the Admin Page

- [ ] Open `public/admin-gist.html` in a text editor
- [ ] Update the CONFIG section with:
  - ADMIN_PASSWORD: Choose a test password (e.g., "test123")
  - GIST_ID: Your Gist ID from step 1
  - GITHUB_TOKEN: Your token from step 2
  - GIST_FILENAME: "iskorly-status.json"
- [ ] Save the file

### 4. Test the Admin Page

- [ ] Open the configured admin-gist.html in a browser
- [ ] You should see the login screen
- [ ] Enter your admin password
- [ ] Verify you can log in successfully
- [ ] Check that the current status loads from the Gist
- [ ] Try clicking "Disable App"
- [ ] Change the message text
- [ ] Click "Save Changes to Gist"
- [ ] Verify the success message appears
- [ ] Check browser console for any errors (F12)

### 5. Verify Gist Update

- [ ] Go to your Gist on GitHub
- [ ] Click "Revisions" to see the edit history
- [ ] Verify the latest revision shows your changes
- [ ] Check the raw URL in a browser
- [ ] Verify it returns the updated JSON

### 6. Test Android Integration (Optional)

If you have the Android development environment set up:

- [ ] Add the Gist raw URL to `local.properties`:
  ```
  KILL_SWITCH_URL=https://gist.githubusercontent.com/raw/YOUR_GIST_ID/iskorly-status.json
  ```
- [ ] Build the app: `./gradlew assembleDebug`
- [ ] Install on a test device
- [ ] Launch the app
- [ ] If disabled=true in Gist, you should see the kill-switch dialog
- [ ] Check logcat for kill-switch messages: `adb logcat | grep ISA_VISION`

### 7. Test Error Handling

Test that the admin page handles errors gracefully:

- [ ] Try logging in with wrong password (should show error)
- [ ] Temporarily change GIST_ID to invalid value (should show error on load)
- [ ] Temporarily use expired token (should show 401 error)
- [ ] Verify all errors are displayed to the user

### 8. Security Checks

- [ ] Verify CONFIG values have CHANGE_ME placeholders in git
- [ ] Verify configured file is not tracked by git
- [ ] Check that .gitignore includes admin-*-configured.html
- [ ] Verify no secrets are in git history

### 9. Documentation Review

- [ ] Read through GIST_KILL_SWITCH_SETUP.md
- [ ] Verify all links work
- [ ] Check that examples are clear
- [ ] Verify setup steps are accurate

### 10. Clean Up

After testing:

- [ ] Delete test Gist (or keep for future testing)
- [ ] Revoke test GitHub token
- [ ] Remove configured admin page or keep it secure
- [ ] Document any issues found

## Expected Results

### ✅ Admin Page Login
- Password authentication should work
- Current status should load from Gist
- Status badge should reflect disabled/enabled state
- Message should be displayed in preview

### ✅ Admin Page Controls
- Enable/Disable buttons should toggle state
- Message textarea should be editable
- Preview should update as you type
- Save button should update the Gist
- Success message should appear after save

### ✅ Gist Updates
- Changes should appear in Gist revision history
- Raw URL should return updated JSON within ~30 seconds
- JSON format should be preserved (disabled, message fields)

### ✅ Android App (if tested)
- App should fetch status on startup
- If disabled=true, non-cancelable dialog should appear
- Dialog should show the message from Gist
- Status should be cached for offline use

## Common Issues and Solutions

### Issue: "Configuration incomplete" error
**Solution:** Make sure all CONFIG values are updated (remove CHANGE_ME)

### Issue: "GitHub API error: 401"
**Solution:** Token is invalid or expired. Generate a new token.

### Issue: "GitHub API error: 404"
**Solution:** Gist ID is incorrect. Verify the Gist exists and ID is correct.

### Issue: "File not found in Gist"
**Solution:** GIST_FILENAME doesn't match. Check filename in your Gist.

### Issue: Changes not appearing in raw URL
**Solution:** GitHub CDN may cache for up to 60 seconds. Wait and refresh.

### Issue: Android app not fetching status
**Solution:** 
- Verify KILL_SWITCH_URL is set in local.properties
- Check logcat for errors
- Verify raw Gist URL is accessible in browser

## Automated Testing Notes

For future automation, consider:
- Selenium/Playwright tests for admin page UI
- API tests for GitHub Gist endpoints
- Android instrumentation tests for kill-switch dialog
- Integration tests for end-to-end flow

## Report Issues

If you find any bugs or have suggestions:
1. Note the exact steps to reproduce
2. Include error messages from console (F12)
3. Include Android logcat output (if applicable)
4. Create an issue on GitHub with details
