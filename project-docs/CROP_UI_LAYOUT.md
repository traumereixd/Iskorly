# SimpleCropActivity UI Layout Documentation

## UI Structure Overview

```
┌─────────────────────────────────────────────┐
│                                             │
│                                             │
│         CropImageView (with grid)           │
│              (Full screen)                  │
│                                             │
│                                             │
│                                             │
├─────────────────────────────────────────────┤
│  Rotation: 0°              Free  4:3  1:1   │  ← Info & Aspect Ratio Row
├─────────────────────────────────────────────┤
│    ↶    ↷    ↔         [spacer]    ✕   ✓   │  ← Action Buttons Row
│  Rotate Rotate Flip               Cancel    │
│   Left  Right Horiz                Confirm  │
└─────────────────────────────────────────────┘
```

## Bottom Control Panel Details

### First Row (Info & Aspect Ratio)
- **Left Side:**
  - Rotation angle text (e.g., "0°", "90°", "180°", "270°")
  - Text size: 14sp, bold
  - Color: ?attr/colorOnSurface (adapts to theme)

- **Right Side:**
  - Three aspect ratio buttons
  - Style: Outlined Material Button
  - Height: 40dp (compact)
  - Options: "Free", "4:3", "1:1"
  - Selected button has pressed state visual

### Second Row (Action Buttons)
All buttons are ImageButtons with:
- Size: 56dp × 56dp (exceeds 48dp accessibility minimum)
- Margin: 8dp around each button
- Background: crop_action_bg.xml (ripple effect on indigo)
- Icon color: White (#FFFFFF)

**Left Group (Transformations):**
1. **Rotate Left** (↶)
   - Icon: ic_crop_rotate_left.xml
   - Action: Rotate -90°
   - Content description: "Rotate 90 degrees left"

2. **Rotate Right** (↷)
   - Icon: ic_crop_rotate_right.xml
   - Action: Rotate +90°
   - Content description: "Rotate 90 degrees right"

3. **Flip Horizontal** (↔)
   - Icon: ic_crop_flip.xml
   - Action: Mirror horizontally
   - Content description: "Flip horizontal"

**Right Group (Confirmation):**
4. **Cancel** (✕)
   - Icon: ic_crop_cancel.xml
   - Action: Exit without saving
   - Content description: "Cancel crop"

5. **Confirm** (✓)
   - Icon: ic_crop_confirm.xml
   - Action: Save cropped image
   - Content description: "Confirm crop"

## Color Themes

### Light Theme
- Background: #FDFCFF (off-white)
- Surface: #FFFFFF (white)
- On Surface: #1C1B1F (dark text)
- Action buttons: #4452A6 (indigo) with white icons

### Dark Theme
- Background: #121212 (very dark gray)
- Surface: #1E1E1E (dark gray)
- On Surface: #FFFFFF (white text)
- Action buttons: #4452A6 (indigo) with white icons

## Responsive Behavior

### Portrait Mode
- Controls stack vertically in bottom panel
- Action buttons remain in single row (will wrap on very small screens)
- CropImageView takes remaining space above controls

### Landscape Mode
- Same layout structure (RelativeLayout with bottom-aligned controls)
- Controls may take less vertical space ratio
- Action buttons remain accessible

### Small Screens (≤ 5.0")
- Buttons maintain 56dp size for touch targets
- Text remains readable at 14sp
- Scrolling not needed (bottom-aligned container)

### Large Screens (≥ 6.0")
- Same layout with more breathing room
- Buttons don't scale up (fixed 56dp)
- More visible crop area

## Interaction Flow

1. **Load Image**
   - Image appears in CropImageView with grid guidelines
   - Default: Free aspect ratio
   - Rotation display shows: "0°"

2. **Apply Transformations**
   - Tap rotate buttons → Image rotates 90°, angle updates
   - Tap flip → Image mirrors horizontally
   - Tap aspect ratio → Crop box constrains to ratio
   - Each action provides haptic feedback (vibration)

3. **Crop**
   - User drags crop handles to select area
   - Guidelines help with alignment

4. **Confirm/Cancel**
   - Confirm → Process and save, return to camera
   - Cancel → Discard changes, return to camera

## Accessibility Features

- All buttons have content descriptions for screen readers
- Touch targets meet 48dp minimum (using 56dp)
- High contrast between icons and backgrounds
- Text size meets readability standards (14sp)
- Color contrast passes WCAG AA standards:
  - Light mode: Dark text on white surface
  - Dark mode: White text on dark surface

## Performance Notes

- Maximum output resolution: 2048px (prevents OOM)
- Automatic downsampling on load if needed
- Bitmap recycling after use
- OutOfMemoryError handling with fallback
- Efficient image processing in background thread
