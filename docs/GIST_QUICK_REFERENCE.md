# GitHub Gist Kill-Switch Quick Reference

## 🎯 Quick Setup (5 minutes)

### 1. Create Gist
```
1. Go to: https://gist.github.com/
2. Filename: iskorly-status.json
3. Content: {"disabled": false, "message": "App is running"}
4. Create public gist
5. Copy Gist ID from URL
```

### 2. Generate Token
```
1. Go to: https://github.com/settings/tokens?type=beta
2. Generate new token
3. Permission: Gists → Read and Write
4. Copy token (shown only once!)
```

### 3. Configure Admin Page
```javascript
// Edit public/admin-gist.html
const CONFIG = {
    ADMIN_PASSWORD: 'your-password',
    GIST_ID: 'your-gist-id',
    GITHUB_TOKEN: 'github_pat_...',
    GIST_FILENAME: 'iskorly-status.json'
};
```

### 4. Configure Android App
```properties
# Edit local.properties
# Get the raw URL by clicking "Raw" on your Gist
KILL_SWITCH_URL=https://gist.githubusercontent.com/GIST_ID/raw/iskorly-status.json
```

### 5. Deploy & Test
```bash
# Deploy admin page (choose one):
firebase deploy --only hosting        # Option A: Firebase
# or just open file:///.../admin-gist.html  # Option B: Local

# Build Android app:
./gradlew assembleRelease
```

## 📋 JSON Format

```json
{
  "disabled": false,
  "message": "Your message to users"
}
```

## 🔗 Important URLs

- **Gist URL:** `https://gist.github.com/username/GIST_ID`
- **Raw URL:** `https://gist.githubusercontent.com/GIST_ID/raw/FILENAME` (simpler)
- **Raw URL (alt):** `https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/FILENAME` (with username)
- **Token Settings:** `https://github.com/settings/tokens?type=beta`
- **Admin Page:** `file:///path/to/admin-gist.html` or your hosting URL

**Tip:** Click "Raw" on your Gist page to get the exact raw URL for your setup.

## 🔑 Required Permissions

### GitHub Token
- ✅ Gists: Read and Write
- ❌ Repository access: None
- ❌ Other permissions: None

## ⚙️ Configuration Checklist

- [ ] Gist created and public
- [ ] GitHub token generated with Gist permissions
- [ ] Admin page CONFIG updated (no CHANGE_ME values)
- [ ] Android app local.properties updated
- [ ] Admin page tested (login, load, save)
- [ ] Gist updates verified in browser
- [ ] Android app tested (optional)
- [ ] Configured files NOT committed to git

## 🚨 Common Mistakes

1. ❌ Using secret Gist instead of public → App can't read it
2. ❌ Using Gist URL instead of raw URL → JSON parsing fails
3. ❌ Token without Gist permissions → 403 errors
4. ❌ Wrong GIST_FILENAME → File not found errors
5. ❌ Committing configured admin page → Security risk!

## 🛠️ Troubleshooting

| Problem | Solution |
|---------|----------|
| "Configuration incomplete" | Update all CONFIG values |
| "Invalid password" | Check ADMIN_PASSWORD matches |
| "GitHub API error: 401" | Token expired, regenerate |
| "GitHub API error: 404" | Wrong GIST_ID |
| "File not found" | GIST_FILENAME mismatch |
| Changes not visible | Wait 30-60s for CDN |
| Android app ignores | Check KILL_SWITCH_URL in local.properties |

## 📖 Full Documentation

- **Complete Setup:** [GIST_KILL_SWITCH_SETUP.md](../GIST_KILL_SWITCH_SETUP.md)
- **Configuration:** [ADMIN_PAGE_CONFIG.md](ADMIN_PAGE_CONFIG.md)
- **Comparison:** [KILL_SWITCH_COMPARISON.md](KILL_SWITCH_COMPARISON.md)
- **Testing:** [TESTING_GIST_KILL_SWITCH.md](TESTING_GIST_KILL_SWITCH.md)

## 🔒 Security Best Practices

1. ✅ Use strong admin password (12+ characters)
2. ✅ Rotate GitHub token every 90 days
3. ✅ Never commit configured admin page
4. ✅ Deploy admin page to private/protected location
5. ✅ Monitor Gist revision history for changes
6. ⚠️ Remember: JSON is publicly readable (by design)

## 💡 Pro Tips

- Set token expiration to 90 days and set a calendar reminder to renew
- Keep a backup copy of your Gist ID and filename
- Test changes with `disabled: false` before setting to `true`
- Use descriptive messages that tell users when to check back
- Bookmark your Gist URL for quick access

## 📞 Support

- Issues: Create an issue on GitHub
- Questions: Check the full documentation
- Security: See [SECURITY.md](../project-docs/SECURITY.md)
