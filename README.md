# Iskorly

**AI-powered answer sheet scanning, scoring, and item analysis for classrooms.**

[![Version](https://img.shields.io/badge/version-1.7-blue.svg)](project-docs/CHANGELOG.md)
[![Android](https://img.shields.io/badge/Android-6.0%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/license-Educational-orange.svg)](#license)

> **Iskorly** (formerly "Item Score Analysis App") - Faster Scoring. Smarter Teaching.

🌐 **[Visit our website](https://traumereixd.github.io/Iskorly/)** | 📋 [QBO Materials](docs/qbo.md)

---

## Overview

Iskorly uses Google Cloud Vision OCR to automatically extract and score student answers from photos or batch-imported images. Compare results against configurable answer keys and get instant per-question analytics to improve your teaching.

### Key Features

✅ **Smart OCR Scanning** - AI-powered answer extraction from photos  
✅ **Automatic Scoring** - Instant grading with color-coded feedback (green/red/yellow)  
✅ **Batch Processing** - Import and process multiple images at once  
✅ **Crop & Rotate** - Built-in image cropper for better OCR accuracy  
✅ **Item Analysis** - Per-question statistics showing common mistakes  
✅ **Answer Key Management** - Multiple slots for different quizzes with import/export  
✅ **History & Export** - Track all results and export to CSV  
✅ **Accessibility** - Large text mode and high-contrast OCR options  

---

## Quick Start

### Prerequisites

- Android device (API 23+, Android 6.0 or higher)
- Google Vision API key ([Get one here](https://console.cloud.google.com))
- OCR.Space API key (optional, for handwriting fallback)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/traumereixd/Iskorly.git
   cd Iskorly
   ```

2. **Configure API Keys**
   
   Create `local.properties` in the project root:
   ```properties
   # Required
   GCLOUD_VISION_API_KEY=your_vision_api_key_here
   
   # Optional: fallback for handwriting
   OCR_SPACE_API_KEY=your_ocr_space_key_here
   ```

3. **Build and Install**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### Basic Usage

1. **Set up an answer key** - Menu → Setup → Enter question numbers and answers
2. **Scan an answer sheet** - Start → Scan or Import Photos
3. **Crop the image** - Focus on the answer area, rotate if needed
4. **Review parsed answers** - Edit any OCR errors in the grid
5. **Score and save** - Tap Confirm & Score, then Save

📖 **[Read the full tutorial in the app](project-docs/QUICK_START.md)** or tap Tutorial from the main menu.

---

## Documentation

- 📘 [Quick Start Guide](project-docs/QUICK_START.md) - Detailed usage instructions
- 📋 [Changelog](project-docs/CHANGELOG.md) - Version history and features
- 🔒 [Security Policy](project-docs/SECURITY.md) - Data privacy and vulnerability reporting
- 🧪 [Testing Guide](project-docs/TESTING_GUIDE.md) - Manual testing procedures
- 👩‍🏫 [Teacher Analytics Guide](project-docs/TEACHER_ANALYTICS_GUIDE.md) - Using masterlist and item analysis
- 🎓 [DepEd Presentation](project-docs/DEPED_PRESENTATION.md) - Background and research

**Implementation Details:**
- [Visual Design Spec](project-docs/VISUAL_DESIGN_SPEC.md)
- [OCR Accuracy Improvements](project-docs/OCR_ACCURACY_IMPROVEMENTS.md)
- [Crop UI Implementation](project-docs/CROP_UI_LAYOUT.md)

---

## How It Works

### Scanning Flow

```
📱 Start Scan → 📷 Take Photo → ✂️ Crop → 🔍 OCR Parse → ✏️ Edit Answers → ✅ Score → 💾 Save
```

### OCR Processing

1. **Image Enhancement** - Grayscale conversion, contrast adjustment
2. **Google Vision API** - Primary OCR engine
3. **OCR.Space Fallback** - Used for handwriting or if Vision fails
4. **Answer Key Filtering** - Only extracts questions matching your key
5. **Two-Column Grid** - Displays results in editable format

### Item Analysis

- **Masterlist** - Aggregated statistics across all saved records
- Shows correct/incorrect counts, percentage, and most common wrong answer per question
- Filter by exam or section for targeted analysis
- Export to CSV for spreadsheet analysis

---

## Technical Details

- **Language**: Java 11
- **Build System**: Gradle (Kotlin DSL)
- **Min SDK**: 23 (Android 6.0)
- **Target SDK**: 34 (Android 14)

### Key Dependencies

- AndroidX Core & AppCompat
- Material Design Components 3
- OkHttp for API calls
- CanHub Android Image Cropper

### Architecture

- `MainActivity.java` - Main controller and UI
- `Parser.java` - OCR text parsing with answer key filtering
- `OcrProcessor.java` - OCR pipeline management
- `QuestionStats.java` - Statistical analysis
- `ImageUtil.java` - Image preprocessing

---

## API Requirements

### Google Cloud Vision API
- **Required** for OCR functionality
- Free tier available, then pay-per-use
- [Get API Key](https://console.cloud.google.com)

### OCR.Space API (Optional)
- **Optional** fallback for handwriting
- Free tier: 25,000 requests/month
- [Get API Key](https://ocr.space/ocrapi)

### OpenRouter API (Optional)
- **Optional** for Vercel reparse endpoint
- Uses `meta-llama/llama-3.3-70b-instruct:free` (zero-cost)
- [Get API Key](https://openrouter.ai/)

---

## Best Practices

### Photography Tips
- ✅ Use good, even lighting (avoid shadows and glare)
- ✅ Keep answer sheet flat and parallel to camera
- ✅ Fill the frame but leave small margins
- ✅ Use in-app crop to focus on answers only

### Handwriting Tips
- ✅ Use dark pen (black or blue)
- ✅ Write clearly in designated areas
- ✅ Avoid cross-outs when possible
- ✅ Enable High-Contrast OCR in Settings for faint marks

### Batch Scanning
- ✅ Maintain consistent lighting across sheets
- ✅ Keep same orientation for all images
- ✅ Review merged results carefully
- ✅ First non-blank answer wins in conflicts

---

## Security & Privacy

- 🔒 All student data stored **locally on device**
- 🔒 No data transmitted except images to OCR APIs
- 🔒 No personally identifiable information sent with images
- 🔒 API keys stored in `local.properties` (gitignored)
- 🔒 CSV exports contain student data - handle with care

See [SECURITY.md](project-docs/SECURITY.md) for vulnerability reporting.

---

## Contributing

This project is maintained for DepEd Philippines educational use. For bug reports or feature requests, please create an issue on GitHub.

---

## License

Educational use - DepEd Philippines

---

## Credits

**Research Team:**
- Españo, Elijah Ria D.
- Lolos, Kneel Charles B.
- Mahusay, Queen Rheyceljoy F.
- Medel, Myra J.
- Reyes, John Jharen R.
- Sahagun, Jayson G.
- Tagle, Steve Aldrei D.

**Developer:** Sahagun, Jayson G.

**Version:** 1.7 | **Date:** January 2025

---

## Support

For technical support or questions, please:
- 📧 Open an issue on GitHub
- 📖 Check the [documentation](project-docs/)
- 🎓 Review the [DepEd presentation](project-docs/DEPED_PRESENTATION.md)

---

**Made with ❤️ for teachers in the Philippines**
