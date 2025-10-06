# Changelog - Item Score Analysis App

## Version 1.3 (2025)

### New Features

1. **Parsing Constrained to Answer Key**
   - OCR parsed answers now strictly follow the configured answer key
   - Only questions in the answer key are shown in the editable list
   - Questions with no detected value show as blank for manual editing
   - Extraneous detections not in the key are ignored

2. **Two-Column Grid Layout for Parsed Answers**
   - Replaced vertical linear list with responsive 2-column grid
   - Each answer displayed in a MaterialCardView cell
   - Improved space efficiency and readability
   - 8dp spacing between cells for better visual separation

3. **Multi-Image Batch Scanning**
   - New "Import Photos" button in scan session
   - Select multiple images from gallery for sequential OCR processing
   - Automatic merging of results (first non-blank value wins)
   - Progress dialog shows processing status
   - Status footer displays: "Processed X image(s) • Filled Y / N answers"

4. **In-App Camera Crop**
   - Integrated uCrop library for image cropping
   - After camera capture, user can crop image before OCR
   - Freeform aspect ratio with grid guidelines
   - Fallback to direct OCR if crop fails or is canceled

5. **Updated UI and UX**
   - Import Photos button alongside Scan button
   - Improved visual feedback during multi-image processing
   - Better status messages for batch operations

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
