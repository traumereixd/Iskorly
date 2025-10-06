# Iskorly UX 2.0 Manual Testing Checklist

## Prerequisites
- Android device or emulator (API 23+)
- Valid GCLOUD_VISION_API_KEY in local.properties
- Camera permission granted

## Phase 1: Splash & Navigation
- [ ] Cold start shows Iskorly splash screen (no white flash)
- [ ] Splash transitions smoothly to main menu after 800ms
- [ ] Main menu displays with gradient background
- [ ] Iskorly logo and tagline visible
- [ ] All navigation cards visible (Start Scan, History, Masterlist, Answer Key Setup)
- [ ] Tutorial and Credits buttons visible

## Phase 2: Navigation Routes
- [ ] "Start Scan" card → opens MainActivity in scan view
- [ ] "History" card → opens MainActivity with history view
- [ ] "Masterlist" card → opens MainActivity with masterlist view
- [ ] "Answer Key Setup" card → opens MainActivity with setup view
- [ ] "Tutorial" button → shows tutorial dialog
- [ ] "Credits" button → shows credits dialog
- [ ] Back button from MainActivity → returns to main menu
- [ ] Double back press from main menu → confirms exit

## Phase 3: Crop Reliability (CRITICAL)
- [ ] Test 1: Capture → Crop UI appears → Confirm crop → OCR runs
- [ ] Test 2: Capture → Crop UI appears → Confirm crop → OCR runs
- [ ] Test 3: Capture → Crop UI appears → Confirm crop → OCR runs
- [ ] Test 4: Capture → Crop UI appears → Confirm crop → OCR runs
- [ ] Test 5: Capture → Crop UI appears → Confirm crop → OCR runs
- [ ] Cancel crop → Returns to camera without freezing
- [ ] Check logcat for CROP_FIX and UX2_CHECK logs

## Phase 4: Visual/Theme Verification
- [ ] Primary buttons use indigo color (#4452A6)
- [ ] Main menu gradient visible (indigo tones)
- [ ] Answer highlights after scoring:
  - Correct answers: light green background
  - Incorrect answers: light red background
  - Blank answers: light yellow background
- [ ] Inter font loaded (check text appearance)
- [ ] No layout clipping on small screen (test on 5" device/emulator)

## Phase 5: Core Features (Non-Regression)
- [ ] Answer Key: Add/Remove/Clear answers
- [ ] Answer Key: Slot management (New/Rename/Delete/Import/Export)
- [ ] Single scan: Capture → Crop → OCR → Edit → Score → Save to history
- [ ] Batch import: Import 3+ photos → All processed → Merged results
- [ ] History: View past results, grouped by section/exam
- [ ] Masterlist: Per-question statistics displayed
- [ ] CSV Export: Export history to CSV file
- [ ] Orientation change during scan: No crash

## Phase 6: Memory & Performance
- [ ] 5 consecutive scans: No OOM error
- [ ] Check logcat for GC thrashing warnings
- [ ] App responsive during OCR processing
- [ ] No visible memory leaks (use Android Profiler if available)

## Expected Log Entries
Look for these in logcat after cold start:
```
I/UX2_CHECK: UX 2.0 Features:
I/UX2_CHECK: - Splash Screen: Enabled
I/UX2_CHECK: - Main Menu: Redesigned
I/UX2_CHECK: - Color Palette: Indigo + Emerald
I/UX2_CHECK: - Typography: Inter font
I/UX2_CHECK: - uCrop: Enabled
I/UX2_CHECK: - FileProvider paths: OK
```

After first capture:
```
D/CROP_FIX: Capture triggered, session active: true
D/CROP_FIX: Bitmap decoded: 1280x720
D/CROP_FIX: File size after save: XXXXX bytes
D/CROP_FIX: URI authority: com.bandecoot.itemscoreanalysisprogram.fileprovider
D/CROP_FIX: file_paths.xml resource verified: XXXX
D/CROP_FIX: Source file exists: true
D/CROP_FIX: Created destUri: content://...
```

## Known Issues / Expected Behavior
- Build may fail in CI due to jitpack.io network issues (environment limitation)
- First scan may take longer as OCR engines initialize
- Downloadable font may load asynchronously on first launch

## Reporting Results
Document any failures with:
- Device model & Android version
- Exact steps to reproduce
- Logcat excerpt showing error
- Screenshot if UI-related
