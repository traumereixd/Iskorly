# Smart Answer Strip - User Guide

## What is the Smart Answer Strip?

The Smart Answer Strip is a printable margin strip that students can fill alongside any exam to dramatically improve OCR reliability for multiple-choice, true/false, and matching questions. By providing a consistent, high-contrast format for answers, the strip makes scanning more accurate and faster.

## Key Benefits

- **Higher OCR Accuracy**: Structured answer bubbles are easier for the OCR engine to read than handwritten answers scattered across a page
- **Faster Grading**: The strip concentrates all answers in one vertical region, making scanning more efficient
- **Works with Any Test**: Simply attach the strip to the margin of any existing exam
- **No Special Equipment**: Print on regular paper using any printer

## How to Use the Strip

### For Teachers

1. **Print the Strip**
   - Download [strip_v1.pdf](strip_v1.pdf)
   - Print on standard A4 or Letter-sized paper
   - Use regular printer settings (no special paper required)
   - Print enough copies for your entire class

2. **Attach to Exams**
   - Tape or staple the strip along the left or right margin of your exam
   - Ensure the strip is firmly attached and lies flat
   - The strip should not cover any exam questions

3. **Brief Students**
   - Show students where to find the question numbers on the strip
   - Demonstrate how to fill/shade the letter bubbles (A-E) completely
   - Remind students to keep the strip flat and avoid folding

### For Students

1. **Fill the Bubbles**
   - Find the row matching your question number (1-50)
   - **Completely fill/shade** the circle corresponding to your answer (A, B, C, D, or E)
   - Use a dark pen or pencil for best results
   - Fill bubbles neatly—avoid marks outside the circles

2. **Keep It Flat**
   - Do not fold the strip
   - Keep the strip area clean and free of stray marks
   - If you make a mistake, erase completely or cross out clearly and fill the correct bubble

3. **Submit**
   - Submit your exam with the strip attached
   - The strip will be scanned along with your exam

## Scanning with Iskorly

### Current Usage (Manual Mode)

1. **Capture the Image**
   - Use Iskorly's camera to photograph the exam with the strip attached
   - Ensure the entire strip is visible and in focus
   - Good lighting is essential for accurate OCR

2. **Crop to Include Strip**
   - When cropping, include the answer strip area in your selection
   - The OCR will read both the exam body and the strip

3. **Verify Parsed Answers**
   - Review the parsed answers in Iskorly's editable grid
   - The strip-based answers should appear more accurately than free-form handwriting
   - Make any necessary corrections before scoring

### Future: Strip Mode (Coming Soon)

A future update will add an optional "Strip Mode" that:
- Focuses OCR exclusively on the strip region
- Provides even faster and more reliable answer extraction
- Automatically aligns and deskews using the fiducial markers

## Tips for Best Results

### Do's ✓
- Fill bubbles completely and neatly
- Use a dark pen or pencil (black or dark blue recommended)
- Keep the strip flat and smooth
- Ensure good lighting when scanning
- Verify the strip is fully visible in the photo

### Don'ts ✗
- Don't fold or crease the strip
- Don't make stray marks near the bubbles
- Don't use light-colored pens (yellow, light green, etc.)
- Don't cover the fiducial markers (square boxes at top and bottom)
- Don't partially fill bubbles—fill them completely

## Technical Details

### Strip Specifications
- **Questions**: 1-50 (expandable in future versions)
- **Answer Options**: A, B, C, D, E per question
- **Fiducial Markers**: Two square markers at ends for deskewing and alignment
- **Format**: Vector PDF for crisp printing at any size
- **Paper**: Compatible with A4 and Letter sizes

### Fiducial Markers
The square markers at the top and bottom of the strip are **fiducial markers**. These help future versions of Iskorly automatically:
- Detect the strip location
- Correct for skew or rotation
- Align the strip for optimal OCR

**Do not write over or obscure these markers.**

## Troubleshooting

### Problem: OCR still misreads answers
- **Solution**: Ensure bubbles are filled completely and darkly
- Verify good lighting when scanning
- Check that the strip is not wrinkled or folded

### Problem: Strip doesn't fit on my exam
- **Solution**: The strip is designed for standard A4/Letter margins
- Try printing at 100% scale (not "fit to page")
- Alternatively, attach the strip to a separate page and scan both pages

### Problem: Running out of questions (need more than 50)
- **Solution**: Use multiple strips or number the second strip starting at 51
- Future versions may support 100+ questions on a single strip

## Support

For questions or feedback about the Smart Answer Strip:
- **Email**: teamiskorly@gmail.com
- **GitHub**: [Iskorly Repository Issues](https://github.com/traumereixd/Iskorly/issues)

---

**Version**: 1.0  
**Last Updated**: January 2026  
**Compatible with**: Iskorly v1.7+
