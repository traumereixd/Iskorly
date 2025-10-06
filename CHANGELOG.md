# Changelog - Item Score Analysis App

## Version 1.3 (2025)

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

4. **In-App Camera Crop**
   - Integrated uCrop library (v2.2.8) for image cropping
   - After camera capture, user can crop image before OCR
   - Freeform aspect ratio with grid guidelines
   - Fallback to direct OCR if crop fails or is canceled
   - Improves accuracy by allowing tight cropping around answer area

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

7. **Code Quality & Structure**
   - Created OcrProcessor helper class to encapsulate OCR logic
   - Extracted image processing, API calls, and parsing into reusable component
   - QuestionStats class for statistical computations
   - Improved code organization and maintainability
   - Better separation of concerns

8. **UX Polishing**
   - Visual feedback: green (correct), red (incorrect), yellow (unanswered) highlighting
   - Smart button states: Confirm & Score disabled until at least one answer filled
   - TextWatcher dynamically enables/disables buttons based on content
   - Progress dialogs during multi-image batch processing
   - Improved status messages throughout the app
   - Smooth fade transitions between views

### Improvements

- Updated version to 1.3 in build.gradle.kts and credits
- Added new string resources for v1.3 features
- Updated tutorial text to cover new functionality
- Enhanced Parser.java with filterToAnswerKey function
- Better logging for debugging multi-image and crop features

### Technical Changes

- Added uCrop dependency (2.2.8) via jitpack
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
