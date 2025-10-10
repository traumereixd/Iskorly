# Implementation Changes Summary

## Overview
This document describes the three major changes implemented in this update:

1. **Remove Subgroup Prefill on App Start**
2. **Change Autocomplete Import/Export from JSON to CSV/TXT**
3. **Add Paste Import Dialog for Students/Sections/Exams**

---

## 1. Remove Subgroup Prefill Persistence

### Changes Made
- **loadPersistedInputs()**: Removed logic that loads `PREF_LAST_SECTION` and `PREF_LAST_EXAM` on app startup
- **saveInputValues()**: Removed logic that saves section and exam field values to SharedPreferences

### Behavior
- Section and Exam fields now start **blank** on every cold start of the app
- Autocomplete/recents functionality remains **unchanged** - users can still see and select recent entries via dropdown
- The recents list is still populated and maintained through the `updateRecents()` method when records are saved

---

## 2. Autocomplete Import/Export Format Changes

### Export Changes
- **Format**: Changed from JSON to CSV
- **MIME Type**: `text/csv` instead of `application/json`
- **Filenames**: Changed from `.json` to `.csv` extensions

#### Export Formats

**Per-Category Export** (Students, Sections, or Exams):
```csv
word
Student Name 1
Student Name 2
Section A
```

**All Categories Export**:
```csv
category,word
students,Student Name 1
students,Student Name 2
sections,Section A
exams,Quiz 1
```

### Import Changes
- **Supported Formats**: CSV, TXT, and JSON (backward compatibility)
- **MIME Types**: `text/plain`, `text/csv`, `application/json`, `*/*`

#### Import Logic
1. **JSON Detection**: First tries to parse as JSON for backward compatibility
2. **CSV/TXT Fallback**: If JSON parsing fails, treats content as CSV/plain text

#### CSV Import Parsing

**Per-Category Import**:
- Supports single-column format
- If line contains comma, takes first column value
- Strips leading numbering patterns (e.g., `1.`, `2)`, `3-`)
- Skips header lines (e.g., "word")

**All Categories Import**:
- Expects `category,word` format
- Maps category tokens: `students`, `sections`, `exams`
- Skips header line (e.g., "category,word")

---

## 3. Paste Import Dialog

### New User Flow

#### For Single Category Import (Students/Sections/Exams):
1. User clicks "Import" button
2. **Choice Dialog** appears with options:
   - **"Paste Text"**: Opens paste import dialog
   - **"Choose File"**: Opens file picker (existing flow)
   - **"Cancel"**: Closes dialog

#### Paste Import Dialog:
- **Title**: "Paste to Import — [Category]"
- **Input**: Multiline text field (10-20 lines)
- **Hint**: Shows supported formats
- **Buttons**: Import / Cancel

### Smart Text Parser

The `parseSmartText()` method handles various input formats:

#### Supported Formats:
```
1. SAHAGUN, JAYSON G.
2) BANDE, HEZEKIAH G.
3 - SMITH, JOHN A.

SAHAGUN, JAYSON G.
BANDE, HEZEKIAH G.
SMITH, JOHN A.
```

#### Parser Features:
- **Strips numbering**: Removes patterns like `1.`, `2)`, `3-`, etc.
- **Trims punctuation**: Removes trailing `.`, `,`, `;`, `!`, `?`
- **Discards blanks**: Skips empty lines
- **Preserves spacing**: Keeps original spacing within names

---

## Technical Details

### New Methods Added

1. **showImportChoiceDialog(String category)**
   - Displays dialog with Paste vs File choice
   - Handles user selection

2. **showPasteImportDialog(String category)**
   - Creates multiline EditText for pasting
   - Displays import dialog

3. **importFromPastedText(String category, String text)**
   - Processes pasted text
   - Saves to SharedPreferences
   - Updates UI

4. **parseSmartText(String text)**
   - Parses flexible text formats
   - Returns list of cleaned entries

5. **capitalize(String str)**
   - Helper to capitalize category names

6. **importFromCsvAll(String content)**
   - Parses category,word CSV format
   - Imports multiple categories at once

7. **importFromCsvSingle(String content, String category)**
   - Parses single-column CSV/TXT
   - Imports to one category

### Modified Methods

1. **importAutocompleteFromUri(Uri uri)**
   - Added JSON detection and fallback to CSV/TXT
   - Maintains backward compatibility

2. **exportAutocompleteToUri(Uri uri)**
   - Changed output format to CSV
   - Generates proper CSV with headers

3. **setupAutocompleteManagerListeners()**
   - Updated Import buttons to show choice dialog
   - Changed export filenames to .csv
   - Updated MIME types for import launchers

### Button Click Handlers Updated:
- `buttonImportStudentsJson`: Now calls `showImportChoiceDialog("students")`
- `buttonImportSectionsJson`: Now calls `showImportChoiceDialog("sections")`
- `buttonImportExamsJson`: Now calls `showImportChoiceDialog("exams")`
- All Export buttons: Changed filenames from `.json` to `.csv`

---

## Backward Compatibility

### JSON Import Support
- Old JSON files can still be imported
- Format detection is automatic
- No user action required for migration

### Example JSON (Still Supported):
```json
{
  "students": ["Student 1", "Student 2"],
  "sections": ["Section A", "Section B"],
  "exams": ["Quiz 1", "Quiz 2"]
}
```

---

## Testing Recommendations

### Test Case 1: Subgroup Prefill
1. Enter Section and Exam values
2. Close and reopen app
3. Verify fields are **blank**
4. Verify recent entries still appear in dropdown

### Test Case 2: CSV Export
1. Export Students/Sections/Exams individually
2. Verify CSV format with "word" header
3. Export All categories
4. Verify CSV format with "category,word" header

### Test Case 3: CSV Import
1. Create CSV file with single column format
2. Import to Students category
3. Verify entries loaded correctly

### Test Case 4: Paste Import
1. Click Import on Students button
2. Choose "Paste Text"
3. Paste numbered list (e.g., "1. Name")
4. Verify import strips numbering
5. Verify entries appear in UI

### Test Case 5: JSON Backward Compatibility
1. Export as old JSON format
2. Import the JSON file
3. Verify successful import

### Test Case 6: Smart Parser
Test various formats:
- `1. NAME`
- `2) NAME`
- `NAME` (plain)
- Mixed formats in same paste

---

## Error Handling

### Import Errors
- Empty content: Shows "No valid entries found"
- Invalid format: Shows error toast with message
- File read errors: Logged and displayed to user

### Export Errors
- File write errors: Logged and displayed to user
- Permission issues: Handled by Android system

---

## Code Quality

### Best Practices Applied
- ✅ Minimal changes to existing code
- ✅ Backward compatibility maintained
- ✅ Proper error handling and logging
- ✅ User-friendly toast messages
- ✅ Consistent coding style
- ✅ Reused existing helper methods (escapeCsv)

### No Breaking Changes
- All existing features continue to work
- JSON import still supported
- Recents/autocomplete unchanged
- No data loss for existing users
