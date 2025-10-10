# Visual Changes Summary - Photo-Based UI Polish

## Main Menu Screen
**Before**: 
- Background: Gradient (indigo shades)
- Icon colors: Primary indigo (#4452A6) and emerald green

**After**:
- Background: Collage image (bg_menu)
- Icon colors: Brand brown (#5f432c) for ALL icons
- Navigation: Tutorial and Credits now open in MainActivity with dialogs

## Main Screen (Input Card)
**Before**:
- Background: main_bg.jpg
- Card: White with no special background
- Buttons: Indigo color

**After**:
- Background: bg_main (collage image)
- Card: Cream panel image (panel_bg) as background layer
- Buttons: Brand brown (#5f432c) color

## Splash Screen
**Before**:
- Background: Solid indigo color

**After**:
- Background: Collage image (bg_splash)

## By Section Summary (Masterlist)
**Before**:
```
Overall Score: 850 | Mean: 42.5 (85.0%) | Std Dev: 5.23 | MPS: 37.5 (75.0%) (n=20)
```

**After**:
```
Overall Score: 850 | Mean: 42.5 (85.0%) | Std Dev: 5.23 | MPS: 37.5 (75.0%) (n=20)
Exam(s): Math Midterm, Science Quiz, English Test
```

## Color Palette Changes
- Primary action color changed from Indigo (#4452A6) to Brand Brown (#5f432c)
- Affects all buttons in activity_main.xml
- Affects all icons in activity_main_menu.xml

## Navigation Flow Changes
**Main Menu → Tutorial**:
- Before: Shows dialog in MainMenuActivity
- After: Launches MainActivity → shows dialog there

**Main Menu → Credits**:
- Before: Shows dialog in MainMenuActivity  
- After: Launches MainActivity → shows dialog there

**Main Menu → Start Scan**:
- No change: Launches MainActivity normally
