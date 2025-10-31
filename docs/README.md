# Iskorly Website & QBO Application Materials

This folder contains the startup website and QBO SHOWQASE 2025 application materials for Iskorly.

## Contents

- **index.html** - Responsive landing page with sections for Hero, Problem, Solution, How It Works, Features, Progress, SDG Alignment, Business Model, Privacy, Team, and Contact
- **styles.css** - Modern, responsive styling with brand color (#5f432c) and accessibility features
- **script.js** - Smooth navigation, mobile menu, IntersectionObserver animations, and analytics event stubs
- **qbo.md** - Copy-ready content for QBO SHOWQASE 2025 application form
- **assets/** - Logo, badges, and app screenshot placeholders

## Publishing on GitHub Pages

### Enable GitHub Pages

1. Go to your repository on GitHub: https://github.com/traumereixd/Iskorly
2. Click **Settings** (top menu)
3. Scroll down to **Pages** (left sidebar)
4. Under **Source**, select:
   - Source: **Deploy from a branch**
   - Branch: **main** (or your default branch)
   - Folder: **/docs**
5. Click **Save**
6. Wait 1-2 minutes for deployment
7. Visit your site at: **https://traumereixd.github.io/Iskorly/**

### Updating the Site

After GitHub Pages is enabled, any changes you push to the `/docs` folder will automatically deploy within 1-2 minutes.

```bash
# Make your edits to files in docs/
# Then commit and push
git add docs/
git commit -m "Update website content"
git push origin main
```

## Editing Content

### Updating Copy

**Hero Section:**
- Edit the `<h1>` and `<p>` tags in the `.hero` section of `index.html`
- Update statistics in `.hero-stats`

**Problem/Solution Sections:**
- Edit text in `.problem-card` and `.solution-text` divs
- Add or remove cards as needed (maintain responsive grid)

**Features:**
- Update `.feature-card` items
- Keep 6-8 features for best layout

**Team:**
- Update `.team-member` sections with names and roles
- Replace avatar initials in `.member-avatar` divs
- Add LinkedIn links when available

**Contact Email:**
- Search for `mailto:` in `index.html`
- Replace `jayson.sahagun@example.com` with actual email

### Updating Styles

**Brand Colors:**
- Primary brand color is defined in `styles.css` as `--brand-primary: #5f432c`
- To change colors, edit CSS custom properties in `:root` section

**Responsive Breakpoints:**
- Desktop: default styles
- Tablet: `@media (max-width: 1024px)`
- Mobile: `@media (max-width: 768px)`
- Small mobile: `@media (max-width: 480px)`

### Adding New Sections

1. Copy an existing `<section>` block in `index.html`
2. Update the `id` attribute for navigation
3. Modify content and classes as needed
4. Add navigation link in `<nav>` if desired
5. Ensure `.fade-in` class is on elements you want animated

## File Structure

```
docs/
├── index.html          # Main landing page
├── styles.css          # Responsive stylesheet
├── script.js           # Interactive features & animations
├── qbo.md              # QBO application copy
├── README.md           # This file
└── assets/
    ├── placeholder-logo.svg      # Iskorly logo
    ├── sdg4-badge.svg            # SDG 4 badge
    ├── app-screen-scan.png       # Screenshot placeholder 1
    ├── app-screen-grid.png       # Screenshot placeholder 2
    └── app-screen-heatmap.png    # Screenshot placeholder 3
```

## Testing Locally

To preview the site locally before pushing:

```bash
# Option 1: Python simple server
cd docs/
python3 -m http.server 8000
# Visit http://localhost:8000

# Option 2: PHP built-in server
cd docs/
php -S localhost:8000
# Visit http://localhost:8000

# Option 3: VS Code Live Server extension
# Right-click index.html → "Open with Live Server"
```

## Accessibility

The site includes:
- Semantic HTML5 elements
- ARIA labels on interactive elements
- Keyboard navigation support
- `prefers-reduced-motion` support
- Adequate color contrast (WCAG AA)
- Alt text for images

## Browser Support

Tested and supported on:
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Mobile browsers (iOS Safari, Chrome Android)

## QBO Application

The `qbo.md` file contains pre-written answers for the QBO SHOWQASE 2025 application. Copy and paste sections as needed into the online form. Key sections include:

- Startup Description
- Product Overview
- Problem Statement
- Business Model
- Stage & Milestones
- Team Information
- Funding Status
- SDG Alignment
- What We're Looking For

Update placeholders (e.g., LinkedIn URLs, email addresses, metrics) before submitting.

## Replacing Placeholder Images

The current app screenshots are SVG placeholders. To replace with real screenshots:

1. Take screenshots from the Android app
2. Resize to ~300-600px width for web optimization
3. Save as PNG or JPEG
4. Replace files in `assets/` folder:
   - `app-screen-scan.png`
   - `app-screen-grid.png`
   - `app-screen-heatmap.png`
5. Commit and push changes

## SEO & Social Sharing

Update meta tags in `<head>` of `index.html`:
- `og:url` - Your actual GitHub Pages URL
- `og:image` - Path to a high-quality preview image (1200x630px recommended)
- `twitter:image` - Same as og:image

## Troubleshooting

**Site not loading after enabling GitHub Pages:**
- Wait 2-5 minutes for initial deployment
- Check Settings → Pages for deployment status
- Ensure `/docs` folder is on the correct branch

**Changes not appearing:**
- Clear browser cache (Ctrl+Shift+R or Cmd+Shift+R)
- Check GitHub Actions tab for deployment status
- Verify files were committed and pushed

**Mobile menu not working:**
- Check browser console for JavaScript errors
- Ensure `script.js` is loading (check Network tab)

**Styles not applying:**
- Verify `styles.css` path is correct in `index.html`
- Check for CSS syntax errors
- Clear browser cache

## Support

For questions or issues with the website:
- Open an issue on GitHub: https://github.com/traumereixd/Iskorly/issues
- Contact: jayson.sahagun@example.com (update with actual contact)

## License

Website content and assets for Iskorly © 2025. Educational use - DepEd Philippines.

---

*Last Updated: October 2025*
