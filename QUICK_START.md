# Iskorly UX 2.0 - Quick Start Guide

## What Changed

### 🔧 Crop Reliability (Critical Fix)
**Problem**: Persistent "Crop not available" errors after capture  
**Solution**: 
- Runtime validation of `file_paths.xml`
- Proper bitmap lifecycle (flush before recycle)
- Enhanced CROP_FIX logging throughout pipeline
- Simplified URI permission handling

### �� Visual Refresh
- **New Colors**: Deep Indigo (#4452A6) + Emerald (#21A179)
- **New Font**: Inter (Google Fonts)
- **Splash Screen**: 800ms branded intro
- **Main Menu**: Card-based navigation with gradient background

## Quick Testing

### 1. Install & Launch
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.bandecoot.itemscoreanalysisprogram/.SplashActivity
```

### 2. Verify Splash → Menu
- Cold start should show indigo splash with logo
- After 800ms, transitions to main menu
- Menu shows 4 cards + 2 buttons

### 3. Test Crop (Critical)
Execute 5 times:
1. Tap "Start Scan" card
2. Grant camera permission if needed
3. Tap "Scan" button
4. Crop UI should appear
5. Adjust crop, tap checkmark
6. Wait for OCR processing

**Success**: All 5 attempts show crop UI without errors  
**Failure**: Any "Crop not available" toast = BUG

### 4. Check Logs
```bash
adb logcat -s UX2_CHECK CROP_FIX
```

Expected output:
```
I/UX2_CHECK: UX 2.0 Features:
I/UX2_CHECK: - uCrop: Enabled
I/UX2_CHECK: - FileProvider paths: OK

D/CROP_FIX: file_paths.xml resource verified: [id]
D/CROP_FIX: Capture triggered, session active: true
D/CROP_FIX: Bitmap decoded: 1280x720
D/CROP_FIX: File size after save: [bytes]
```

## Navigation Flow

```
Splash (800ms)
  ↓
Main Menu
  ├─ Start Scan → MainActivity (scan mode)
  ├─ History → MainActivity (history view)
  ├─ Masterlist → MainActivity (masterlist view)
  ├─ Answer Key Setup → MainActivity (setup view)
  ├─ Tutorial → Dialog
  └─ Credits → Dialog
```

## Files Modified (Key Ones)

### Java
- `UiConfig.java` - NEW (config constants)
- `SplashActivity.java` - NEW (launcher)
- `MainActivity.java` - Crop fixes + navigation + colors
- `MainMenuActivity.java` - Redesigned navigation

### Resources
- `activity_splash.xml` - NEW (splash layout)
- `activity_main_menu.xml` - Redesigned (cards)
- `ic_iskorly_mark.xml` - NEW (logo)
- `bg_main_menu_gradient.xml` - NEW (gradient)
- `font/inter.xml` - NEW (typography)
- `colors.xml` - New palette
- `themes.xml` - Theme.Iskorly

### Config
- `AndroidManifest.xml` - Splash as launcher

## Troubleshooting

### Build Fails
**Cause**: jitpack.io network issue (environment)  
**Solution**: Not a code issue; build on local machine with internet

### Crop Still Fails
**Check**:
1. Logcat for CROP_FIX entries - is file_paths.xml found?
2. Does app have CAMERA permission?
3. Is storage available (check cache dir)?

### UI Looks Wrong
**Check**:
1. Inter font may load slowly on first launch
2. Clear app data and restart
3. Check device API level (min 23 required)

## Configuration

### Disable uCrop (for testing)
Edit `UiConfig.java`:
```java
public static final boolean ENABLE_UCROP = false;
```

### Change Splash Duration
Edit `UiConfig.java`:
```java
public static final int SPLASH_DELAY_MS = 1200; // 1.2 seconds
```

## Documentation

- `MANUAL_TESTING_CHECKLIST.md` - Complete test scenarios
- `UX2_IMPLEMENTATION_SUMMARY.md` - Technical details
- `VISUAL_DESIGN_SPEC.md` - Design specifications

## Success Criteria

✅ Splash screen displays correctly  
✅ Menu cards navigate to correct views  
✅ 5 consecutive crop operations succeed  
✅ Answer highlights use new colors  
✅ Inter font visible  
✅ All core features still work  

## Support

For issues:
1. Check logcat (CROP_FIX, UX2_CHECK tags)
2. Review MANUAL_TESTING_CHECKLIST.md
3. Capture screenshots + logs
4. Report via GitHub Issues

---

**Version**: 1.3  
**Status**: Ready for device testing  
**Last Updated**: Implementation complete
