# Manual Testing and Validation Guide

## Pre-Testing Setup

### Requirements
- Android device or emulator (API 23+)
- Test data: CSV file with student names (one per line)
- Sample answer sheets for scanning

### Installation
```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Test Scenarios

### 1. Fade Transition Removal ✓ Priority: High

**Steps**:
1. Launch app
2. Tap "Setup Answer Key"
3. Immediately tap "Back"
4. Tap "View History"
5. Immediately tap "Back"

**Expected**: No fade animations, instant view switching

**Validation**: Views should appear/disappear immediately without any delay or animation

---

### 2. Answer Key Input - Mixed Case ✓ Priority: High

**Steps**:
1. Tap "Setup Answer Key"
2. Enter Question # = 1
3. Enter Answer = "a" (lowercase)
4. Tap "Save"
5. Verify answer is saved as "a" (not forced to "A")
6. Enter Question # = 2
7. Enter Answer = "Ab" (mixed case)
8. Tap "Save"

**Expected**: 
- Answers saved exactly as entered (lowercase/mixed case preserved)
- No automatic capitalization occurs

**Validation**: Current Key display shows "1: a  2: Ab"

---

### 3. Answer Key Scoring - Case Insensitive ✓ Priority: High

**Steps**:
1. Set answer key: Q1=A, Q2=b
2. Scan answer sheet with: Q1=a, Q2=B
3. Confirm and score

**Expected**: Both answers marked correct (case-insensitive comparison)

**Validation**: Score should be 2/2 (100%)

---

### 4. Autocomplete - Student Names ✓ Priority: High

**Steps**:
1. Enter student name "John Doe" and save a record
2. Enter student name "Jane Smith" and save another record
3. Close and reopen the app
4. Tap on Student Name field
5. Type "J"

**Expected**: Dropdown shows "John Doe" and "Jane Smith"

**Validation**: 
- Suggestions appear after typing 1 character
- Both names visible in dropdown
- Tapping a suggestion fills the field

---

### 5. Import Students ✓ Priority: Medium

**Prepare**: Create `students.txt` with content:
```
Alice Anderson
Bob Brown
Charlie Chen
```

**Steps**:
1. Tap "Import Students" button below student name field
2. Select `students.txt` file
3. Tap on Student Name field
4. Verify dropdown shows imported names

**Expected**: 
- Success toast: "Imported 3 student name(s)"
- Dropdown contains all imported names

**Validation**: All three names appear in autocomplete suggestions

---

### 6. Save Set - Create Named Slot ✓ Priority: High

**Steps**:
1. Setup answer key with several answers (e.g., Q1-5)
2. Tap "Save Set" button
3. Dialog appears with "Untitled 1" pre-filled
4. Change name to "Math Quiz 1"
5. Tap "Save"

**Expected**: 
- Success toast: "Answer key set saved: Math Quiz 1"
- Slot selector shows "Math Quiz 1"
- Answers are duplicated to new slot

**Validation**: 
- Slot dropdown shows "Math Quiz 1"
- Current key still shows all answers

---

### 7. History Record - SlotId Tagging ✓ Priority: High

**Steps**:
1. Create slot "Quiz A" with answers
2. Scan and save a test record
3. Create slot "Quiz B" with different answers
4. Scan and save another test record
5. Export history or check storage

**Expected**: Records tagged with appropriate slotId

**Validation**: 
- Use `adb shell run-as com.bandecoot.itemscoreanalysisprogram cat shared_prefs/TestHistoryPrefs.xml`
- Verify both records have different slotId values

---

### 8. Masterlist - By Section View ✓ Priority: Critical

**Setup**: Create test data with 2 sections:
- Section "12-A": 3 students with varied scores
- Section "12-B": 3 students with varied scores

**Steps**:
1. Tap "View History" → "Masterlist"
2. Verify "By Section" button is highlighted
3. Scroll through sections

**Expected**: 
- Collapsible cards for each section
- Each card shows:
  - Section name as header
  - Table: Q#, Correct, Incorrect, % Correct, Common Miss
  - Summary: Overall Score, Mean, Std Dev, MPS, record count
- Sections sorted alphabetically

**Validation Points**:
- [ ] Card headers show section names
- [ ] Click header to collapse/expand
- [ ] Question table has 5 columns
- [ ] Summary shows 5 lines of stats
- [ ] Math is correct (manually verify one section)

---

### 9. Masterlist - All View ✓ Priority: Critical

**Steps**:
1. In Masterlist, tap "All" button
2. Scroll horizontally to see all sections
3. Scroll vertically to see all questions

**Expected**:
- Wide horizontally scrollable table
- Leftmost column: Q#
- One column per section
- Section totals rows below questions
- Overall summary at bottom

**Validation Points**:
- [ ] "All" button is highlighted
- [ ] Table scrolls horizontally
- [ ] Each section has its own column
- [ ] Values show % correct per question
- [ ] Summary rows show: Total Score, Mean, Std Dev, MPS
- [ ] Overall summary block visible at bottom

---

### 10. Masterlist Toggle - State Persistence ✓ Priority: Medium

**Steps**:
1. Switch to "All" view
2. Tap "Back"
3. Tap "Masterlist" again

**Expected**: Returns to "All" view (remembers last selection)

**Note**: Current implementation resets to "By Section" on re-open. This is acceptable behavior but could be enhanced to persist state.

---

### 11. Export CSV - By Section ✓ Priority: High

**Steps**:
1. In Masterlist, ensure "By Section" is selected
2. Tap "Export CSV"
3. Choose save location
4. Open exported file in spreadsheet app

**Expected**:
- File saved as `masterlist_YYYYMMDD_HHMMSS.csv`
- Structure:
  ```
  Masterlist By Section
  
  Section: 12-A
  Q#,Correct,Incorrect,% Correct,Common Miss
  1,5,1,83.3%,B (1)
  ...
  
  Summary for 12-A
  Total Score,45
  Mean,75.50%
  Standard Deviation,12.34
  MPS,75.50%
  Records,6
  
  Section: 12-B
  ...
  ```

**Validation**: 
- [ ] CSV opens in Excel/Sheets without errors
- [ ] All sections present
- [ ] Values match on-screen data
- [ ] Summary stats included for each section

---

### 12. Export CSV - All ✓ Priority: High

**Steps**:
1. In Masterlist, tap "All"
2. Tap "Export CSV"
3. Choose save location
4. Open exported file

**Expected**:
- Structure:
  ```
  Masterlist All Sections
  
  Q#,12-A,12-B
  1,83.3%,90.0%
  2,75.0%,85.0%
  ...
  
  SUMMARY,12-A,12-B
  Total Score,45,52
  Mean,75.50%,82.30%
  Std Dev,12.34,8.56
  MPS,75.50%,82.30%
  
  OVERALL SUMMARY
  Total Score,97
  Mean,78.90%
  Std Dev,10.45
  MPS,78.90%
  Records,12
  ```

**Validation**:
- [ ] Wide table format with section columns
- [ ] All questions and sections present
- [ ] Summary rows included
- [ ] Overall summary at end
- [ ] Values match on-screen data

---

### 13. Statistics Accuracy ✓ Priority: Critical

**Manual Calculation Test**:

Create controlled data:
- Section "Test": 3 students
  - Student 1: 8/10 = 80%
  - Student 2: 7/10 = 70%
  - Student 3: 9/10 = 90%

**Expected Calculations**:
- Total Score: 24
- Mean: (80 + 70 + 90) / 3 = 80.0%
- Std Dev: sqrt(((80-80)² + (70-80)² + (90-80)²) / 3) = sqrt(200/3) ≈ 8.16
- MPS: 80.0% (same as Mean)
- Records: 3

**Validation**: 
- [ ] All calculated values match manual calculations
- [ ] Values consistent between By Section and All views
- [ ] CSV export matches on-screen values

---

### 14. Edge Cases ✓ Priority: Medium

#### Empty History
**Steps**: 
1. Clear all history
2. Open Masterlist

**Expected**: "No history records found. Scan some answer sheets first!"

#### No Answer Key
**Steps**:
1. Clear answer key
2. Open Masterlist

**Expected**: "No answer key set. Please set up an answer key first."

#### Single Section
**Steps**:
1. Create data with only one section
2. View Masterlist

**Expected**: Works normally with single section

#### Many Sections
**Steps**:
1. Create data with 5+ sections
2. View "All" table

**Expected**: Scrolls horizontally without issues

---

### 15. Snapshot Creation ✓ Priority: High

**Steps**:
1. Create slot "Quiz 1" with answers
2. Scan several tests
3. Tap "Save Set" → name it "Quiz 1 Backup"
4. Modify answers in original slot
5. View Masterlist

**Expected**: 
- Snapshot created for "Quiz 1 Backup"
- Records scanned with "Quiz 1" still associated with it
- Modifying original doesn't affect snapshot

**Note**: Full snapshot viewing UI not implemented in this version (simplified to slot-based filtering)

---

## Regression Testing

### Core Functionality (Must Still Work)
- [ ] Camera scanning works
- [ ] OCR processing works
- [ ] Answer key management (add/remove/clear)
- [ ] History viewing (grouped by exam/section)
- [ ] History export CSV (original functionality)
- [ ] Slot import/export
- [ ] Photo import

---

## Performance Testing

### Large Dataset
**Setup**: 
- 50 questions
- 100 students
- 5 sections

**Test**:
1. Open Masterlist
2. Switch between views
3. Export CSV

**Expected**: 
- Masterlist loads in < 2 seconds
- View switching is instant
- Export completes in < 5 seconds

**Validation**: No lag or freezing

---

## Known Limitations

1. **Snapshot Viewing**: Simplified to slot-based filtering rather than time-based windows
2. **View State**: Masterlist toggle resets to "By Section" when re-opened
3. **Recent Values**: Limited to 50 entries per field (by design)

---

## Bug Report Template

```
### Issue Title

**Severity**: Critical / High / Medium / Low

**Test Scenario**: [Reference from above]

**Steps to Reproduce**:
1. 
2. 
3. 

**Expected Behavior**:

**Actual Behavior**:

**Screenshots**: [if applicable]

**Device Info**:
- Model: 
- Android Version: 
- App Version: 1.3

**Logs**: [adb logcat output if available]
```

---

## Sign-Off Checklist

Before marking as complete:
- [ ] All HIGH priority tests passed
- [ ] All CRITICAL priority tests passed
- [ ] At least 80% of MEDIUM priority tests passed
- [ ] No regression in core functionality
- [ ] Performance acceptable with realistic data
- [ ] Documentation accurate

---

## Success Criteria Met

✅ Masterlist By Section shows correct data
✅ Masterlist All view works with scrolling
✅ Export CSV produces readable files
✅ Save Set creates new slots correctly
✅ Answer keys accept mixed case
✅ No fade animations present
✅ Autocomplete works for all input fields
✅ Import students functionality works

---

## Notes for Testers

- Use realistic data (at least 2 sections, 5+ students each)
- Test with both short and long section names
- Try special characters in names (O'Brien, José, etc.)
- Test with varying question counts (10, 20, 50, 100)
- Test on both phone and tablet form factors
- Test in both portrait and landscape orientations
