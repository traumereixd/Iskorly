## Iskorly (formerly Item Score Analysis App) v1.7

AI-powered answer sheet scanning, scoring, and item analysis for classrooms.

> Rebranded from “Item Score Analysis” to “Iskorly” for a shorter, more memorable identity.
> **UX 2.0** visual refresh brings modern interface, new color system, and enhanced crop reliability.

## Tagline
Faster Scoring. Smarter Teaching

## Overview
Iskorly uses OCR (Google Vision + fallback) to extract student answers from photos or batch-imported images, compares them to a configurable answer key, and produces per-question analytics.

(Existing sections below remain the same—only name replaced.)

## Core Features (v1.3)
- Smart answer key filtering (only key questions displayed)
- Batch scanning (multi-image import)
- Crop-before-OCR workflow (CanHub Android Image Cropper with rotation & free-style crop)
- Two-column editable grid
- Color-coded correctness feedback
- Handwriting enhancement + fallback engine
- Masterlist (per-question stats)
- CSV export
- Multiple answer key slots
- Simplified UX 2.0 main menu (Start, Tutorial, Credits)

## Rebranding Notes
- Package name unchanged for backward compatibility.
- Historical references: See CHANGELOG.md for prior naming.
- Credits updated to reflect “Iskorly” branding.

(Keep rest of previous README content here unchanged unless you want me to regenerate it all with new name everywhere.)

## Overview

The Item Score Analysis (ISA) App uses Google Vision API and OCR technology to automatically recognize and score student answer sheets. It provides comprehensive statistical analysis and supports multiple answer key formats.

## Version 1.3 Features

### Core Functionality
- **Smart OCR Parsing**: Automatically extracts answers from photos, constrained to configured answer key
- **Answer Key Management**: Multiple slots for different quizzes with import/export capability
- **Two-Column Grid View**: Efficient editable answer display with visual feedback
- **Multi-Image Batch Scanning**: Import multiple photos at once for sequential processing
- **In-App Cropping**: Crop captured images before OCR for improved accuracy
- **Handwriting Recognition**: Enhanced preprocessing and OCR.Space fallback for better results
- **Masterlist Statistics**: Per-question analysis showing correct/incorrect rates and common mistakes
- **History Tracking**: Complete record keeping with CSV export capability

### Smart Features
- **Answer Key Filtering**: Only shows questions from your configured key
- **Visual Feedback**: Green (correct), red (incorrect), yellow (unanswered) highlighting
- **Smart Button States**: Automatically enables/disables based on content
- **Progress Indicators**: Visual feedback during batch operations

## Getting Started

### Prerequisites
- Android device running Android 6.0 (API 23) or higher
- Google Vision API key (required)
- OCR.Space API key (optional, for handwriting fallback)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/traumereixd/ISA-v1.2.git
   cd ISA-v1.2
   ```

2. **Configure API Keys**
   
   Create a `local.properties` file in the project root:
   ```properties
   # Required: Google Vision API key
   GCLOUD_VISION_API_KEY=your_vision_api_key_here
   
   # Optional: OCR.Space API key for handwriting fallback
   OCR_SPACE_API_KEY=your_ocr_space_key_here
   ```

3. **Build and Install**
   ```bash
   ./gradlew assembleDebug
   # Install on connected device
   ./gradlew installDebug
   ```

## Usage

### Setting Up Answer Keys

1. Tap **Setup Answer Key** from the main screen
2. Select or create an answer key slot
3. Enter question numbers and correct answers (A-Z or text up to 40 chars)
4. Use Import/Export to share keys between devices

### Scanning Answer Sheets

**Option 1: Camera Scan**
1. Tap **Start Scan**
2. Position answer sheet in camera view
3. Tap **Scan** to capture
4. Crop the image to focus on answers
5. Confirm to process with OCR

**Option 2: Import Photos**
1. Tap **Start Scan**
2. Tap **Import Photos**
3. Select one or multiple images
4. Wait for batch processing to complete

### Reviewing and Scoring

1. Review parsed answers in the editable 2-column grid
2. Manually correct any errors
3. Tap **Confirm & Score** to calculate results
4. Colors indicate: 🟢 Correct | 🔴 Incorrect | 🟡 Unanswered
5. Tap **Save** to add to history

### Analyzing Results

**View History**
- Browse all saved records organized by exam and section
- Rename or delete entries as needed
- Export all data to CSV

**Masterlist Statistics**
1. From History screen, tap **Masterlist**
2. View per-question statistics:
   - Correct/incorrect counts
   - Percentage correct
   - Most common wrong answer
3. Use for item analysis and test improvement

## Technical Details

### Architecture

- **Language**: Java 11
- **Build System**: Gradle (Kotlin DSL)
- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 34 (Android 14)

### Key Components

- `MainActivity.java`: Main application controller
- `Parser.java`: OCR text parsing with roman numeral support
- `OcrProcessor.java`: OCR pipeline management
- `QuestionStats.java`: Statistical analysis
- `ImageUtil.java`: Image preprocessing for OCR

### Dependencies

- AndroidX Core & AppCompat
- Material Design Components 3
- OkHttp for API calls
- CanHub Android Image Cropper (Maven Central)

## API Requirements

### Google Vision API
- **Type**: Cloud-based OCR
- **Rate Limits**: Check your GCP quota
- **Cost**: Free tier available, then pay-per-use
- **Get Key**: [Google Cloud Console](https://console.cloud.google.com)

### OCR.Space API (Optional)
- **Type**: Alternative OCR fallback
- **Rate Limits**: Free tier: 25,000 requests/month
- **Get Key**: [OCR.Space](https://ocr.space/ocrapi)

## Tips for Best Results

### Photography
- Use good lighting (avoid shadows and glare)
- Keep answer sheet flat and parallel to camera
- Fill entire frame, but leave small margins
- Use in-app crop to focus on answer area only

### Handwritten Answers
- Use dark pen (black or blue)
- Write clearly in designated areas
- Avoid cross-outs when possible
- Crop tightly around answers

### Batch Scanning
- Consistent lighting across all sheets
- Same orientation for all images
- Review merged results carefully
- First non-blank answer wins in conflicts

## Troubleshooting

**OCR returns empty results**
- Check API key configuration
- Verify image quality and lighting
- Try cropping to reduce noise
- Enable OCR.Space fallback

**Incorrect parsing**
- Ensure answer key is correctly configured
- Review question number format (1. or 1) or 1:)
- Check for OCR artifacts or unclear handwriting
- Manually correct in editable grid

**App crashes or freezes**
- Check available storage space
- Reduce image resolution
- Process fewer images at once
- Check network connectivity for API calls

## Security & Privacy

- API keys stored in `local.properties` (gitignored)
- No data transmitted except to OCR APIs
- All data stored locally on device
- Export CSV for manual backup

## Contributing

This project is maintained for DEPED use. For bug reports or feature requests, please create an issue on GitHub.

## License

Educational use - DEPED Philippines

## Credits

**Research Team**
Researchers:
Españo, Elijah Ria D.
Lolos, Kneel Charles B.
Mahusay, Queen Rheyceljoy F.
Medel, Myra J.
Reyes, John Jharen R.
Sahagun, Jayson G.
Tagle, Steve Aldrei D.

**Developer**
- Sahagun, Jayson G.

**Version**: 1.3
**Date**: January 2025

## Changelog

See [CHANGELOG.md](CHANGELOG.md) for detailed version history.

## Support

For technical support or questions about deployment, please contact the development team or create an issue on GitHub.
