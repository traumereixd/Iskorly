# Quick Testing Guide

## How to Test the Three New Features

### Feature 1: Blank Subgroup Fields on App Start

**What Changed:**
- Section and Exam fields NO LONGER auto-fill with last used values on app start
- Fields are now blank every time you open the app

**How to Test:**
1. Open the app
2. Enter a Section name (e.g., "Section A")
3. Enter an Exam name (e.g., "Quiz 1")
4. Close the app completely
5. Reopen the app
6. **VERIFY**: Section and Exam fields should be **BLANK**
7. Click on Section or Exam field dropdown
8. **VERIFY**: Recent entries should still appear in the dropdown

**Expected Result:** ✅ Fields blank on start, but recents still available

---

### Feature 2: CSV Import/Export (replacing JSON)

**What Changed:**
- Export format changed from JSON to CSV
- Import accepts CSV, TXT, and JSON (backward compatible)

**How to Test Export:**
1. Open app and click "Manage Autocomplete"
2. Add some test students (e.g., "John Smith", "Jane Doe")
3. Click "Export" button for Students
4. **VERIFY**: Default filename is `autocomplete_students.csv` (not .json)
5. Save the file
6. Open the CSV file
7. **VERIFY**: Format should be:
   ```csv
   word
   John Smith
   Jane Doe
   ```

**How to Test Import (CSV):**
1. Create a text file with this content:
   ```
   Student One
   Student Two
   Student Three
   ```
2. Save as `test.csv` or `test.txt`
3. In app, click "Import" for Students
4. Choose "Choose File"
5. Select your test file
6. **VERIFY**: Toast shows "Imported 3 students"
7. **VERIFY**: Students appear in the chips

**How to Test Import (JSON - Backward Compatibility):**
1. Create a JSON file:
   ```json
   {
     "students": ["Test Student 1", "Test Student 2"]
   }
   ```
2. Import the file
3. **VERIFY**: Import works successfully

---

### Feature 3: Paste Import Dialog

**What Changed:**
- Import buttons now show a choice: "Paste Text" or "Choose File"
- Paste dialog allows direct text entry without file
- Smart parser handles numbered lists

**How to Test:**
1. Open app and click "Manage Autocomplete"
2. Click "Import" button for Students
3. **VERIFY**: Dialog appears with two buttons:
   - "Paste Text"
   - "Choose File"
   - "Cancel"

4. Click "Paste Text"
5. **VERIFY**: Large text input dialog appears
6. Paste this test data:
   ```
   1. SAHAGUN, JAYSON G.
   2) BANDE, HEZEKIAH G.
   3- SMITH, JOHN A.
   JONES, MARY B.
   ```
7. Click "Import"
8. **VERIFY**: Toast shows "Imported 4 entries"
9. **VERIFY**: Names appear in chips WITHOUT numbering
10. **VERIFY**: Names are: "SAHAGUN, JAYSON G", "BANDE, HEZEKIAH G", "SMITH, JOHN A", "JONES, MARY B"

**Formats the Parser Handles:**
- `1. NAME` → strips "1."
- `2) NAME` → strips "2)"
- `3- NAME` → strips "3-"
- `NAME` → keeps as-is
- Blank lines → ignored
- Trailing punctuation → removed

**Edge Cases to Test:**
```
1. Student Name,
2) Another Name.
3 Student Three

Student Four
```
Result should be 4 clean names without numbering or trailing punctuation.

---

## Complete Test Flow

### Scenario: Teacher Adding Students from Class List

**Setup:**
Teacher receives class list via email in this format:
```
Grade 10 - Section A Students:
1. DELA CRUZ, JUAN A.
2. SANTOS, MARIA B.
3. REYES, PEDRO C.
4. GARCIA, ANA D.
```

**Test Steps:**
1. Open Iskorly app
2. Click "Manage Autocomplete"
3. Click "Import" button for Students
4. Choose "Paste Text"
5. Copy the list above and paste it
6. Click "Import"
7. **VERIFY**: Toast shows "Imported 4 entries"
8. **VERIFY**: All 4 names appear in chips without numbers
9. **VERIFY**: Names cleaned: "DELA CRUZ, JUAN A." → "DELA CRUZ, JUAN A" (no trailing period)

**Export Test:**
10. Click "Export" button for Students
11. Save as `students_backup.csv`
12. Open the CSV file
13. **VERIFY**: Contains:
    ```csv
    word
    DELA CRUZ, JUAN A
    SANTOS, MARIA B
    REYES, PEDRO C
    GARCIA, ANA D
    ```

**Re-import Test:**
14. Click "Clear All" to remove all students
15. Click "Import" for Students
16. Choose "Choose File"
17. Select `students_backup.csv`
18. **VERIFY**: All 4 students imported successfully

---

## Verification Checklist

- [ ] Section/Exam fields blank on cold start
- [ ] Recent entries still appear in dropdown
- [ ] Export creates CSV files (not JSON)
- [ ] CSV export has proper headers
- [ ] Import accepts CSV files
- [ ] Import accepts TXT files
- [ ] Import still accepts JSON (backward compat)
- [ ] Import button shows choice dialog
- [ ] Paste import dialog has large text area
- [ ] Parser strips numbering (1., 2), etc.)
- [ ] Parser removes blank lines
- [ ] Parser trims trailing punctuation
- [ ] Import success shows count toast
- [ ] All imported items appear in UI

---

## Known Working Formats

### Paste Import Formats:
✅ Numbered with period: `1. NAME`
✅ Numbered with parenthesis: `2) NAME`
✅ Numbered with dash: `3- NAME`
✅ Numbered with space: `4 NAME`
✅ Plain names: `NAME`
✅ Mixed formats in same paste

### CSV Import Formats:
✅ Single column (one name per line)
✅ With header: `word` at top
✅ Without header: just names
✅ With comma in name: proper escaping

### JSON Import (Backward Compat):
✅ Old format: `{"students": ["Name1", "Name2"]}`
✅ All categories: `{"students": [...], "sections": [...], "exams": [...]}`

---

## Troubleshooting

**Problem:** Import doesn't work
- Check file format (CSV, TXT, or JSON)
- Ensure file has content
- Try paste import instead

**Problem:** Numbers still appear in names
- Verify format is supported (1., 2), 3-, etc.)
- Check for unusual numbering (e.g., Roman numerals not supported)

**Problem:** Some names missing
- Check for blank lines (they're skipped)
- Verify names aren't empty after cleaning

**Problem:** CSV export opens in JSON
- Check file extension is .csv
- Some apps may show JSON for old exports (delete and re-export)
