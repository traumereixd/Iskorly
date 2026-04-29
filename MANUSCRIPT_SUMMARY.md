# Research Manuscript Summary

## File Information
- **Filename**: `research-manuscript.txt`
- **Title**: "Iskorly: Vision-only OCR UI/UX Evaluation via Brief Demonstrations and Descriptive Analysis"
- **Version Evaluated**: Iskorly v1.8.1
- **Length**: 594 lines, ~42KB

## Document Structure

The manuscript contains the following sections:

1. **Title Page** - Research team, developer, version, and date
2. **Chapter I: Introduction** - Background, problem statement, objectives, hypotheses, significance, scope & limitations
3. **Chapter II: Conceptual and Theoretical Framework** - Conceptual framework, theoretical framework, related literature
4. **Chapter III: Methodology** - Research design, participants, materials, procedure, instruments, statistical treatment, data handling, ethical considerations
5. **Definition of Terms** - Key terminology definitions
6. **Chapter IV: Results and Discussion** - UI/UX results with descriptive statistics and interpretation
7. **Chapter V: Summary, Conclusions, and Recommendations** - Summary, conclusions, recommendations for teachers/developers/researchers, limitations
8. **References** - Academic citations
9. **Appendices** - Survey instruments, demographic info, VI ranges

## Key Compliance Points

### ✅ All Acceptance Criteria Met

#### 1. OCR Engine References
- ✅ **Zero** occurrences of "OCR.Space" (REMOVED)
- ✅ 14+ mentions of "Google Cloud Vision API" (vision-only approach)
- ✅ Explicitly states "no fallback engines" for clarity

#### 2. Storage Technology
- ✅ **Zero** occurrences of "SQLite" (REMOVED)
- ✅ 10+ mentions of "SharedPreferences (JSON)"
- ✅ 23+ mentions of "CSV export" functionality

#### 3. Application Version
- ✅ Consistent use of "Iskorly v1.8.1" throughout (16 mentions)

#### 4. Methodology
- ✅ Demo-only approach with 30 teachers
- ✅ 3–5 minute individual sessions clearly described
- ✅ Three-phase procedure: Orientation (~30-60s), Live Demo (~2-3m), Survey (~1-2m)
- ✅ 20-item Likert survey (10 UI + 10 UX items)

#### 5. Statistical Treatment
- ✅ Descriptive statistics ONLY (Mean, Standard Deviation, Verbal Interpretation)
- ✅ Explicit statement: "no ANOVA, t-tests, or p-values"
- ✅ Mathematical formulas provided:
  - Mean: x̅ = (∑ xᵢ)/n
  - Sample SD: s = √[∑(xᵢ − x̅)² / (n − 1)]
- ✅ Verbal Interpretation ranges defined:
  - 1.00–1.79: SD (Strongly Disagree)
  - 1.80–2.59: D (Disagree)
  - 2.60–3.39: N (Neutral)
  - 3.40–4.19: A (Agree)
  - 4.20–5.00: SA (Strongly Agree)

#### 6. Research Focus
- ✅ UI/UX evaluation via demo and survey
- ✅ NO accuracy/time benchmarking experiments
- ✅ NO OCR settings efficacy experiments
- ✅ Focus on usability: clarity, navigation, responsiveness, correction ease, analytics utility

#### 7. Research Questions & Hypotheses
- ✅ RQ1: UI quality via mean, SD, VI
- ✅ RQ2: UX quality via mean, SD, VI
- ✅ Descriptive thresholds: H₀ (x̅ < 4.00) vs. Hₐ (x̅ ≥ 4.00)

#### 8. Theoretical Framework
- ✅ Basic Information Processing Theory (input → recognition → post-processing → output)
- ✅ Diffusion of Innovations Theory (Rogers)
- ✅ HCI principles integrated throughout

#### 9. Features Described
- ✅ Capture → optional crop → OCR → editable grid → score → save
- ✅ Masterlist analytics (MPS, per-item stats, common wrong answers)
- ✅ CSV export for data portability
- ✅ UI/UX aids: two-column grid, high-contrast toggle, global black text, large-text option, haptics

#### 10. Ethical Considerations
- ✅ Informed consent, voluntary participation
- ✅ Privacy and confidentiality (anonymous responses)
- ✅ Local storage via SharedPreferences (JSON)
- ✅ Minimal data retained; only OCR requests sent to Google Cloud Vision API
- ✅ API key security emphasized

## Chapter Highlights

### Chapter III: Methodology (Most Critical)
This chapter was completely rewritten to reflect the **actual evaluation methodology**:
- **No experimental design** for accuracy/time/settings
- **Demo-only sessions** with individual teachers
- **Brief 3–5 minute exposure** per participant
- **Immediate survey administration** capturing first impressions
- **Descriptive analysis only** without inferential statistics

### Chapter IV: Results and Discussion
- Placeholder data structure provided for future field implementation
- Per-item means, SDs, and VIs presented in table format
- Overall UI and UX scores with interpretations
- Discussion connects findings to HCI principles and Diffusion of Innovations Theory
- Acknowledges device/environment variability in modest-rated items

### Chapter V: Conclusions and Recommendations
- Recommendations for **teachers** (onboarding, CSV templates, communities)
- Recommendations for **developers** (capture guidance, responsiveness, privacy-preserving OCR, adaptive parsing)
- Recommendations for **researchers** (broader samples, longitudinal studies, device thresholds, accessibility, LMS integrations)
- Limitations clearly stated (short exposure, convenience sampling, no accuracy/time experiments)

## Definition of Terms
All key terms defined with **no OCR.Space entries**:
- Google Cloud Vision API
- SharedPreferences (JSON)
- Two-Column Mode
- High-Contrast Mode
- Masterlist
- CSV
- Vision-Only OCR
- Editable Grid
- Haptic Feedback
- Large-Text Option
- Global Black Text
- Verbal Interpretation (VI)
- Descriptive Statistics
- Demo-Only Methodology

## Appendices Included
1. **Appendix A**: Full UI Survey Instrument (10 items with Likert scale)
2. **Appendix B**: Full UX Survey Instrument (10 items with Likert scale)
3. **Appendix C**: Demographic Information (aggregate summary)
4. **Appendix D**: Verbal Interpretation Ranges table

## Validation Results

All 15+ validation checks **PASSED**:
- ✅ No OCR.Space references
- ✅ No SQLite references
- ✅ SharedPreferences extensively mentioned
- ✅ Google Cloud Vision API extensively mentioned
- ✅ Version v1.8.1 consistently used
- ✅ Demo methodology clearly described
- ✅ 20-item survey structure (10 UI + 10 UX)
- ✅ Descriptive statistics only (no inferential)
- ✅ Mean, SD, VI formulas present
- ✅ VI ranges properly defined
- ✅ Correct manuscript title
- ✅ CSV export extensively discussed
- ✅ All chapters present
- ✅ Definition of Terms section included
- ✅ Analytics features (MPS, masterlist, common errors) emphasized
- ✅ UI/UX hypotheses with 4.00 threshold
- ✅ Basic Information Processing Theory
- ✅ Diffusion of Innovations Theory
- ✅ Ethical considerations section
- ✅ Research questions (RQ1, RQ2)
- ✅ Survey instruments in appendices

## Next Steps (for Researchers)

This manuscript is **ready for field implementation**. To complete the study:

1. **Prepare Materials**:
   - Print 30 copies of the 20-item survey (Appendices A & B)
   - Configure Android device with Iskorly v1.8.1 and Google Cloud Vision API key
   - Prepare plain paper and pens for sample answers
   - Create quick answer key in app for demo

2. **Recruit Participants**:
   - Identify 30 Filipino secondary school teachers
   - Obtain informed consent
   - Schedule individual 3–5 minute sessions

3. **Conduct Evaluations**:
   - Follow three-phase procedure (Orientation, Demo, Survey)
   - Record survey responses anonymously
   - Ensure consistent demo workflow across all 30 sessions

4. **Analyze Data**:
   - Calculate means, SDs, and VIs for each of 20 items
   - Calculate overall UI mean, SD, VI (10 items)
   - Calculate overall UX mean, SD, VI (10 items)
   - Replace placeholder values in Chapter IV tables
   - Compute Cronbach's α for reliability (optional)

5. **Finalize Manuscript**:
   - Update Chapter IV with actual results
   - Refine discussion based on real findings
   - Submit for review or publication

## Contact

For questions about this manuscript or the Iskorly project:
- **Developer**: Sahagun, Jayson G.
- **Repository**: traumereixd/Iskorly
- **Version**: v1.8.1
