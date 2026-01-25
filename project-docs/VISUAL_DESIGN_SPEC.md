# Iskorly UX 2.0 - Visual Design Specification

## Color Palette

### Primary Colors
- **Primary Indigo**: `#4452A6` - Main brand color (buttons, splash, headers)
- **Primary Variant**: `#E3E7F9` - Light indigo (backgrounds, subtle accents)
- **On Primary**: `#FFFFFF` - Text on primary color

### Secondary Colors
- **Secondary Emerald**: `#21A179` - Accent color (icons, highlights)
- **Secondary Variant**: `#1A8461` - Darker emerald (hover states)
- **On Secondary**: `#FFFFFF` - Text on secondary color

### Surface & Background
- **Surface**: `#FFFFFF` - Card backgrounds
- **Surface Elevated**: `#F8F9FB` - Elevated components
- **Background**: `#FDFCFF` - App background
- **On Surface**: `#1C1B1F` - Text on surface

### Semantic Answer Colors
- **Answer Correct**: `#C8E6C9` - Light green background for correct answers
- **Answer Incorrect**: `#FFCDD2` - Light red background for wrong answers
- **Answer Blank**: `#FFF9C4` - Light yellow for unanswered
- **Answer Neutral**: `#E0E0E0` - Gray for neutral state

## Typography

### Font Family
**Inter** (Google Fonts - Downloadable)
- Clean, modern sans-serif
- Excellent legibility at all sizes
- Professional appearance

### Text Styles
- **App Title**: 36sp, Bold, White (on splash/menu)
- **Section Headers**: 28sp, Bold, On Surface
- **Card Labels**: 18sp, Bold, On Surface
- **Body Text**: 16sp, Regular, On Surface
- **Captions**: 14sp, Regular, On Surface Variant
- **Tagline**: 16sp, Regular, Primary Variant

## Layouts

### Splash Screen (SplashActivity)
```
┌─────────────────────────────────┐
│                                 │
│    [Indigo Background]          │
│                                 │
│         ┌───────┐               │
│         │   I   │  (Logo)       │
│         └───────┘               │
│                                 │
│        Iskorly                  │
│    Faster Scoring.              │
│    Smarter Teaching             │
│                                 │
│                                 │
└─────────────────────────────────┘
```

**Details**:
- Full-screen indigo background (#4452A6)
- Centered logo (120dp x 120dp)
- App name below logo (32sp, bold, white)
- Tagline below name (14sp, light indigo)
- 800ms duration before transitioning to menu

### Main Menu (MainMenuActivity)
```
┌─────────────────────────────────┐
│  [Gradient Background]           │
│       Indigo → Light Indigo      │
│                                  │
│         ┌───────┐                │
│         │   I   │  (Logo 96dp)   │
│         └───────┘                │
│                                  │
│        Iskorly (36sp)            │
│   Faster Scoring.                │
│   Smarter Teaching               │
│                                  │
│  ┌──────────────────────────┐   │
│  │  📷  Start Scan           │   │ ← Primary card (elevated)
│  │      Scan answer sheets   │   │
│  └──────────────────────────┘   │
│                                  │
│  ┌──────────────────────────┐   │
│  │  📋  History              │   │
│  └──────────────────────────┘   │
│                                  │
│  ┌──────────────────────────┐   │
│  │  📊  Masterlist           │   │
│  └──────────────────────────┘   │
│                                  │
│  ┌──────────────────────────┐   │
│  │  ✏️  Answer Key Setup     │   │
│  └──────────────────────────┘   │
│                                  │
│     [Tutorial Button]            │
│     [Credits Button]             │
│                                  │
└─────────────────────────────────┘
```

**Layout Details**:
- Vertical scrolling container (NestedScrollView)
- Gradient background (indigo tones)
- Logo + branding at top
- 4 primary action cards:
  - White surface background
  - 16dp padding, 12dp bottom margin
  - Icon (40dp) + label layout
  - Start Scan has 4dp elevation (primary action)
  - Others have 2dp elevation
  - 12dp rounded corners
- Secondary action buttons at bottom
  - Outlined style for Tutorial
  - Text style for Credits

### Answer Highlighting (After Scoring)
```
Question Grid with colored backgrounds:

┌──────────────────────────────┐
│ Q1: A  [GREEN - Correct]     │
│ Q2: B  [RED - Incorrect]     │
│ Q3: C  [GREEN - Correct]     │
│ Q4: -  [YELLOW - Blank]      │
│ Q5: D  [GREEN - Correct]     │
└──────────────────────────────┘
```

**Color Mapping**:
- Correct: Light green (#C8E6C9) bg, dark green text
- Incorrect: Light red (#FFCDD2) bg, dark red text
- Blank: Light yellow (#FFF9C4) bg, dark yellow text

## Component Specifications

### MaterialCardView (Navigation Cards)
- **Corner Radius**: 12dp
- **Elevation**: 4dp (primary), 2dp (secondary)
- **Background**: White (#FFFFFF)
- **Padding**: 16dp
- **Margin**: 12dp bottom

### MaterialButton
- **Primary**: Filled, indigo background
- **Secondary**: Outlined, indigo stroke
- **Text**: Text button, indigo text
- **Min Height**: 48dp (touch target)
- **Padding**: 16dp horizontal

### Icons
- **Size**: 32dp (card icons), 40dp (primary action icon)
- **Color**: Primary indigo or secondary emerald
- **Style**: Material system icons

## Spacing System

- **Micro**: 4dp (icon margins)
- **Small**: 8dp (text margins)
- **Medium**: 12dp (card margins)
- **Large**: 16dp (card padding, section spacing)
- **XLarge**: 24dp (screen padding)
- **XXLarge**: 32dp (top logo margin)

## Motion & Transitions

### Splash to Menu
- Duration: 800ms
- Type: Fade (no custom transition - standard activity transition)

### Menu to MainActivity
- Type: Standard activity transition
- Back navigation: Standard back stack

### Card Interactions
- Ripple effect on touch (Material default)
- Haptic feedback (KEYBOARD_TAP)

## Accessibility

### Color Contrast
- All text meets WCAG AA standards
- Primary indigo on white: 4.5:1+
- White on primary indigo: 4.5:1+

### Touch Targets
- All interactive elements: minimum 48dp height
- Cards: full width, ample padding

### Content Descriptions
- All icons have meaningful descriptions
- ImageViews include contentDescription attributes

## Dark Mode Support

Currently using `Theme.Material3.DayNight.NoActionBar` as parent, but colors optimized for light mode. Dark mode implementation can leverage:
- `values-night/colors.xml` for alternate palette
- Surface color variants already defined
- Future enhancement: Auto-switching based on system theme

## Responsive Design

### Small Screens (≤ 5.0")
- NestedScrollView ensures all content accessible
- Cards stack vertically
- No horizontal scrolling required

### Large Screens (≥ 6.0")
- Same vertical layout (consistent experience)
- More breathing room around cards
- Potential future: Two-column grid for tablets

## Implementation Notes

1. **Gradient Background**: Uses `bg_main_menu_gradient.xml` (135° linear gradient)
2. **Downloadable Font**: Inter loads asynchronously via Google Fonts provider
3. **Vector Assets**: All icons are vector drawables (resolution-independent)
4. **Material Components**: Leverages Material3 design system for consistency
5. **Theme Inheritance**: New theme extends Material3.DayNight for future flexibility

## Comparison: Before vs After

### Before (Beige Theme)
- Beige primary color (#BDAE8A)
- Simple button-only menu
- Old background image
- Basic layout

### After (UX 2.0)
- Professional indigo palette (#4452A6)
- Card-based navigation with icons
- Modern gradient background
- Splash screen branding
- Enhanced visual hierarchy

## Design Rationale

### Why Indigo + Emerald?
- **Professional**: Suitable for educational setting
- **Trustworthy**: Deep blue conveys reliability
- **Fresh**: Emerald accent adds vibrancy without overwhelming
- **Accessible**: Strong contrast ratios

### Why Card-Based Navigation?
- **Clarity**: Each action clearly delineated
- **Modern**: Follows current Material Design trends
- **Scannable**: Icons + labels easy to parse at a glance
- **Scalable**: Easy to add/remove actions in future

### Why Inter Font?
- **Legibility**: Designed for screen readability
- **Modern**: Clean, professional appearance
- **Free**: No licensing concerns
- **Web-Safe**: Also works in documentation/web views

---

**Visual Design**: Complete  
**Implementation**: Ready for device testing  
**Documentation**: Comprehensive specifications provided
