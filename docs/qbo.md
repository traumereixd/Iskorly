# QBO SHOWQASE 2025 Application - Iskorly

Copy-ready answers for the QBO SHOWQASE 2025 application form.

---

## Basic Information

**Startup Name:** Iskorly

**Industry:** Education

**Location:** Landayan, San Pedro, Laguna; Tunasan, Muntinlupa City

**Launch Date:** 2025 (working prototype/pilot phase)

---

## Startup Description (3-4 sentences)

Iskorly is an AI-powered mobile application that transforms any Android smartphone into an answer sheet scanner with built-in analytics. Using OCR technology (Google Vision API with OCR.Space fallback), teachers can capture answer sheets with their phone camera, instantly parse student responses, and generate comprehensive item-level statistics including MPS by section, common wrong answers, and difficulty heatmaps. With a teacher verification step before scoring, Iskorly maintains accuracy while dramatically reducing manual grading time. The app stores all data locally for privacy and exports to CSV for school record-keeping.

---

## Core Product Overview

**What does your product do?**

Iskorly enables teachers to scan, score, and analyze student answer sheets using their smartphone camera. The app captures answer sheet photos, uses dual OCR engines to extract student answers, presents them in an editable color-coded grid for teacher verification, calculates scores, and generates masterlist analytics showing per-item statistics, most common mistakes, and difficulty heatmaps. All processing happens with local data storage and CSV export capability.

**Key Features:**
- Camera2-based capture with automatic orientation correction
- Dual OCR: Google Vision primary, OCR.Space fallback for handwriting
- Domain-specific parser with Roman numeral support
- Teacher verification via editable two-column grid with color feedback
- Answer key slots for multiple quizzes with import/export
- Multi-image batch import with deterministic merge
- History tracking with rename and delete options
- Masterlist analytics: MPS by section, correct/incorrect rates, common wrong answers, heatmap visualization
- CSV and JSON export with anonymized data
- High-contrast and two-column display toggles
- Local-only storage via SharedPreferences (no remote database)

---

## Primary Problem You're Solving

**What problem does Iskorly address?**

Teachers in the Philippines face significant delays in providing student feedback due to manual scoring burdens, especially with class sizes of 40-50 students. Manual grading of answer sheets is time-consuming, error-prone, and provides no aggregated insights into which questions students struggle with most. Most schools cannot afford Optical Mark Reader (OMR) machines, leaving teachers to score everything by hand. This delays formative assessment, prevents data-driven instruction, and consumes hours that could be spent on teaching.

**Impact:**
- Manual scoring delays feedback by days or weeks, reducing learning effectiveness
- Large class sizes make individual attention and item analysis nearly impossible
- No OMR hardware access in under-resourced schools
- Teachers lack visibility into common student misconceptions and item difficulty
- Administrative burden reduces time available for lesson planning and student support

---

## Stage

**Current Stage:** Idea/MVP (working prototype, no pilots yet)

**Development Status:**
- Working Android app with full scanning-to-analytics pipeline
- Functional dual OCR system (Google Vision + OCR.Space)
- Editable answer grid with color-coded feedback
- Masterlist analytics with CSV export
- Multiple answer key slots with import/export
- Batch multi-image processing with deterministic merge
- Local storage with privacy-first design
- **No pilots started yet** - seeking initial school partners

---

## Business Model

**How will Iskorly make money?**

Iskorly is free for teachers and students. We have not raised funds and we are not sponsored yet. For now this is a student project applying to programs.

**Future Sustainability Strategy:**
1. **Free for End Users:** Teachers and students never pay. No subscriptions, paywalls, or in-app purchases.

2. **Future B2B Sponsorships (exploring):** Schools, school districts, and Local Government Units (LGUs) could sponsor Iskorly as part of their educational technology initiatives. Sponsorships would cover cloud API costs (Google Vision, OCR.Space) and infrastructure.

3. **Grants and Cloud Credits:** We will pursue education-focused grants (e.g., DepEd innovation grants, international education foundations) and cloud provider credits (Google Cloud Platform Education, AWS Educate) to subsidize operational costs.

4. **On-Device OCR Exploration:** Actively researching on-device OCR solutions (Google MLKit, Tesseract) to reduce cloud API dependency and minimize ongoing costs. This would improve offline functionality and reduce infrastructure needs.

**Cost Structure:**
- Primary costs: OCR API usage (billed per request)
- Secondary costs: Hosting for website/support materials
- Zero cost for users (free app, no server-side user accounts)

**Why This Model:**
- Aligns with educational equity goals (no financial barrier to teachers)
- Schools already budget for educational technology; Iskorly fits existing procurement
- LGUs in the Philippines increasingly fund digital education initiatives
- Grant funding available for high-impact, low-cost education solutions
- On-device OCR reduces future costs significantly

---

## Funding Status

**Funding Raised:** $0 (No funding yet)

**Current Funding Status:** Self-funded / bootstrapped student project

**Future Funding Needs:**
- Seed funding to scale API credits during pilot expansion
- Grants for on-device OCR research and development
- Sponsorships from schools/LGUs for operational costs
- Cloud provider credits for infrastructure

---

## Founders & Team

**Jayson G. Sahagun**
- Role: Developer
- Responsibilities: Camera2 pipeline implementation, OCR integration (Google Vision + OCR.Space), domain-specific parser development, analytics engine, SharedPreferences persistence, CSV/JSON export
- LinkedIn: [Add LinkedIn profile link]

**Myra Mia Medel**
- Role: Graphic Designer
- Responsibilities: Visual design, UI/UX, branding, color system, iconography, promotional materials
- LinkedIn: [Add LinkedIn profile link]

**Queen Rheyceljoy (Rheycel) Mahusay**
- Role: Operations/Manager
- Responsibilities: Teacher outreach, pilot coordination, stakeholder engagement, feedback collection, operational workflows, deployment planning
- LinkedIn: [Add LinkedIn profile link]

---

## Biggest Achievement

**Working Android Application with Full Analytics Pipeline**

Our biggest achievement is developing a fully functional Android app that successfully implements the complete scanning-to-analytics workflow. Iskorly currently features:

- **End-to-End Pipeline:** Camera2 capture → dual OCR (Google Vision + OCR.Space fallback) → domain parser → teacher verification → scoring → history tracking → masterlist analytics with CSV export
- **Technical Validation:** ≤2 percentage point accuracy gap target vs manual scoring with teacher verification step
- **MVP Complete:** Ready for pilot deployment with interested teachers
- **Privacy-First Design:** No student face capture, local-only storage, anonymized exports
- **Teacher-Friendly UX:** Color-coded feedback, editable grid, batch processing, answer key reuse

This demonstrates technical feasibility and scalability potential with minimal infrastructure. **No pilots have been conducted yet** - we are in the application and preparation stage.

---

## Other Milestones

- ✅ **Dual OCR Engine Implementation:** Google Vision API as primary OCR with OCR.Space as fallback for challenging handwriting, improving recognition robustness
- ✅ **Two-Column and High-Contrast Modes:** Display toggles for better accessibility and faster review
- ✅ **Deterministic Merge Algorithm:** Multi-image batch import with first-non-blank-answer-wins logic for handling multiple photos of same answer sheet
- ✅ **Anonymized CSV Export:** School-safe CSV format with scores and statistics only, no personally identifiable information
- ✅ **Masterlist Analytics:** Per-item statistics including MPS by section, correct/incorrect counts, most common wrong answers, and visual difficulty heatmap
- ✅ **Answer Key Slots:** Multiple answer key storage with import/export capability for quiz/test reuse
- ✅ **Local Privacy Design:** SharedPreferences-based storage with no remote database, ensuring data stays on teacher's device
- 🔄 **Teacher Onboarding Kit (In Progress):** User guides, tutorial videos, and best practices documentation for new users
- 🔄 **On-Device OCR Research (In Progress):** Exploring Google MLKit and Tesseract to reduce cloud API costs and improve offline functionality

---

## What We're Looking For (Indicators/Asks)

**Networking:**
- Connections with DepEd district supervisors and school principals in Laguna, Metro Manila, and other regions for potential pilot partnerships
- Introductions to educational technology advocates and teacher networks
- Partnerships with educational NGOs and foundations focused on Filipino classrooms

**Potential Customers (Schools/LGUs):**
- Public schools interested in piloting Iskorly (seeking initial 3-5 schools for first pilots)
- Private schools with large class sizes looking for assessment efficiency tools
- Local Government Units (LGUs) with education technology budgets
- DepEd regional offices evaluating tools for teacher professional development

**Potential Investors/Funders:**
- Angel investors or VCs interested in EdTech and social impact in the Philippines
- Grant programs focused on SDG 4 (Quality Education) and educational equity
- Corporate social responsibility programs from tech companies (Google, AWS, Microsoft)
- Impact investors prioritizing accessible, scalable education solutions

**Other Support:**
- Cloud provider credits (GCP, AWS) to subsidize OCR API costs during initial pilots
- Introductions to education research teams for formal efficacy studies when pilots begin
- Media exposure to increase teacher awareness and adoption
- Mentorship from experienced EdTech founders on scaling and sustainability

**Exposure Opportunities:**
- Pitch competitions and demo days for visibility
- DepEd innovation showcases and teacher conferences
- EdTech publications and podcast features
- Partnership announcements when first pilots launch

---

## SDG Alignment

**Primary SDG:** SDG 4 - Quality Education

**Target 4.c:** Substantially increase the supply of qualified teachers, including through international cooperation for teacher training in developing countries, especially least developed countries and small island developing States.

**How Iskorly Contributes:**
- **Increases Teacher Effectiveness:** By reducing grading time by ≥50%, teachers gain hours for lesson planning, student support, and professional development
- **Enables Data-Driven Instruction:** Item-level analytics reveal which concepts students struggle with, allowing targeted reteaching
- **Democratizes Assessment Technology:** Makes advanced OMR-like capabilities accessible to under-resourced schools without expensive hardware
- **Supports Formative Assessment:** Faster feedback cycles improve student learning outcomes and teacher responsiveness
- **Reduces Teacher Burnout:** Automating tedious tasks helps retain quality teachers in the profession

**Secondary SDG:** SDG 9 - Industry, Innovation, and Infrastructure

**How Iskorly Contributes:**
- **Leverages Existing Infrastructure:** Uses smartphones teachers already own, removing hardware barriers
- **Mobile-First Innovation:** OCR-based approach democratizes educational technology for resource-constrained environments
- **Promotes Digital Literacy:** Teachers gain experience with AI-powered tools and data analytics
- **Scalable Solution:** Minimal infrastructure requirements enable rapid, low-cost expansion to thousands of schools

---

## Contact Information

**Email:** jayson.sahagun@example.com (update with actual contact)

**Website:** https://traumereixd.github.io/Iskorly/

**GitHub Repository:** https://github.com/traumereixd/Iskorly

---

## Additional Notes for QBO Application

**Alignment with QBO Focus Areas:**
- **Education Vertical:** Core focus on improving teacher effectiveness and student outcomes
- **Technology Innovation:** AI/ML-powered OCR and analytics
- **Social Impact:** Addresses educational equity in under-resourced Philippine schools
- **Scalability:** Software-only solution with minimal infrastructure needs
- **Sustainability:** Clear B2B sponsorship model with grant/credit support pathway

**Key Differentiators:**
- Always-free for teachers (no freemium trap)
- Privacy-first design (local storage, no student PII)
- Teacher verification step maintains human oversight and accuracy
- Dual OCR for robustness
- Works with existing smartphones (no new hardware)
- Ready for pilot deployment

**Current Status:**
- Working MVP deployed on Android
- Applying to accelerator programs (QBO 2025)
- Seeking initial pilot school partners
- Technical targets: ≤2pp accuracy gap, ≥50% time savings

---

*Last Updated: October 2025*
