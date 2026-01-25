# Changelog - Iskorly

> **Iskorly** (formerly "Item Score Analysis App") - AI-powered answer sheet scanning and scoring for classrooms.

---

## Summary of Key Features

Iskorly provides teachers with:
- **Smart OCR Scanning**: AI-powered answer extraction from photos or batch imports
- **Automatic Scoring**: Instant grading against configurable answer keys  
- **Crop-Before-OCR**: Built-in cropper with rotation for better accuracy
- **Item Analysis**: Per-question statistics and common mistake tracking
- **Batch Processing**: Multi-image import for efficient scanning
- **Data Export**: CSV export for further analysis
- **Accessibility**: Large text mode and high-contrast options

---

## Version 1.7 (Latest) - January 2025

### UX 2.0 Visual Refresh
- Simplified main menu (Start, Tutorial, Credits)
- Modern interface with consistent indigo color scheme
- Improved button layouts and visual hierarchy

---

## Version 1.3 (2025)

*Rebranding note: App name changed from "Item Score Analysis" to "Iskorly" for memorability.*

### New Features

1. **Parsing Constrained to Answer Key**
   - OCR parsed answers now strictly follow the configured answer key
   - Only questions in the answer key are shown in the editable list
   - Questions with no detected value show as blank for manual editing
   - Extraneous detections not in the key are ignored
   - Implemented via Parser.filterToAnswerKey() function

2. **Two-Column Grid Layout for Parsed Answers**
   - Replaced vertical linear list with responsive 2-column grid
   - Each answer displayed in a MaterialCardView cell
   - Improved space efficiency and readability
   - 8dp spacing between cells for better visual separation
   - Dynamic text watchers enable Confirm button when any answer is filled

3. **Multi-Image Batch Scanning**
   - New "Import Photos" button in scan session
   - Select multiple images from gallery for sequential OCR processing
   - Automatic merging of results (first non-blank value wins)
   - Progress dialog shows processing status
   - Status footer displays: "Processed X image(s) • Filled Y / N answers"
   - Enhanced image preprocessing applied to all imported images

4. **In-App Camera Crop (Enhanced Custom UI)**
   - Custom SimpleCropActivity with full-featured crop controls
   - Rotate left/right buttons (90° increments) with cumulative rotation tracking
   - Flip horizontal button for mirror transformations
   - Rotation angle display (0°, 90°, 180°, 270°) for user feedback
   - Aspect ratio presets: Free, 4:3, 1:1 for common use cases
   - Haptic feedback on all button interactions
   - Dark theme support with night-mode colors (#121212 background, #1E1E1E surface)
   - Performance safeguards: bitmap recycling, OutOfMemoryError handling with downscaling
   - Maximum output size limits to prevent memory issues
   - Accessibility: content descriptions on all controls, 56dp touch targets
   - Replaces CanHub's built-in crop UI with custom implementation
   - Fallback to automatic crop if crop fails or is canceled
   - Based on CanHub CropImageView component with enhanced UX layer

5. **Improved Handwriting Sensitivity**
   - Image preprocessing with grayscale conversion and contrast enhancement
   - New ImageUtil.enhanceForOcr() method applies ColorMatrix transformations
   - Fallback to OCR.Space API when Google Vision returns empty results
   - OCR_SPACE_API_KEY configuration in local.properties (optional)
   - Logs which OCR engine produced results for debugging
   - Updated tutorial with handwriting tips

6. **Masterlist: Per-Question Statistics**
   - New Masterlist button in History screen
   - Aggregated item analysis across all saved records
   - Displays table with columns: Q# | Correct | Incorrect | % | Common Miss
   - Shows most common wrong answer for each question
   - Summary statistics: total questions, attempts, overall % correct
   - Filters by current answer key slot
   - Future-proof design for exam/section filters

8. **Code Quality & Structure**
   - Created OcrProcessor helper class to encapsulate OCR logic
   - Extracted image processing, API calls, and parsing into reusable component
   - QuestionStats class for statistical computations
   - Improved code organization and maintainability
   - Better separation of concerns
   - Export CSV button added to History screen (Feature #4 partial implementation)

9. **UX Polishing**
   - Visual feedback: green (correct), red (incorrect), yellow (unanswered) highlighting
   - Smart button states: Confirm & Score disabled until at least one answer filled
   - TextWatcher dynamically enables/disables buttons based on content
   - Progress dialogs during multi-image batch processing
   - Improved status messages throughout the app
   - Smooth fade transitions between views

10. **UX 2.0 Updates (Latest)**
   - Simplified main menu to show only Start, Tutorial, and Credits (removed History, Masterlist, Setup cards)
   - Tutorial and Credits converted to white cards matching Start card style for consistency
   - Unified all button colors to indigo primary (Setup, History, Import Photos, Scan Again, Cancel)
   - Start button launches MainActivity normally without special intents
   - History, Masterlist, and Setup remain accessible through MainActivity as before

### Improvements

- Updated version to 1.3 in build.gradle.kts and credits
- Added new string resources for v1.3 features
- Updated tutorial text to cover new functionality
- Enhanced Parser.java with filterToAnswerKey function
- Better logging for debugging multi-image and crop features

### Technical Changes

- Added CanHub Android Image Cropper dependency (4.5.0) via Maven Central (migrated from uCrop/JitPack)
- New ActivityResultLaunchers for multi-image import and crop
- Refactored JPEG processing to support crop workflow
- Added ImageUtil for consistent image processing across features

### Bug Fixes

- Fixed answer key filtering to prevent stray question numbers
- Improved error handling for image processing failures

## Version 1.1 (Previous)

- Initial release with basic OCR and scoring functionality
- Answer key management with slots
- History tracking and CSV export

---

## Future Enhancements (Not In Scope for v1.3)

The following features are documented for future releases:

- **Main Menu Redesign** (deferred to v1.4): Dedicated main menu screen with Start/Tutorial/Credits buttons, remove MaterialToolbar
  - Would require refactoring MainActivity into separate activities or fragment-based navigation
  - Significant architectural change best suited for a major version update
  - Current menu-based navigation is functional and familiar to users
  
- **Answer Key Auto-Detection**: Automatically detect and populate answer key from scanned key sheets

- **Offline On-Device OCR**: Integrate ML Kit for offline OCR to reduce latency and eliminate API dependencies

- **Analytics Graphs**: Add difficulty discrimination index and other advanced statistical visualizations

- **Masterlist CSV Export**: Export per-question statistics table to CSV format

- **Advanced Filters**: Exam/Section filters for Masterlist view to analyze specific subsets of data

