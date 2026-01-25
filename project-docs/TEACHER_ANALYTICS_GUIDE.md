# Teacher's Guide: New Analytics Features

## What's New?

ISA v1.2 brings powerful item analysis tools to help you understand test quality and identify learning gaps.

## New Analytics in Masterlist

### By Section View - New Columns

When you open Masterlist and view "By Section", you'll see these new columns:

#### 📊 Difficulty (0.00 to 1.00)
**What it means:** How easy the question is
- **0.90-1.00**: Very easy - almost everyone got it right
- **0.70-0.89**: Easy - most students succeeded
- **0.50-0.69**: Medium difficulty - half or more correct
- **0.30-0.49**: Challenging - less than half correct
- **0.00-0.29**: Very hard - most students missed it

**When to review:**
- Very easy (>0.90): Question might be too simple or obvious
- Very hard (<0.30): Question might be too difficult, ambiguous, or miskeyed

#### 🎯 Discrimination (usually -1.00 to +1.00)
**What it means:** How well the question separates strong from weak students
- **> 0.30**: Excellent - high performers got it right, low performers missed it
- **0.20-0.29**: Good - acceptable discrimination
- **0.10-0.19**: Poor - doesn't distinguish well between ability levels
- **< 0.10**: Very poor or negative - review this question!

**When to review:**
- Negative values: Strong students missed it while weak students got it right (usually means a problem!)
- Very low (<0.10): Question doesn't help measure knowledge effectively

#### 📈 Upper% and Lower%
**What they mean:** Performance comparison of your best and weakest students
- **Upper%**: Percentage of top 27% students who got it right
- **Lower%**: Percentage of bottom 27% students who got it right

**What to look for:**
- **Large gap (>30%)**: Good question - discriminates well
- **Small gap (<20%)**: Question doesn't distinguish ability levels
- **Negative gap (Upper% < Lower%)**: Red flag - review the question!

### Section Summary - New Metrics

At the bottom of each section, you'll see:

#### 🔒 Reliability Metrics

**KR-20 and Cronbach's Alpha (0.00 to 1.00):**
- **> 0.80**: Excellent test consistency
- **0.70-0.79**: Good test consistency
- **0.60-0.69**: Acceptable for informal assessments
- **< 0.60**: Test may be unreliable - consider revision

**What they measure:** How consistently the test measures student knowledge

**SEM (Standard Error of Measurement):**
- Lower is better
- Represents typical error in student scores
- Example: If SEM = 3, a score of 80 really means 77-83

## Understanding the Heatmap (All View)

When you switch to "All" view, you'll see a color-coded table:

### Color Guide
- 🟢 **Green (85-100%)**: Excellent mastery
- 🟢 **Light Green (70-84%)**: Good performance
- 🟡 **Yellow (50-69%)**: Needs improvement
- 🔴 **Red (0-49%)**: Significant difficulty

### How to Use It
1. **Spot patterns:** Entire rows/columns of red indicate problem areas
2. **Compare sections:** See which classes struggle with which topics
3. **Track progress:** Use over time to see improvement

## Practical Applications

### Use Case 1: Reviewing a Test After Grading

**Step 1:** Open Masterlist → By Section
**Step 2:** Look for questions with:
- Difficulty > 0.95 (too easy)
- Difficulty < 0.30 (too hard)
- Discrimination < 0.10 (not working well)
- Negative discrimination (serious problem!)

**Step 3:** Review flagged questions:
- Check answer key for errors
- Read question wording - is it clear?
- Consider difficulty level vs. grade appropriateness

### Use Case 2: Identifying Learning Gaps

**Step 1:** Switch to All view
**Step 2:** Look for rows (questions) with mostly red/yellow
**Step 3:** These topics need re-teaching across all sections
**Step 4:** Export CSV to share with other teachers

### Use Case 3: Comparing Class Performance

**Step 1:** All view shows each section as a column
**Step 2:** Compare color patterns between sections
**Step 3:** Identify which classes need extra support
**Step 4:** Check section summaries for reliability

### Use Case 4: Improving Test Quality

**Good Test Characteristics:**
- Average difficulty: 0.50-0.70 (not too easy, not too hard)
- Most items: discrimination > 0.20
- Reliability (KR-20): > 0.70
- Mix of easy, medium, and hard questions

**Red Flags:**
- Many items with discrimination < 0.10
- Several items with negative discrimination
- Reliability < 0.60
- All items too easy (>0.90) or too hard (<0.30)

## Tips for Teachers

### 1. Start Simple
Don't try to analyze everything at once. Begin with:
- Difficulty: Which questions are hardest/easiest?
- Discrimination: Which questions don't work well?

### 2. Focus on Action Items
Look for:
- ❌ Questions that need revision (poor discrimination)
- 📚 Topics that need re-teaching (low performance across sections)
- ✅ Strong questions to keep for future tests

### 3. Use the CSV Export
Export your analysis to:
- Share with department colleagues
- Include in reports for administration
- Track improvements over multiple test cycles

### 4. Combine with Qualitative Feedback
Numbers tell part of the story:
- Ask students which questions were confusing
- Review answer explanations for common misconceptions
- Consider pacing - did students have enough time?

### 5. Build a Question Bank
Track questions over time:
- Keep questions with high discrimination
- Revise or retire questions with poor statistics
- Balance test difficulty by mixing items

## Common Questions

**Q: My discrimination values are all negative. What's wrong?**
A: This usually means your answer key has errors. Double-check your correct answers!

**Q: Why does my test have low reliability (KR-20 < 0.60)?**
A: Common causes:
- Test is too short (< 10 questions)
- Questions are too easy or too hard (no variance)
- Questions measure different skills (not consistent)
- Answer key errors

**Q: Should I remove very easy questions?**
A: Not necessarily! A few easy "warm-up" questions can boost student confidence. Just don't make everything easy.

**Q: What if I have a small class (< 10 students)?**
A: Statistics become less reliable with small groups. Focus on difficulty and common wrong answers instead of discrimination.

**Q: Can I compare tests from different quarters?**
A: Only if they're on the same topics and similar difficulty. The numbers are relative to each specific test.

## Quick Reference Chart

| Metric | Good Range | Action Needed If |
|--------|-----------|------------------|
| Difficulty | 0.40-0.80 | < 0.30 or > 0.90 |
| Discrimination | > 0.20 | < 0.10 or negative |
| Upper% - Lower% | > 20% | < 20% or negative |
| KR-20 / Alpha | > 0.70 | < 0.60 |

## Need More Help?

1. Review the yellow legend at the top of By Section view
2. Check ANALYTICS_IMPLEMENTATION.md for technical details
3. Consult with your department head or assessment coordinator

## Remember

These analytics are **tools**, not **judgments**:
- Use them to improve teaching and testing
- Combine with your professional judgment
- Focus on helping students learn, not just measuring them

---

**Version:** ISA v1.2
**Last Updated:** 2025-10-10
