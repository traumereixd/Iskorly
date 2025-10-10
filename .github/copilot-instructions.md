# Copilot Instructions for Iskorly

## Project Overview

Iskorly (formerly Item Score Analysis App) is an AI-powered Android application designed for educational use in the Philippines (DEPED). It provides answer sheet scanning, automatic scoring, and comprehensive item analysis for classroom assessments.

**Purpose**: Enable teachers to quickly scan and grade student answer sheets using OCR technology, then analyze question-level statistics to improve test quality.

**Key Capabilities**:
- OCR-based answer sheet scanning (Google Vision API + OCR.Space fallback)
- Automatic scoring against configurable answer keys
- Advanced psychometric analysis (difficulty, discrimination, reliability)
- Per-section and overall statistics
- CSV export for data analysis
- Batch image processing

## Tech Stack

### Core Technologies
- **Language**: Java 11
- **Platform**: Android (Min SDK 23, Target SDK 34)
- **Build System**: Gradle (Kotlin DSL)
- **UI Framework**: Material Design 3 (Material Components)

### Key Dependencies
- AndroidX Core & AppCompat
- Material Design Components 3
- OkHttp (for API calls)
- CanHub Android Image Cropper (Maven Central)
- Google Vision API (Cloud OCR)
- OCR.Space API (fallback for handwriting)

### Build Configuration
- Root: `build.gradle.kts` - Plugin versions
- App: `app/build.gradle.kts` - Dependencies and API key injection
- Settings: `settings.gradle.kts` - Repository configuration

## Architecture & Key Files

### Core Application Files
- **MainActivity.java** (~3500 lines): Main application controller
  - Answer sheet scanning workflow
  - Answer key management (multiple slots)
  - Student data entry with autocomplete
  - Masterlist views (By Section / All)
  - History management
  - CSV import/export

- **QuestionStats.java** (~500 lines): Statistical analysis engine
  - Item analysis (difficulty, discrimination, delta)
  - Reliability metrics (KR-20, KR-21, Cronbach's alpha)
  - Per-section and overall summaries
  - Upper/lower group analysis

- **MasterlistRepository.java**: Snapshot versioning system
  - Stores snapshots tied to answer key slots
  - JSON serialization via SharedPreferences

### OCR Pipeline
- **OcrProcessor.java**: Orchestrates OCR workflow
- **ocr/CloudVisionOcrEngine.java**: Google Vision API integration
- **ocr/OcrSpaceEngine.java**: OCR.Space fallback for handwriting
- **Parser.java**: OCR text parsing with Roman numeral support
- **ImageUtil.java**: Image preprocessing (contrast, sharpening)

### UI Components
- **MainMenuActivity.java**: Simplified 3-card menu (Start/Tutorial/Credits)
- **SplashActivity.java**: App initialization
- **SimpleCropActivity.java**: Custom crop UI with rotate/flip
- **OmrOverlayView.java**: Camera overlay guide

### Utilities
- **NetworkUtil.java**: Network state checking
- **UiConfig.java**: UI configuration constants

## Code Style & Conventions

### Java Conventions
- **Package**: `com.bandecoot.itemscoreanalysisprogram` (legacy, do not change)
- **Naming**: camelCase for methods/variables, PascalCase for classes
- **Logging**: Use `Log.d/i/w/e` with consistent TAG (e.g., "ISA_VISION", "CROP_FLOW")
- **Error Handling**: Try-catch with user-friendly Toast messages
- **Comments**: Inline comments for complex logic, JavaDoc for public methods

### Android Patterns
- **Activities**: Use `onCreate()` for initialization, `onActivityResult()` for launchers
- **UI Updates**: Run on UI thread with `runOnUiThread()`
- **Resource IDs**: Descriptive names (e.g., `btnScanSubmit`, `tvStudentName`)
- **Permissions**: Request at runtime for camera, storage
- **SharedPreferences**: Use for lightweight persistence (answer keys, history, settings)

### Material Design 3
- **Theme**: Uses indigo primary color (#4452A6)
- **Typography**: Inter font family
- **Components**: MaterialButton, MaterialAutoCompleteTextView, CardView
- **Dark Mode**: Automatic system theme detection supported

## Testing Guidelines

### Current State
- **No automated test infrastructure** in place (only example Kotlin tests)
- **Manual testing is primary validation method**
- Comprehensive manual testing guides provided:
  - `MANUAL_TESTING_GUIDE.md`: 15 detailed test scenarios
  - `MANUAL_TESTING_CHECKLIST.md`: Quick validation checklist

### Manual Testing Focus Areas
1. **OCR Accuracy**: Test with various handwriting styles and lighting
2. **Statistical Calculations**: Verify with known datasets
3. **Edge Cases**: 0 students, 1 student, all correct/incorrect
4. **UI Responsiveness**: Batch operations, large datasets (60+ questions)
5. **CSV Round-trip**: Export → verify → import
6. **Dark Mode**: All screens in light/dark themes

### Adding Tests (Future)
- If adding unit tests, use JUnit 4
- For UI tests, use Espresso
- Focus on:
  - Statistical calculations (QuestionStats)
  - Parser logic (roman numerals, various formats)
  - CSV export formatting
  - Image preprocessing quality

## Build & Development

### Local Setup
1. Clone repository
2. Create `local.properties` with API keys:
   ```properties
   GCLOUD_VISION_API_KEY=your_key_here
   OCR_SPACE_API_KEY=your_key_here  # Optional
   ```
3. Sync Gradle and build

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Clean build
./gradlew clean assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

### CI/CD
- **GitHub Actions**: `.github/workflows/release.yml`
- Triggers on version tags (e.g., v1.3.0) or manual dispatch
- Builds signed APK and AAB
- Creates GitHub release with artifacts
- See `RELEASE_WORKFLOW_SETUP.md` for detailed setup

## Key Workflows

### Answer Sheet Scanning Workflow
1. User taps "Start Scan" → selects answer key slot
2. Capture photo or import images
3. Crop image (SimpleCropActivity)
4. OCR processing (Google Vision → OCR.Space fallback)
5. Parse answers constrained to answer key
6. Display in editable 2-column grid with color feedback
7. Manual corrections if needed
8. Confirm & Score → calculate statistics
9. Save to history with autocomplete student/section/exam data

### Answer Key Management
- Multiple slots (3 by default, expandable)
- Import/export JSON format
- Question numbers + correct answers (A-Z or text up to 40 chars)
- Case-insensitive scoring (mixed case allowed)
- Tied to Masterlist snapshots via MasterlistRepository

### Masterlist Views
- **By Section**: Per-section cards with question tables
- **All**: Aggregated view across all sections
- Each question shows: correct/incorrect counts, difficulty, discrimination, upper/lower %
- Section summaries: KR-20, Cronbach's alpha, SEM, mean, SD, MPS
- CSV export with all metrics

## API Requirements & Security

### Google Vision API
- **Required** for OCR functionality
- Store key in `local.properties` (gitignored)
- Injected via BuildConfig at compile time
- Never commit keys to version control

### OCR.Space API (Optional)
- Fallback for handwriting recognition
- Free tier: 25,000 requests/month
- Same security as Vision API key

### Privacy
- All data stored locally on device
- No cloud storage or analytics
- OCR images sent only to configured APIs
- CSV export for manual backup

## Common Patterns & Anti-Patterns

### ✅ DO
- Use try-catch for API calls and file I/O
- Provide user feedback via Toast for all operations
- Log important events for debugging
- Validate user input before processing
- Handle null cases explicitly
- Use existing utility methods (e.g., ImageUtil for preprocessing)

### ❌ DON'T
- Don't add new external dependencies without discussion
- Don't modify package name (breaks backward compatibility)
- Don't remove existing logging (helps with support)
- Don't change API key injection mechanism
- Don't add animations (teachers prefer instant UI updates)
- Don't pre-cache analytics (compute on-demand to save storage)

## Documentation

### For Users
- `README.md`: Getting started, features, usage
- `QUICK_REFERENCE.md`: Teacher-friendly feature guide
- `MANUAL_TESTING_GUIDE.md`: 15 test scenarios
- `TEACHER_ANALYTICS_GUIDE.md`: Plain-language stats explanations
- `CHANGELOG.md`: Version history

### For Developers
- `TEACHER_WORKFLOWS_IMPLEMENTATION.md`: Technical implementation details
- `ANALYTICS_IMPLEMENTATION.md`: Statistical methods and formulas
- `SIMPLECROP_IMPLEMENTATION.md`: Crop UI technical spec
- `RELEASE_WORKFLOW_SETUP.md`: CI/CD setup guide
- Various `*_IMPLEMENTATION.md` files: Feature-specific docs

### Best Practices for Updates
- Update `CHANGELOG.md` for user-facing changes
- Create implementation summary docs for major features
- Update README.md for new capabilities
- Provide manual testing guides for new features

## Known Limitations & Design Decisions

### Current Limitations
- No automated test infrastructure
- Masterlist toggle state not persisted between sessions
- Recent values autocomplete capped at 50 entries (performance)
- No Room database (SharedPreferences for now)

### Design Decisions (Don't Change Without Discussion)
- **Slot-based filtering**: Instead of time-based windows (reduces complexity)
- **On-demand stats**: Compute when needed vs pre-cache (reduces storage)
- **Instant transitions**: No animations (teacher feedback: faster is better)
- **Two-column merge**: First non-blank wins (simple, predictable)
- **Discrete color scale**: Instead of gradient (improves clarity)

## Performance Considerations

### Image Processing
- Maximum output size: 2048px (prevents OOM)
- Bitmap recycling after use
- Downscaling fallback on OOM
- Preprocessing only when needed

### Large Datasets
- Efficient algorithms (O(n*k) for most operations)
- No unnecessary loops in stats calculations
- Proper data structure selection
- UI updates batched when possible

### Memory Management
- Recycle bitmaps explicitly
- Use try-finally for resource cleanup
- Handle OutOfMemoryError gracefully
- Minimal memory overhead for analytics

## Git Workflow

### Branching
- Main branch: `master`
- Feature branches: `copilot/*` or descriptive names
- Release tags: `v1.x.y`

### Commits
- Clear, descriptive commit messages
- Reference issues when applicable
- Group related changes together
- Don't commit API keys or local.properties

### Files to Never Commit
- `local.properties` (contains API keys)
- `*.keystore` (signing keys)
- IDE-specific files (covered by .gitignore)
- Build artifacts (`app/build/`, `app/release/`)

## Support & Maintenance

### Issue Tracking
- Create GitHub issues for bugs or feature requests
- Include Android version, device model, and logs when reporting bugs
- Screenshots appreciated for UI issues

### Code Review Focus
- Backward compatibility (package name, data formats)
- Security (API keys, data privacy)
- Performance (bitmap handling, large datasets)
- User experience (teacher feedback prioritized)
- Documentation completeness

## Future Enhancement Opportunities

### Short Term
- Per-question notes and competency tagging
- Filter chips for slot/exam in Masterlist
- Demo mode with anonymization

### Medium Term
- Room database migration for better performance
- Backup/restore to ZIP functionality
- Auto-key from majority feature
- Retake awareness tracking

### Long Term
- Parallel OCR processing for two-column mode
- Machine learning for answer key inference
- Interactive analytics dashboard
- Export to PDF reports

---

**Version**: 1.7 (as of January 2025)
**Maintainer**: Sahagun, Jayson G.
**Organization**: DEPED Philippines
**License**: Educational Use
