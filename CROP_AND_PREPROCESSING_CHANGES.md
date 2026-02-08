# Mandatory Crop Selection and Handwriting OCR Preprocessing

## Overview
This document describes the changes made to implement mandatory crop selection for both camera captures and gallery imports, along with handwriting-oriented preprocessing to improve OCR accuracy.

## Changes Made

### 1. Handwriting-Oriented Preprocessing (ImagePreprocessor.java)

Added three new preprocessing methods specifically optimized for handwriting OCR:

#### `preprocessForHandwriting(Bitmap src)`
- **Purpose**: Primary preprocessing pipeline for handwriting OCR
- **Pipeline**:
  1. Grayscale conversion
  2. Otsu binarization for clean text separation
  3. Sharpening to enhance letter edges
  4. Ultra-high contrast boost for character definition
- **Benefits**: Provides aggressive preprocessing to maximize handwriting recognition accuracy

#### `deskewHandwriting(Bitmap src)`
- **Purpose**: Normalize skewed/rotated handwriting
- **Method**: Simplified Hough transform to detect and correct text angle
- **Threshold**: Only corrects if angle > 0.5 degrees
- **Benefits**: Improves OCR accuracy for tilted/rotated handwriting

#### `detectSkewAngle(Bitmap gray)` (private helper)
- **Purpose**: Detect text skew angle using edge-based analysis
- **Method**: Samples edge pixels and finds dominant orientation
- **Range**: -90 to +90 degrees

#### `rotateImage(Bitmap src, float angle)` (private helper)
- **Purpose**: Rotate image by specified angle
- **Benefits**: Supports deskew correction

### 2. OCR Pipeline Integration (OcrProcessor.java)

Enhanced the multi-variant OCR preprocessing pipeline to prioritize handwriting preprocessing:

- **All quality conditions** now include `preprocessForHandwriting()` as a priority variant
- **Adaptive selection**: Handwriting preprocessing is tried first or early in all scenarios:
  - Blurry images: handwriting → ultra_contrast → sharpened → classroom
  - Low light/contrast: handwriting → ultra_contrast → sharpened → adaptive_histogram
  - Overexposed: adaptive_histogram → handwriting → classroom
  - Good quality: handwriting → light → original → standard
  - Default: handwriting → classroom → light → adaptive_histogram

### 3. Mandatory Crop for Gallery Imports (MainActivity.java)

Completely redesigned gallery import flow to require manual crop for each image:

#### New Fields
- `galleryImportQueue`: Queue of URIs to process
- `galleryMergedAnswers`: Accumulated results from all images
- `galleryTotalImages`: Total images to process
- `galleryProcessedImages`: Count of processed images
- `isProcessingGalleryQueue`: Flag to track queue state

#### Modified Methods

**`onPhotosImported(List<Uri> uris)`**
- **Old behavior**: Processed all images directly with OCR (no crop)
- **New behavior**: 
  - Initializes queue with all imported URIs
  - Shows message about mandatory crop
  - Launches crop activity for first image
  - Subsequent images processed sequentially after each crop

**`processNextGalleryImage()`** (new method)
- Polls next image from queue
- Launches crop activity
- When queue is empty, calls `finishGalleryImport()`

**`finishGalleryImport()`** (new method)
- Updates UI with merged results
- Shows completion status
- Cleans up queue resources

**`onCropResult(ActivityResult result)`**
- **Enhanced**: Now detects if processing gallery queue
- Routes to `processCroppedImageForGalleryQueue()` if in queue mode
- Routes to regular `processCroppedImage()` for camera captures

**`processCroppedImageForGalleryQueue(Uri croppedUri)`** (new method)
- Processes cropped image with OCR
- Merges results into `galleryMergedAnswers`
- Increments `galleryProcessedImages`
- Shows progress toast
- Calls `processNextGalleryImage()` to continue

**`handleCropCancellation()`** (new method)
- Handles user canceling crop during gallery import
- Finishes import with partial results

**`handleCropFailure()`** (new method)
- Skips failed image and continues queue
- Or uses fallback for camera captures

## User Flow Changes

### Camera Capture Flow (UNCHANGED)
1. User taps "Scan" button
2. Camera captures image
3. **Crop activity launches automatically**
4. User crops/rotates/adjusts image
5. User confirms crop
6. OCR processes cropped image with handwriting preprocessing
7. Results displayed

### Gallery Import Flow (NOW REQUIRES CROP)
1. User taps "Import Photos" and selects N images
2. **Crop activity launches for image 1**
3. User crops/rotates/adjusts image 1
4. User confirms crop
5. OCR processes cropped image 1
6. Progress shown: "Processed 1 of N images"
7. **Crop activity launches for image 2**
8. ... repeat for all N images ...
9. After all images processed, merged results displayed
10. Status shows: "Processed N image(s) • Filled X / Y answers"

### Benefits of New Flow
- ✅ **No automatic OCR without crop**: All images require manual crop selection
- ✅ **User control**: User can select exact region of interest for each image
- ✅ **Better accuracy**: Handwriting preprocessing runs on cropped region only
- ✅ **Clear feedback**: Progress shown for each image
- ✅ **Graceful handling**: Cancel/retry options at each step
- ✅ **Memory efficient**: Processes images one at a time

## Technical Details

### Preprocessing Variants Priority
The OCR processor now tries up to 8 preprocessing variants per image, with handwriting preprocessing as a priority:

1. **handwriting**: Aggressive binarization + sharpening + ultra-contrast
2. **ultra_contrast**: Double contrast enhancement
3. **sharpened**: Edge enhancement
4. **adaptive_histogram**: Histogram equalization
5. **classroom**: De-yellow + grayscale + contrast + Otsu
6. **light**: Grayscale + moderate contrast
7. **standard**: Enhanced via ImageUtil
8. **original**: Unmodified image

### Quality-Based Adaptation
- **Blurry**: Prioritizes handwriting, ultra-contrast, sharpening
- **Low light**: Prioritizes handwriting, ultra-contrast, adaptive histogram
- **Overexposed**: Prioritizes adaptive histogram, handwriting
- **Good quality**: Prioritizes handwriting, light processing
- **Default**: Prioritizes handwriting, classroom, light

### Error Handling
- **Crop canceled**: Gallery import stops, partial results saved
- **Crop failed**: Image skipped, continues with next
- **OCR failed**: Shows error, continues with next image
- **Out of memory**: Recycles bitmaps, triggers GC, continues

## Testing Recommendations

1. **Camera capture**: Verify crop still works correctly
2. **Single gallery import**: Test one image through crop → OCR
3. **Multiple gallery imports**: Test 3-5 images, verify sequential crop
4. **Crop cancellation**: Cancel during import, verify partial results
5. **Poor quality images**: Test handwriting preprocessing on blurry/skewed images
6. **Handwriting samples**: Test actual handwritten answer sheets

## Code Review Notes

### Files Modified
- `app/src/main/java/com/bandecoot/itemscoreanalysisprogram/ImagePreprocessor.java`
  - Added 3 new public methods
  - Added 3 new private helper methods
  - Total: ~170 new lines

- `app/src/main/java/com/bandecoot/itemscoreanalysisprogram/OcrProcessor.java`
  - Modified adaptive variant selection logic
  - Added handwriting variant to all branches
  - Total: ~40 lines modified

- `app/src/main/java/com/bandecoot/itemscoreanalysisprogram/MainActivity.java`
  - Added 5 new instance fields for queue management
  - Completely rewrote `onPhotosImported()` method
  - Added 5 new methods for queue processing
  - Enhanced `onCropResult()` to handle both flows
  - Total: ~160 new lines, ~140 lines removed

### Backward Compatibility
- ✅ Camera capture flow unchanged
- ✅ Existing tests still pass
- ✅ OCR processor API unchanged
- ✅ SimpleCropActivity unchanged
- ⚠️ Gallery import UX changed (now requires crop - **intentional**)

### Performance Considerations
- Gallery import is now slower (requires user interaction for each image)
- This is **intentional** to ensure accuracy
- Memory usage improved (sequential processing vs. batch)
- Handwriting preprocessing adds minimal overhead (already in variant pipeline)

## Future Enhancements

Potential improvements for consideration:

1. **Batch crop preview**: Show thumbnails of all images with crop rectangles
2. **Remember crop settings**: Reuse crop area from previous image
3. **Auto-crop suggestions**: ML-based crop rectangle suggestion
4. **Deskew refinement**: More sophisticated skew detection algorithm
5. **Preprocessing preview**: Show preprocessing result before OCR
6. **Quality metrics**: Show confidence scores for OCR results
