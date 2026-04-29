# Iskorly — Item Score Analysis Program

[![Version](https://img.shields.io/badge/version-1.8.1-blue.svg)](app/build.gradle.kts)
[![Android](https://img.shields.io/badge/Android-6.0%2B-green.svg)](https://developer.android.com)
[![Min SDK](https://img.shields.io/badge/minSdk-23-brightgreen.svg)](app/build.gradle.kts)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red.svg)](#license)

Iskorly helps teachers scan answer sheets with their phone camera, extract student responses using OCR, score them automatically against a configurable answer key, and generate per-question item analysis — all without an internet-connected laptop or spreadsheet software.

**Website:** [iskorly.vercel.app](https://iskorly.vercel.app/)

---

## Table of Contents

- [Problem Statement](#problem-statement)
- [Screenshots](#screenshots)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Setup and Run](#setup-and-run)
- [Usage Workflow](#usage-workflow)
- [Privacy and Ethics](#privacy-and-ethics)
- [Roadmap](#roadmap)
- [Credits](#credits)
- [License](#license)

---

## Problem Statement

Manual answer sheet checking is slow, inconsistent, and produces no diagnostic data. Teachers in resource-constrained settings rarely have access to OMR scanners or Excel-based grading tools. Iskorly turns a commodity Android phone into a portable grading and analytics system:

- Scans handwritten or bubble answer sheets via camera
- Extracts answers through cloud OCR with a multi-engine fallback strategy
- Scores against configurable answer key slots in seconds
- Surfaces per-question statistics (discrimination index, most common wrong answer, section MPS) to guide instructional decisions

---

## Screenshots

Place screenshots in `docs/screenshots/` using the filenames below. The image paths in the table are already set — only the files need to be added.

| Scan and Capture | Crop and Verify | Scoring |
|---|---|---|
| ![Scan](docs/screenshots/scan.png) | ![Crop](docs/screenshots/crop.png) | ![Score](docs/screenshots/score.png) |

| History and Filters | Masterlist Analytics | Answer Key Slots |
|---|---|---|
| ![History](docs/screenshots/history.png) | ![Masterlist](docs/screenshots/masterlist.png) | ![Slots](docs/screenshots/slots.png) |

---

## Key Features

### Scanning and OCR

- Live camera preview and still capture via **Camera2**
- Proper JPEG orientation handling for all Android devices
- Mandatory crop step using **uCrop** for region-of-interest accuracy; auto-crop fallback available
- Selectable OCR engines:
  - **Google Cloud Vision** (Document Text Detection)
  - **Azure Read** (high-accuracy printed and handwritten text)
  - **OCR.Space** (optional fallback, useful for handwriting when primary engines are unavailable)
- Multi-variant OCR strategy: retries with high-contrast and two-column variants when initial results are sparse; early exit when fill threshold is met

### Answer Key Management

- Named answer key **slots** — store and switch between multiple exam keys without overwriting
- Import and export answer keys in JSON, CSV, and TXT formats

### Verification and Scoring

- Parsed answers displayed in an editable two-column grid for manual correction before scoring
- One-tap scoring against the active answer key slot
- Color-coded result cells: correct (green), incorrect (red), blank (yellow)
- Correct-answer hints shown inline for incorrect responses

### History and Export

- Results saved locally with student name, section, exam, timestamp, raw score, percentage score, and per-question answers
- History grouped by quiz and section with collapsible sections
- Filter by quiz name or section; search across records
- Export to CSV with quiz and section selection dialog

### Masterlist Analytics

- Aggregated per-question statistics across all saved results:
  - Correct count, incorrect count, percentage correct, most common wrong answer
- Section summaries: mean score, standard deviation, Mean Percentage Score (MPS)
- Heatmap overlay for quick visual identification of problem areas across sections
- By-section view and all-sections view

### Usability

- Autocomplete manager for student names, section names, and exam names (importable/exportable)
- Type hints: configurable question-number ranges to guide the parser (multiple choice vs. identification)
- Remote kill-switch endpoint for disabling the app without a Play Store update
- Admin panel protected by local PIN

---

## Tech Stack

| Layer | Technology |
|---|---|
| Platform | Android (Java 11), min SDK 23 (Android 6.0), target SDK 34 |
| Build | Gradle with Kotlin DSL |
| Camera | Camera2 API |
| Image processing | uCrop 2.2.8, CanHub Android Image Cropper 4.5.0, Bitmap preprocessing (resize, contrast) |
| Networking | OkHttp 4.9.3 |
| OCR engines | Google Cloud Vision REST API, Azure Computer Vision Read API, OCR.Space REST API |
| UI | Material Design Components 3, MaterialAutoCompleteTextView, MaterialCardView |
| Persistence | SharedPreferences (history, answer key slots, settings, kill-switch state cache) |
| CI (optional) | GitHub Actions (build + lint) |

---

## Architecture

The app follows a single-activity pattern. `MainActivity` orchestrates the full grading pipeline:

1. Camera preview and still capture (Camera2)
2. Crop flow (uCrop intent → result callback)
3. OCR via `OcrProcessor`, which delegates to the active `OcrEngine` implementation
4. Answer parsing via `Parser.parseOcrTextSmartWithFallback` with configurable type hints
5. Editable verification grid displayed to the user
6. Scoring against the active answer key slot
7. Persistence to local history and export to CSV; masterlist aggregation via `QuestionStats`

The `OcrEngine` interface allows swapping Google Cloud Vision for Azure Read (or the OCR.Space fallback) without changes to the UI layer. This abstraction is the primary seam for future modularization and testing.

Supporting classes:

| Class | Responsibility |
|---|---|
| `OcrProcessor` | Orchestrates multi-variant OCR retries, early-exit logic, and engine selection |
| `OcrEngine` | Interface for pluggable OCR backends |
| `Parser` | Extracts answer tokens from OCR text using answer-key-aware heuristics |
| `QuestionStats` | Computes per-question and per-section statistics for the masterlist |
| `ImageUtil` | Bitmap resizing, JPEG compression, and contrast enhancement |

---

## Setup and Run

### Prerequisites

- **Android Studio** Hedgehog (2023.1) or later
- Android SDK installed (via SDK Manager in Android Studio)
- Java 11 or later
- A physical Android device is strongly recommended for camera testing; an emulator can be used for UI-only flows

### 1. Clone the repository

```bash
git clone https://github.com/traumereixd/Iskorly.git
cd Iskorly
```

### 2. Open in Android Studio

Open the project root directory in Android Studio and wait for the Gradle sync to complete.

### 3. Configure API keys

API keys are injected at build time via `BuildConfig` fields. They are read from `local.properties` (gitignored) or Gradle project properties. **Do not commit keys to version control.**

Copy the provided template and fill in your values:

```bash
cp local.properties.template local.properties
```

Then edit `local.properties`:

```properties
# Required — configure at least one OCR engine

# Option A: Azure Computer Vision (recommended primary engine)
AZURE_VISION_KEY=your-azure-vision-api-key
AZURE_VISION_ENDPOINT=https://your-region.api.cognitive.microsoft.com/

# Option B: Google Cloud Vision (alternative primary engine)
GCLOUD_VISION_API_KEY=your-google-cloud-vision-api-key

# Optional — OCR.Space fallback for handwriting
OCR_SPACE_API_KEY=your-ocr-space-api-key

# Optional — Remote kill-switch (GitHub Gist raw URL or any HTTPS JSON endpoint)
# JSON format: {"disabled": false, "message": ""}
KILL_SWITCH_URL=https://gist.githubusercontent.com/your-username/your-gist-id/raw/status.json
```

The corresponding `BuildConfig` fields defined in `app/build.gradle.kts` are:
`GCLOUD_VISION_API_KEY`, `AZURE_VISION_KEY`, `AZURE_VISION_ENDPOINT`, `OCR_SPACE_API_KEY`, `KILL_SWITCH_URL`.

See [local.properties.template](local.properties.template) for full documentation and [GIST_KILL_SWITCH_SETUP.md](GIST_KILL_SWITCH_SETUP.md) for kill-switch configuration.

### 4. Build and run

Connect a device with USB debugging enabled (or start an emulator), then click **Run** in Android Studio, or use the command line:

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## Usage Workflow

1. Open the app and enter **Student Name**, **Section**, and **Exam** (autocomplete suggestions appear as you type).
2. Go to **Answer Key Slots** and create or import an answer key for the exam.
3. Tap **Scan** to open the camera; capture the answer sheet.
4. The mandatory crop screen opens — frame the answer area precisely and confirm.
5. OCR runs automatically. The parsed answers appear in an editable grid; correct any misreads.
6. Tap **Confirm and Score**. Results are highlighted and the score is displayed.
7. Tap **Save** to store the result in history.
8. View **History** to browse past results, apply filters, or export to CSV.
9. View **Masterlist** for aggregated per-question statistics and section comparisons.

---

## Privacy and Ethics

Iskorly uses third-party cloud OCR services. When a scan is submitted:

- The cropped image is encoded as JPEG and sent over HTTPS to the selected OCR provider (Google Cloud Vision or Azure Cognitive Services).
- Student names and answer text are **not** sent to OCR providers — only the image.
- All results (names, scores, answers) are stored exclusively on the device in SharedPreferences; no data is transmitted to any server controlled by this project.

**Deployment responsibilities:**

- Inform students and guardians that answer sheet images will be processed by a cloud OCR service.
- Review the data retention policies of Google Cloud Vision and Azure Cognitive Services before institutional deployment.
- Handle CSV exports containing student data in accordance with applicable data privacy regulations (e.g., RA 10173 in the Philippines).
- Do not share `local.properties` or API keys.

See [project-docs/SECURITY.md](project-docs/SECURITY.md) for the vulnerability disclosure policy.

---

## Roadmap

- Migrate history storage from SharedPreferences to Room (proper schema, migrations, and query support)
- Add ML Kit on-device OCR as a fully offline engine option, removing the cloud dependency for basic use cases
- Add instrumentation tests for the OCR-to-parse-to-score pipeline
- Add a deterministic parser unit test suite with sample OCR output fixtures
- Modularize OCR engine, parser, and history into separate Gradle modules
- Implement proper ViewModel and LiveData to decouple UI state from activity lifecycle

---

## Documentation

- [Quick Reference](project-docs/QUICK_REFERENCE.md)
- [Teacher Analytics Guide](project-docs/TEACHER_ANALYTICS_GUIDE.md)
- [OCR Accuracy Improvements](project-docs/OCR_ACCURACY_IMPROVEMENTS.md)
- [Kill-Switch Setup (GitHub Gist)](GIST_KILL_SWITCH_SETUP.md)
- [Kill-Switch Setup (Firebase)](KILL_SWITCH_SETUP.md)
- [Admin Page Configuration](docs/ADMIN_PAGE_CONFIG.md)
- [Security Policy](project-docs/SECURITY.md)

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

**Version:** 1.8.1

---

## License

No license file is currently included in this repository. All rights are reserved by the authors. If you intend to use, adapt, or distribute this software, contact the developer for permission.
