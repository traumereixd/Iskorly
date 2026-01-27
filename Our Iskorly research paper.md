DEVELOPMENT AND EVALUATION OF AN ANDROID-BASED AUTOMATED ANSWER
 SHEET SCORING APP USING OPTICAL CHARACTER
 RECOGNITION (OCR) FOR ASSESSING
 STUDENTS’ RECORDS
A.Y.  2025-2026






Españo, Elijah Ria D., Lolos, Kneel Charles B., Mahusay, Queen Rheyceljoy F., Medel, Myra Jesalva, Reyes, John Jharen R., Sahagun, Jayson G., Tagle, Steve Aldrei D.


Science, Technology, Engineering, and Mathematics Strand,
Senior High School Department




LYCEUM OF ALABANG, INC




Mr. Leonardo D. Alfaro. LPT




September, 2025


CHAPTER I


THE PROBLEM AND ITS BACKGROUND


Project Context
The educational system of the Philippines is experiencing one of the most current and complex issues in assessment administration: the load of manual scoring using answer sheets. Educators in cities and in rural schools all over the country still use the old techniques of marking and registering student answers, a process which takes up colossal portions of classroom hours, adds in the element of human error and postpones the delivery of feedback to students which is crucial to their learning. The manual scoring system usually includes the peer-checking system (so-called exchange papers) or a teacher-implemented audit, which is both time-consuming and increasingly out of step with the modern demands of education, which require fast and data-driven decision-making.
Situation in the Philippine case where the K-12 curriculum by the Department of Education is competency based in assessment and presupposes regular measurement of learning in a variety of competencies. Teachers are faced at the same time with high populations of students in their classes which may be over 50 students per section in state schools, multiple subject preparation and massive documentation demands. This workload pressure has a direct effect on teacher well-being, professional satisfaction and eventually, instruction quality. Philippine Statistics Authority indicates that teachers at the public schools claim to spend an average of 15-20 hours per week on grading and non-teaching administrative activities on top of classroom learning, which is one of the biggest contributors of professional burnout and turnover in the teaching profession (PSA, 2023).
This paper responds to this challenge and in keeping with the international trend of adopting technologies to improve educational processes, the study proposes a technological solution to a common problem; Iskorly, which is a mobile application using Android OS and Optical Character Recognition (OCR) technology to computerize the process of scoring an objective-type answer sheets. Instead of needing costly specialized equipment (OMR scanners, dedicated computers) or institutional support (learning management systems, subscriptions to cloud storage), Iskorly can run on Android devices, which does not introduce obstacles to implementation, and respects privacy as it can process data on-device. The app is tailored specifically to the Philippine school setting: it supports fluctuating lighting situations in a classroom environment with erratic electricity supply, supports generic printed answer sheets that do not need special forms or technology, and offers analysis tools in a format that can be used by classroom leaders to take action.
Iskorly hopes to make a contribution to the augmented efficiency of assessment, further enhanced feedback and, lastly, learning outcomes of the Philippine students by mechanizing the mechanical part of scoring and still maintaining teacher judgment in instructional planning. The creation and testing of this application also shows how the right technology, in this case, technology that is not foisted on it but is developed with the local context in mind, can help tackle the intractable problems in education in resource-starved institutions.


Background of the Study
Assessment has long served as the compass guiding educational practice, yet in Philippine classrooms, the mechanics of conducting assessment remain largely manual and inefficient. For decades, teachers have employed consistent but labor-intensive methods: students exchange papers with peers, mark each other's responses according to an orally dictated answer key, and teachers subsequently verify, audit, and record all scores. This time-honored practice reflects cultural values of peer participation and collective responsibility, but it also creates substantial inefficiencies and opportunities for error.
The challenge of manual assessment scoring is not unique to the Philippines; however, the scale and acuity of the problem in the Philippine context warrant urgent attention. The Department of Education's K to 12 curriculum framework, implemented nationwide since 2016, has intensified assessment demands through competency-based learning and mastery-oriented evaluation systems. Teachers are now expected to administer frequent formative assessments, monitor individual student progress across multiple competencies, and generate evidence-based reports documenting learning gains. Simultaneously, the majority of Philippine teachers teach in environments with significant resource constraints: limited access to technology, crowded classrooms, inconsistent electricity supply, and inadequate administrative support. The result is a chronic mismatch between assessment demands and available resources, forcing teachers to absorb assessment burdens through extended working hours rather than through technological solutions.
Study by Langove and Khan (2024) examined the effect of automated grading systems on teacher workload and student performance in an exploratory mixed-methods study. The study employed quantitative data collection through classroom observation before and after the integration of automated grading systems, documenting that manual grading consumed an average of 15 hours per week per teacher. Upon implementation of automated grading technology, this burden was reduced to 9.75 hours per week, a 35% reduction. Currently, student engagement metrics improved measurably: assignment submission rates increased from 70% to 88%, and class participation increased from 65% to 81%. These findings underscore both the magnitude of the grading burden and its direct relationship to overall classroom dynamics and student outcomes.
Recent study by Carson and Templin (2019) demonstrated that workload stress, including grading burden, significantly predicts teacher burnout, which in turn is linked to reduced instructional effectiveness, absenteeism, and career attrition. For Philippine teachers, many of whom already work in high-stress environments with multiple job responsibilities outside formal teaching, that additional grading burden represents a critical vulnerability.
The opportunity cost of manual grading extends beyond teacher well-being to student learning outcomes. Educational research consistently demonstrates that timely, specific feedback is among the most powerful influences on student achievement. Yet in traditional manual scoring systems, delays between assessment administration and feedback delivery are inevitable. Students may not receive quiz results until days or even weeks later, by which time momentum for learning has dissipated, misconceptions have hardened, and the opportunity for immediate remediation has passed. For struggling learners, particularly those who benefit most from rapid intervention, this delay is pedagogically costly.
The advent of digital technology, particularly the ubiquity of smartphones and advances in artificial intelligence, creates unprecedented opportunities for addressing these longstanding challenges. Optical Character Recognition (OCR) technology, which converts images of text (printed or handwritten) into machine-readable digital text, has evolved from expensive specialized equipment to accessible cloud-based services available to any developer. A study by Thammarak et al. (2022) evaluated the accuracy of Google Cloud Vision OCR for digitizing official documents (Thai vehicle registration certificates) and achieved an average accuracy of 93.28% through a combination of image preprocessing and domain-specific post-processing. This high accuracy, combined with low cost and accessibility, makes OCR feasible for educational applications.
Building on these technological advances, mobile-first solutions have emerged as particularly promising for educational contexts in developing regions. A comparative study by Saavedra and Uribe (2022) demonstrated that cloud-based OCR technology could be integrated with low-cost hardware (Raspberry Pi microcontroller) to create affordable document digitization systems. Their research on license plate recognition achieved 100% accuracy while remaining economically viable, demonstrating that OCR could be deployed at scale in resource-constrained settings. Extending this logic to mobile devices, smartphones that are already present in the Philippines, represent ideal platforms for deploying OCR-based assessment tools without requiring institutional investment in specialized hardware.
The potential of automated assessment systems in educational technology is increasingly documented. Research by IEEE (2022) examined the application of OCR with machine learning for automating document processing and demonstrated that automated text recognition and subsequent grading could significantly reduce human workload while improving consistency. In the educational context specifically, such automation can reduce checking time, minimize transcription errors, and enable data-driven instructional decisions. A systematic review by Rodriguez et al. (2021) synthesizing evidence from 47 studies on automated grading systems concluded that technology-assisted assessment, when properly designed, maintains or exceeds accuracy of manual grading while dramatically reducing time investment and enabling more frequent assessment.
The context presents unique opportunities for mobile-based assessment innovation. The Philippines has achieved mobile penetration exceeding 80% of the population, with Android dominating the smartphone market due to lower device costs and compatibility with local infrastructure (Statista, 2024). This widespread Android adoption, combined with growing teacher familiarity with smartphones, suggests favorable conditions for deploying mobile assessment applications. Yet the Filipino context also presents specific constraints variables like internet connectivity, inconsistent electrical supply, limited institutional IT support, and privacy concerns in educational data handling, that generic assessment technology solutions may not address.
Recognizing both the urgent need for assessment efficiency innovations and the specific characteristics of the Philippine educational environment, this study introduces Iskorly, an Android-based application designed to automate objective-type answer sheet scoring through OCR technology. Iskorly is designed with explicit attention to Philippine contextual factors: it operates on personal smartphones requiring no institutional infrastructure, processes data locally to protect student privacy, accommodates variable image quality through multiple OCR engines and image preprocessing options, and provides analytics formatted for classroom teachers' immediate instructional needs. Rather than importing wholesale a generic education technology solution, Iskorly represents an effort to create appropriate technology, a technology thoughtfully designed for the specific constraints, values, and opportunities of Philippine educational practice.
The development of Iskorly builds on emerging research demonstrating the viability of OCR-based assessment, the critical need for workload reduction among Philippine teachers, and the potential of mobile platforms for educational innovation in resource-constrained contexts. By combining these evidence streams with grounded understanding of Philippine classroom realities, this research aspires to contribute both a practical tool for teachers and a model of contextually responsive educational technology development applicable to other developing regions facing similar assessment challenges.


Objectives of the Study
The overall objective of this study is to develop and evaluate Iskorly (Version 1.8.1), an Android‑based mobile application that utilizes Optical Character Recognition to automate scoring of objective‑type answer sheets, thereby reducing teacher workload and enhancing assessment efficiency in Philippine classroom contexts.
Specifically, this study aims to:
1. Assess teacher and student satisfaction with Iskorly’s scoring workflow, interface usability, and overall user experience through Likert‑scale surveys, gathering actionable feedback on ease of use, visual clarity, and perceived utility.
2. Evaluate the perceived usefulness of Iskorly’s analytics features, particularly Mean Percentage Score (MPS) by section, per‑item statistics, and common wrong answer identification for informing remedial instruction and curriculum adjustments.


Research Questions
This study aims to investigate the user acceptance and practical utility of Iskorly as a tool for automated answer sheet scoring. Specifically, it seeks to answer:
1. What is the perceived quality of Iskorly’s User Interface (UI) as measured by a 10‑item Likert instrument, reported using mean (x̅), standard deviation (σ), and verbal interpretation (VI)?
2. What is the perceived quality of Iskorly’s User Experience (UX) as measured by a 10‑item Likert instrument, reported using mean (x̅), standard deviation (σ), and verbal interpretation (VI)?


Hypothesis
Hypothesis 1 (UI):
H₀: The overall mean UI rating is below the “Agree” threshold (x̅ < 4.00).
Ha: The overall mean UI rating meets or exceeds the “Agree” threshold (x̅ ≥ 4.00).
Hypothesis 2 (UX):
H₀: The overall mean UX rating is below the “Agree” threshold (x̅ < 4.00).
Ha: The overall mean UX rating meets or exceeds the “Agree” threshold (x̅ ≥ 4.00).


Theoretical Framework
This study employs Basic Information Processing Theory as applied to Optical Character Recognition systems. The framework conceptualizes OCR as a multi‑stage process: input acquisition, recognition, post‑processing, and output generation. At the input stage, image quality determined by lighting, camera focus, paper orientation, and resolution directly affects recognition accuracy. Poor lighting, glare, shadows, or skewed angles introduce noise complicating text extraction.


Basic Information Processing Theory for OCR
The second theoretical pillar supporting this study is Basic Information Processing Theory as applied to Optical Character Recognition systems. This framework conceptualizes OCR as a multi-stage process encompassing input acquisition, recognition, post-processing, and output generation. At the input stage, image quality determined by lighting conditions, camera dvv vc focus, paper orientation, and resolution directly affects subsequent recognition accuracy. Poor lighting, glare, shadows, or skewed angles introduce noise that complicates text extraction. The recognition stage involves the OCR engine itself, which applies pattern matching, feature extraction, and machine learning algorithms to identify characters within the image. Google Cloud Vision API, employed by Iskorly, utilizes deep neural networks trained on extensive datasets to achieve high accuracy even with handwritten text, though performance varies with writing legibility and consistency.
Post-processing represents a critical yet often overlooked stage where raw OCR output is refined, structured, and validated. Iskorly implements answer parsing algorithms that identify answer-first line patterns (e.g., "1. B" or "5) D"), handle Roman numerals (I, II, III, IV, V), filter extraneous text, and align extracted responses to the teacher-defined answer key order. This deterministic logic reduces false positives and ensures that only relevant answer content is scored. The output stage generates structured score reports, timestamps entries, and populates analytics dashboards, transforming raw text recognition into actionable assessment data.
Information Processing Theory also emphasizes the role of feedback loops in system optimization. User interactions, such as manually correcting misread answers, adjusting OCR settings, or cropping images provide implicit training data that inform iterative improvements. Although Iskorly does not currently implement adaptive learning, the theoretical framework suggests future enhancements where user corrections could refine parsing rules or flag problematic handwriting styles for special handling.
Integration of Theories
Together, these theoretical frameworks provide a comprehensive lens for understanding Iskorly's development, evaluation, and adoption. Information Processing Theory guides the technical design, ensuring robust image capture, accurate OCR, and intelligent post-processing. Diffusion of Innovations Theory contextualizes the socio-technical dimensions, explaining how attributes like time savings (relative advantage), smartphone compatibility (compatibility), intuitive design (simplicity), pilot testing opportunities (trialability), and visible analytics (observability) collectively influence teacher acceptance and sustained usage. By aligning technical capabilities with adoption-friendly attributes, Iskorly is positioned not merely as a functional tool but as an innovation with realistic potential for widespread diffusion across the educational landscape.


Conceptual Framework
Figure 1. 
Conceptual Framework of Iskorly: Android-Based Automated Answer Sheet Scoring Using OCR. The diagram illustrates the flow from hardware, software, materials, and knowledge inputs through sequential processing stages to tangible outputs and beneficial outcomes. A feedback loop enables continuous improvement through user corrections, settings optimization, analytics insights, and system enhancements.
Isa nalang
  

The figure presents a conceptual framework for "Iskorly: Android-Based Automated Answer Sheet Scoring Using Optical Character Recognition (OCR)." It begins with the inputs necessary for the application's operation, progresses through sequential processing stages, and concludes with tangible outputs and beneficial outcomes. The framework also incorporates a feedback loop demonstrating how system performance and user experience inform continuous improvement and iterative refinement.
The input section emphasizes both hardware and software requirements essential for Iskorly's functionality. Hardware includes an Android smartphone (version 7.0 or higher) equipped with a functional camera, adequate storage capacity, and internet connectivity for OCR processing. Software requirements encompass the Iskorly application itself, Google Cloud Vision API credentials for primary OCR processing, and optionally an OCR.Space API key for fallback processing. Knowledge requirements include basic smartphone operation skills, understanding of answer key creation and management, and awareness of optimal image capture conditions (adequate lighting, stable camera positioning, clear visibility of the answer area). Assessment materials comprise printed or clearly handwritten answer sheets containing objective-type responses, teacher-prepared answer keys defining correct responses, and section/exam identifiers for organizational purposes.
The process section highlights the sequential activities involved in automated answer sheet scoring. Beginning with image capture using the Camera2 API, the system applies EXIF orientation correction to ensure proper image alignment. Users may optionally crop captured images to isolate the answer area, eliminating extraneous background. The OCR processing stage employs Google Cloud Vision API as the primary engine, with OCR.Space API serving as an automatic fallback if the primary service becomes unavailable. Users can enable two-column mode for multi-column answer layouts and high-contrast enhancement for low-quality images. Answer parsing follows, utilizing deterministic algorithms to extract item numbers and responses from raw OCR output, handle Roman numeral conversions, and filter text to match the answer key order. The scoring engine compares parsed responses to the answer key, calculating correct counts and percentage scores. Users review parsed answers on-screen with visual indicators (correct/incorrect), with the option to manually correct any OCR misreads before finalizing scores. Finally, the system stores results in a local SQLite database and generates analytics, computing Mean Percentage Score (MPS) by section, per-item difficulty statistics, and identification of common wrong answers.
The output section outlines the expected benefits and tangible products. Immediate outputs include accurate, timestamped scores; searchable history records accessible within the app; exportable CSV files containing structured assessment data; and analytics dashboards displaying MPS, item statistics, and visual heatmaps. Pedagogical outcomes encompass reduced teacher grading time enabling greater focus on instructional planning and individual student support, faster feedback delivery facilitating timely review and remediation, enhanced accuracy through elimination of human counting and transcription errors, and data-informed curricular decisions based on item difficulty and misconception patterns revealed by analytics.
The feedback loop at the bottom of the framework emphasizes the cyclical nature of system improvement. When teachers manually correct OCR misreads before finalizing scores, these corrections implicitly signal parsing weaknesses and inform algorithm refinement. User experimentation with OCR settings (two-column mode, high-contrast enhancement, image cropping) provides usage data indicating which features improve accuracy under specific conditions, guiding user training recommendations and interface enhancements. Analytics insights revealing consistently problematic quiz items may prompt teachers to revise future assessments or adjust instructional emphasis, closing the loop between assessment and instruction. Developer monitoring of error logs, API performance metrics, and user feedback informs version updates, bug fixes, and feature additions, progressively enhancing Iskorly's robustness and usability. The diagram makes evident that the processes of capturing, recognizing, scoring, storing, and analyzing assessments are cyclical and mutually reinforcing, with each iteration yielding improvements in accuracy, efficiency, and pedagogical impact.


Scope and Delimitation
This research is focused on the development and evaluation of Iskorly, an Android-based mobile application that utilizes Optical Character Recognition (OCR) technology to automate the scoring of objective-type answer sheets, serving as a practical solution to reduce teacher workload and enhance assessment efficiency in Philippine classroom contexts. The research aims to evaluate the efficacy of OCR-based automated scoring in comparison to traditional manual teacher scoring methods. The research includes developing a functional Android application with OCR integration, answer key management, scoring algorithms, and analytics generation. The resulting application will undergo testing for key performance metrics, including scoring accuracy (percentage agreement with manual scoring), time efficiency (time required per 10 to 100 sheets), user satisfaction with interface and workflow, and perceived usefulness of analytics features. These performance indicators will be compared to baseline measurements of traditional manual checking methods. 
However, the study has specific limitations and deliberate delimitations. It will not examine the effectiveness of individual OCR engines or machine learning algorithms in isolation, as these technical components have been extensively studied in computer science literature; rather, the focus is on the integrated system's performance in educational contexts. 
Furthermore, the study will not implement novel OCR algorithms or develop proprietary recognition technology, instead utilizing existing, publicly available OCR services (Google Cloud Vision API and OCR.Space API). The study is deliberately limited to objective-type assessments (multiple-choice, short-answer with single correct responses) and explicitly excludes subjective assessments such as essay grading, creative writing evaluation, or performance-based tasks requiring rubrics. The study does not address recognition of specialized content including mathematical notation, chemical formulas, scientific diagrams, or geometric figures; Iskorly is designed for standard alphanumeric text responses only. The platform is delimited to Android devices exclusively; iOS (Apple iPhone/iPad) versions are not addressed in this study.
Additional limitations constrain the research scope and should be considered when interpreting findings. OCR accuracy depends heavily on image quality, which is influenced by factors beyond the application's control: lighting conditions (natural daylight, fluorescent lamps, shadows, glare), camera hardware specifications (megapixel count, lens quality, autofocus capability), paper condition (wrinkles, stains, faded printing), and handwriting legibility. Iskorly cannot fully overcome extremely poor-quality inputs; severely illegible handwriting, extreme shadows, or heavily degraded paper may result in recognition errors requiring manual correction. The application's core OCR functionality requires internet connectivity to communicate with cloud-based services; in areas with unreliable or absent internet access, real-time scoring capability is compromised, though the app can queue requests for processing when connectivity is restored. The quality of image capture depends partly on user skill in positioning the camera, framing the answer area clearly, and maintaining stable focus, variables difficult to standardize across diverse users and classroom environments. Student handwriting exhibits wide variability in style, legibility, and consistency, introducing a source of OCR error difficult to eliminate without human review. The study's participant sample is delimited by practical feasibility constraints and is not statistically representative of all  teachers or schools in the Philippines; findings therefore cannot be generalized broadly across diverse institutional contexts, teacher experience levels, or geographic regions. As a developmental research project conducted by Grade 12 STEM students within a single academic year, the study is constrained by time and resources; advanced features such as on-device OCR processing, adaptive machine learning algorithms, or integration with learning management systems are deferred to future development phases. The evaluation period is limited to pilot testing and initial deployment rather than longitudinal impact assessment; long-term effects on teaching practice, student achievement, or institutional adoption patterns remain unknown and cannot be addressed within the current study's timeframe. Privacy considerations limit the study's scope; while Iskorly processes data locally to protect student privacy, temporary transmission of images to external OCR services may create privacy concerns for some institutions or parents, potentially affecting adoption rates in particularly privacy-conscious settings.


Significance of the Study


Teachers. The application will benefit teachers by offering a practical, time-efficient alternative to traditional manual checking methods. Iskorly provides a safe, reliable means for scoring assessments quickly, particularly in situations where teachers manage large class sizes and multiple subject preparations. The reduced grading time and improved accuracy of automated scoring will enhance efficiency in teachers' assessment workflows, freeing time for lesson planning, curricular development, and individualized student support. Faster feedback delivery enables teachers to identify learning gaps rapidly and implement timely interventions, thereby improving instructional responsiveness. Access to item-level analytics (MPS by section, per-item difficulty statistics, common wrong answers) empowers teachers to make data-informed curricular decisions and differentiate instruction based on evidence rather than intuition. Reduced administrative burden may also contribute to improved teacher well-being and professional satisfaction, potentially reducing burnout and increasing career longevity in the teaching profession.


Students. The application will benefit students through accelerated feedback cycles enabling timely review of assessment results while content remains cognitively accessible. Automated scoring reduces concerns about subjective bias or errors in peer checking, fostering greater trust in assessment fairness and objectivity. Students gain access to clear, transparent score reporting and (when shared by teachers) item-level analytics that help them identify strengths and areas requiring additional study, enabling more targeted, efficient learning strategies. More frequent formative assessments made feasible by reduced teacher grading burden create additional practice opportunities and lower-stakes assessment environments that reduce test anxiety while promoting mastery learning. Ultimately, data-informed instruction resulting from Iskorly's analytics supports more responsive, needs-based teaching that enhances learning outcomes.


School Administrators. The application will benefit school leaders and administrators by providing consolidated assessment data enabling evidence-based institutional decision-making. CSV export functionality and analytics dashboards allow administrators to aggregate performance data across sections, grade levels, or subject areas, identifying school-wide trends, achievement gaps, and areas requiring curricular intervention. By reducing individual teacher grading burden, the application indirectly creates time for teachers to participate in professional development, collaborative planning sessions, and instructional coaching investments that yield long-term improvements in teaching quality and student outcomes. The low-cost, scalable model of Iskorly makes technology-enhanced assessment accessible even to under-resourced public schools, promoting equity in access to educational technology. Demonstrated adoption of mobile technology for assessment signals institutional commitment to modernization and responsiveness to teacher needs, potentially enhancing school reputation and competitiveness in teacher recruitment.


Educational Researchers and Developers. This study will be valuable for future researchers and developers, as it provides a crucial foundation for further studies related to educational technology, mobile-assisted assessment, and OCR applications in pedagogical contexts. The empirical evidence regarding OCR accuracy, reliability, and usability in Philippine classroom conditions contributes to knowledge about appropriate technology design for developing contexts. The documented technical architecture, design decisions, implementation challenges, and user feedback serve as a blueprint for other developers seeking to create similar mobile assessment tools, providing actionable guidance on APIs, algorithms, and user interface design principles. Identified areas for future innovation, such as on-device OCR processing, adaptive learning algorithms, LMS integration, or expansion to support specialized content that highlights opportunities for successive generations of development. The study contributes nuanced insights into what constitutes "appropriate technology" for resource-constrained educational contexts, informing policy discussions about technology integration and infrastructure investments.


Philippine Education System. This study contributes to the broader Philippine education system by demonstrating a feasible, scalable model for integrating mobile technology into classroom assessment practices in ways that respect local constraints and values. Empirical evidence regarding adoption factors, usability challenges, and impact on teacher workload can inform Department of Education policies related to technology integration, teacher training programs, and infrastructure investments, ensuring that policy decisions are grounded in contextual realities rather than abstract aspirations. By addressing a perennial challenge through an accessible, locally appropriate innovation, Iskorly contributes to ongoing efforts to enhance educational quality and equity, aligning with national development goals and international commitments such as the United Nations' Sustainable Development Goal 4 (Quality Education). The research demonstrates the capacity of Filipino students and educators to create meaningful technological solutions addressing real-world educational problems, potentially inspiring further student-led innovation and supporting advocacy for STEM education investments.


Broader Global Context. The study contributes to the international discourse on educational technology in developing regions by demonstrating how mobile-first, cloud-assisted solutions can address persistent educational challenges without requiring new institutional infrastructure. The approach of building on existing mobile technology rather than requiring specialized hardware or robust broadband takes a pragmatic stance that acknowledges and works within resource constraints rather than ignoring them, offering a model applicable to other developing contexts facing similar assessment challenges. The research validates the potential of student-led innovation in addressing systemic problems and demonstrates interdisciplinary collaboration bridging computer science, education, and cognitive psychology, potentially inspiring further collaborative research projects globally.
















































Definition of Terms


Android Operating System - A mobile operating system developed by Google, based on the Linux kernel, designed primarily for touchscreen devices such as smartphones and tablets. Iskorly requires Android version 7.0 (Nougat) or higher.


Answer Key - A teacher-defined set of correct responses for an assessment, organized sequentially by item number and stored within Iskorly using a slot-based system. Each answer key includes metadata such as section name, exam title, total items, and creation date, and may be exported or imported in JSON format for backup and sharing purposes.


Answer Key Slot - An individual position within an answer key structure corresponding to a specific item number (1, 2, 3, ..., N), containing the correct answer for that item. Slots ensure ordered alignment between student responses and correct answers during the scoring process.


API (Application Programming Interface) - A set of protocols and tools that allow different software applications to communicate with each other. Iskorly uses APIs to send captured answer sheet images to cloud-based OCR services (Google Cloud Vision API and OCR.Space API) and receive text recognition results.


Base64 Encoding - A method of converting binary data (such as image files) into ASCII text format, enabling transmission over internet protocols that handle text. Iskorly encodes captured JPEG images as Base64 strings before sending them to OCR APIs.


Camera2 API - An Android framework introduced in API level 21 (Lollipop) that provides advanced camera control, including manual focus, exposure, and image format selection. Iskorly utilizes Camera2 for capturing high-quality images of answer sheets.


Cognitive Load Theory - A psychological framework proposing that learning and task performance are influenced by the amount of mental effort required. In this study, automation of grading tasks is theorized to reduce teachers' extraneous cognitive load, freeing mental resources for instructional design and student support.


CSV (Comma-Separated Values) - A plain-text file format for storing tabular data, where each line represents a row and columns are separated by commas. Iskorly exports assessment history as CSV files (format: Student Name, Section, Exam, Score, Total Items, Percentage, Timestamp) compatible with spreadsheet applications such as Microsoft Excel or Google Sheets.


Deterministic Merge - An algorithm used when multiple answer sheet images are imported for a single student (e.g., front and back pages). The merge applies a "first non-blank wins" rule: if item N is blank in image 1 but answered in image 2, the answer from image 2 is used; if both contain answers, image 1 takes precedence. This ensures predictable, reproducible behavior.


Diffusion of Innovations Theory - A social science theory explaining how, why, and at what rate new ideas and technologies spread through populations. Key adoption factors include relative advantage, compatibility, complexity, trialability, and observability. This study employs the theory to analyze Iskorly's adoption potential among Filipino teachers.
EXIF (Exchangeable Image File Format) - Metadata embedded in image files by digital cameras, including capture date, camera settings, and orientation. Iskorly reads EXIF orientation tags and applies appropriate rotation corrections to ensure answer sheets appear upright regardless of how the phone was held during capture.


Heatmap - A visual analytics representation using color gradients to indicate patterns, typically showing item difficulty. In Iskorly's analytics, items answered correctly by most students appear in one color (e.g., green), while difficult items answered correctly by few students appear in another (e.g., red), enabling quick identification of problem areas.


High-Contrast Mode - An optional image preprocessing setting in Iskorly that enhances the difference between text and background, improving OCR accuracy for low-quality images with poor lighting, faded ink, or low-contrast handwriting. The enhancement applies brightness and contrast adjustments before submitting the image to the OCR engine.


History Record - A saved assessment entry in Iskorly's local database, containing student name, section, exam title, raw score, total items, percentage score, and timestamp. History records are searchable, editable (rename/delete), and exportable via CSV.


ImageReader - An Android class that provides direct access to image data captured by the camera in various formats (JPEG, RAW, YUV). Iskorly uses ImageReader to extract JPEG byte arrays from Camera2 capture sessions.
Iskorly - The name of the Android-based automated answer sheet scoring application developed in this study. The name derives from Filipino-English hybrid pronunciation of "score," reflecting the app's cultural context and primary function.


JSON (JavaScript Object Notation) - A lightweight, human-readable data interchange format using attribute-value pairs and arrays. Iskorly uses JSON for importing/exporting answer keys and for parsing API responses from OCR services.


Masterlist - A comprehensive analytics dashboard in Iskorly that aggregates assessment data across multiple history entries, displaying Mean Percentage Score (MPS) by section, overall MPS, per-item statistics, and common wrong answers. The masterlist provides teachers with actionable insights for instructional planning.


Mean Percentage Score (MPS) - The arithmetic mean of percentage scores within a defined group (section, exam, or overall dataset), calculated as the sum of all percentage scores divided by the number of entries. MPS serves as an indicator of group performance and instructional effectiveness.


Objective-Type Assessment - A category of test items with predetermined correct answers that can be scored mechanically without subjective judgment, including multiple-choice, true/false, matching, and short-answer questions with single correct responses. Iskorly is designed exclusively for objective-type items.


OCR (Optical Character Recognition) - A technology that converts images of text (printed or handwritten) into machine-encoded text that can be edited, searched, and processed by computer programs. Iskorly employs cloud-based OCR engines to extract student answers from photographed answer sheets.


OCR.Space API - A cloud-based OCR service offering free and commercial tiers for text recognition from images. Iskorly uses OCR.Space as a fallback when the primary Google Cloud Vision API is unavailable or has exceeded quota limits.


OMR (Optical Mark Recognition) - A technology specifically designed to detect marked areas on paper (e.g., filled bubbles on standardized test forms). Unlike OMR, which requires specialized forms, Iskorly uses general-purpose OCR to read alphanumeric text, providing greater flexibility.


Parsing - The computational process of analyzing text to identify structure and extract meaningful data. Iskorly's answer parser uses regular expressions and pattern matching to identify item numbers, extract responses, handle Roman numerals, and filter extraneous text from raw OCR output.


Roman Numeral Conversion - A parsing feature that recognizes Roman numerals (I, II, III, IV, V, VI, VII, VIII, IX, X) in student responses and converts them to Arabic numerals (1, 2, 3, 4, 5, 6, 7, 8, 9, 10) to match answer key format and enable accurate scoring.


SQLite - A lightweight, embedded relational database engine widely used in mobile applications. Iskorly stores assessment history, answer keys, and settings locally in an SQLite database on the user's Android device, ensuring data persistence without requiring external servers.
Time Saved - The difference in minutes required to score a given number of answer sheets (typically expressed per 100 sheets) between traditional manual checking methods and automated scoring using Iskorly. Time saved is a key dependent variable in evaluating the app's efficiency.


Two-Column Mode - An optional OCR processing setting designed for answer sheets with responses arranged in two vertical columns (e.g., items 1–25 in the left column, items 26–50 in the right). When enabled, Iskorly processes each column separately, then merges results, improving recognition accuracy for multi-column layouts.


Universal Answer Input - A flexible input system in Iskorly's answer key setup that accepts any alphanumeric string (letters, numbers, or short text) as a correct answer, rather than restricting input to predefined choices (e.g., A/B/C/D only). This accommodates varied assessment formats including true/false (T/F), numeric answers, or short text responses.


















CHAPTER II


 REVIEW OF RELATED LITERATURE


This chapter provides the review of related literature and studies in relation to the development of the android-based automated answer sheet scoring app using optical character recognition (OCR). Various discussions from different authors are presented to establish the foundation and relevance of this study.


Optical Character Recognition (OCR) in Educational Contexts
Optical Character Recognition has evolved from specialized, expensive technology to accessible cloud services capable of reliably converting images of text into machine‑editable digital text. This transformation has opened possibilities for educational applications, particularly in automating assessment tasks that consume substantial teacher time.


In a foundational study by Thammarak et al. (2022), researchers evaluated the accuracy of Google Cloud Vision API for digitizing official documents (Thai vehicle registration certificates). The research employed a multi-stage approach combining image preprocessing, Google Cloud Vision API text recognition, and domain-specific post-processing using a dictionary-based correction system. The integrated system achieved an average accuracy of 93.28%, demonstrating superior performance compared to the basic Google Cloud Vision API alone (which achieved lower accuracy without post-processing). This study underscores that while cloud-based OCR services provide strong baseline performance, coupling OCR with intelligent post-processing, such as answer parsing algorithms designed for specific document formats; significantly improves practical accuracy. For Iskorly, this finding validates the design decision to implement answer parsing that filters OCR output to match answer key structure and handles specialized patterns (answer-first lines, Roman numerals, etc.), thereby improving recognition reliability beyond raw OCR capabilities.
Saavedra and Uribe (2022) investigated the feasibility of integrating Google Cloud Vision OCR with resource-constrained hardware (Raspberry Pi microcontroller) to create affordable document digitization systems. Recognizing that OCR is computationally intensive, the researchers leveraged cloud-based computation to overcome hardware limitations. Testing on license plates from Colombian vehicles produced 100% OCR accuracy, demonstrating both the practical viability and cost-effectiveness of cloud-assisted OCR for image data automation. This research is particularly relevant to Iskorly's context, as it validates that cloud OCR services can achieve high accuracy on low-cost devices (smartphones costing far less than specialized scanners), making technology-enhanced assessment economically accessible to under-resourced schools in the Philippines and similar developing regions.
A systematic review by Shrivastava et al. (2023) reviewed OCR accuracy across engines and document types, noting performance depends heavily on image quality and preprocessing. Pre‑capture optimization (adequate lighting, stable positioning, appropriate cropping) and post‑capture preprocessing (contrast enhancement) are often as important as the OCR algorithm. Iskorly’s UX emphasizes camera guidance, optional high‑contrast mode, and a crop workflow to support better inputs.
Automated Grading Systems and Teacher Workload Reduction
The integration of automated grading systems into educational settings has been increasingly studied, revealing consistent benefits for teacher efficiency, accuracy, and student engagement, while also identifying implementation challenges relevant to Iskorly's deployment context.
Langove and Khan (2024) conducted an exploratory mixed-methods study examining the effects of automated grading and feedback systems on teacher workload and student performance. The research employed quantitative classroom observation before and after implementing automated grading systems. The findings revealed a substantial reduction in teacher grading time: from an average of 15 hours per week to 9.75 hours per week, representing a 35% reduction in time spent on assessment administration. Concurrently, student engagement metrics improved measurably: assignment submission rates increased from 70% to 88% (25% increase), and classroom contribution/participation increased from 65% to 81% (25% increase). The researchers concluded that reduced grading burden freed teacher time for higher-order pedagogical activities, which in turn increased classroom engagement and student accountability. This study directly supports Iskorly's value proposition: automating scoring should reduce teacher workload substantially, creating time for instructional innovation and student support while potentially enhancing overall classroom dynamics.
Rodriguez et al. (2021) conducted a systematic meta-analysis synthesizing evidence from 47 studies on automated assessment systems. The meta-analysis examined accuracy, time savings, and student outcomes across diverse educational technologies. Key findings indicated that automated grading systems maintained or exceeded accuracy of manual grading (mean accuracy approximately 92% across studies), while requiring 60–75% less time for scoring comparable assessments. Importantly, the meta-analysis revealed that time savings were most dramatic for objective-type assessments (multiple-choice, true/false, short-answer) with less impact for subjective assessments requiring rubric-based judgment. Student outcomes showed no significant difference between automatically graded and manually graded assessments when compared on final achievement measures, but students receiving automated feedback experienced faster feedback turnaround and reported slightly higher satisfaction with assessment clarity. These findings validate Iskorly's focus on objective-type assessments, where automation delivers maximum time savings and maintains accuracy, while acknowledging that subjective assessments remain appropriately within the domain of teacher judgment.
Warschauer and Liaw (2010), while older than the target 2020–2025 range, remains highly cited in contemporary research on technology-assisted assessment. However, a more recent study by Chen et al. (2023) examined teacher perceptions of automated grading systems in secondary schools across three Asian countries (including data from two Philippine schools). The research employed survey methodology with 287 teachers and qualitative interviews with 45 teachers. Key findings indicated that teachers valued automated systems primarily for time savings (92% of respondents), accuracy improvement (87%), and faster feedback to students (89%). However, teachers also expressed concerns: 68% worried about OCR errors on handwritten responses, 54% had concerns about data privacy, and 42% expressed anxiety about reduced professional autonomy in grading decisions. The study concluded that teacher acceptance of automated systems depends not only on demonstrated efficiency but on clear communication about system accuracy, robust privacy protections, and explicit framing of automation as supporting (not replacing) teacher judgment. For Iskorly's deployment in Philippine schools, this research highlights the importance of teacher training emphasizing manual correction workflows, transparent communication about OCR accuracy limitations, and privacy-first data handling practices.


Mobile Technology and Educational Assessment in Developing Contexts
The proliferation of smartphones, particularly Android devices, in developing regions has created opportunities for mobile-first educational solutions addressing persistent barriers to technology integration, such as infrastructure limitations and cost constraints.
According to Statista (2024) market data, the Philippines has achieved mobile penetration exceeding 80% of the population, with Android devices representing approximately 87% of the smartphone market share. Mobile data usage per capita in the Philippines has grown dramatically, with average smartphone users accessing 4–6 hours of daily screen time. Notably, smartphone penetration reaches even rural areas, though internet connectivity remains inconsistent. This market context with high device availability but variable internet quality is directly influenced by Iskorly's technical design: the application operates on personal devices requiring no institutional IT infrastructure, queues OCR requests to accommodate intermittent connectivity, and implements local data storage minimizing dependence on cloud services for core functionality.
Kaliisa and Picard (2019), while slightly outside the 2020–2025 window, remains the most cited systematic review of mobile learning in sub-Saharan Africa and is frequently cited in contemporary Philippine EdTech research. However, more recent work by Adeyinka et al. (2023) examined mobile-assisted learning adoption across diverse African contexts, finding that mobile devices dramatically expand educational access in resource-constrained settings, particularly for students in rural areas. The research identified critical success factors: applications must function on older/lower-end devices, accommodate low-bandwidth conditions, minimize data consumption, and align with existing pedagogical practices rather than requiring wholesale instructional redesign. These findings align with Iskorly's design philosophy: the app functions on standard Android devices (including older models), queues OCR requests for asynchronous processing accommodating intermittent connectivity, and integrates with existing paper-based assessment workflows familiar to Philippine teachers.
Traxler (2023) provided a contemporary perspective on mobile learning in developing regions, arguing that while mobile technology offers unprecedented educational access, successful integration depends on understanding local contexts like infrastructure realities, teacher capacities, student expectations, and cultural educational values. The author cautioned against "technology solutionism" (assuming technology automatically improves education) and emphasized that meaningful mobile EdTech requires deliberate design attending to contextual constraints. In the Philippine context specifically, Traxler's analysis suggests that Iskorly's success depends not merely on OCR accuracy or technical sophistication, but on how well it integrates with existing classroom practices (exchange-paper checking, communal auditing, informal peer grading) and respects cultural values (collective responsibility, teacher authority, privacy protection).


Data-Driven Instruction and Analytics for Teachers
Educational research increasingly demonstrates the power of formative assessment data and item-level analytics to inform instructional decisions, particularly when analytics are presented in formats teachers can quickly understand and act upon.
Contemporary research by Nicol (2021) applied this framework to technology-assisted assessment, finding that automated systems can enhance feedback effectiveness by providing immediate feedback (enabling rapid student response), specific feedback about which items were incorrect and why, and comparative feedback showing performance relative to class peers. Importantly, Nicol emphasizes that system-generated analytics are most valuable when teachers interpret and communicate results to students, rather than assuming automated reports alone drive learning.
Wiliam and Leahy (2015), while slightly older, remains highly relevant to contemporary assessment analytics. Their formative assessment framework identifies five key components: sharing learning goals with students, engineering classroom discussions/activities to elicit evidence of learning, providing feedback promoting learning, activating students as instructional resources, and activating students as owners of their own learning. Subsequent research by Schneider and Preckel (2017) examined which formative assessment practices most reliably predict improved student outcomes. The analysis identified that item-level analysis (identifying which specific concepts students struggle with) combined with teacher professional judgment about instructional response (adjusting teaching to address identified gaps) most reliably improved outcomes. For Iskorly, this research validates the design emphasis on per-item difficulty statistics, common wrong answer identification, and Mean Percentage Score by section analytics, specifically designed to help teachers identify struggling concepts and plan targeted re-teaching.
A more recent study by Vattøy and Smith (2023) examined how teachers use formative assessment data in practice. Through qualitative classroom observations and teacher interviews, researchers found that teachers most readily use assessment data when analytics are: (1) presented in simple, visually clear formats (avoiding complex statistical jargon), (2) actionable (immediately suggesting instructional implications rather than merely describing performance), and (3) aligned with teachers' existing decision-making processes and curricular structure. Teachers reported using analytics most frequently to identify students needing intervention, regroup for differentiated instruction, and adjust pacing/emphasis of curriculum topics. However, teachers rarely modified assessment design or undertook deeper curriculum redesign based on analytics, suggesting that simpler, action-oriented analytics generate more immediate classroom impact than complex comparative or predictive analytics.


Educational Technology Adoption and Teacher Acceptance
Understanding factors influencing teacher acceptance of new technologies is critical for successful deployment of any educational innovation, particularly in resource-constrained contexts where institutional support for adoption may be limited.
Diffusion of Innovations Theory, originally developed by Rogers (1962) but continuously refined through contemporary research, identifies five key attributes influencing adoption decisions: relative advantage (perceived benefits vs. alternatives), compatibility (alignment with existing practices and values), complexity (ease of learning and use), trialability (ability to experiment on limited basis), and observability (visibility of results). Contemporary applications of the theory to educational technology include research by Holden and Rada (2011) and more recent work by Tarhini et al. (2023) examining technology adoption in Middle Eastern educational contexts. The research confirms that relative advantage (time savings, efficiency gains), compatibility with existing practices, and simplicity of use remain primary adoption drivers, with these factors often more influential than technical sophistication.
A particularly relevant study by Handal et al. (2020) examined barriers and enablers of educational technology adoption specifically in under-resourced schools in four developing countries (including one Philippine school). Through mixed-methods research involving surveys of 156 teachers and qualitative interviews with 24 school leaders, researchers identified critical adoption factors: (1) demonstrated efficiency gains relative to existing methods, (2) low cost and minimal infrastructure requirements, (3) perceived professional autonomy (technology seen as supporting rather than constraining teacher judgment), (4) institutional leadership support enabling pilot testing and training, and (5) peer influence and social proof (seeing respected colleagues successfully using technology). The study found that teacher concerns about technology (technical problems, data security, time required for training) were substantially reduced when schools provided adequate training and demonstrated peer success. For Iskorly deployment in Philippine schools, this research suggests that adoption success depends on: clearly demonstrating time savings (35% reduction benchmark from Langove & Khan 2024), ensuring straightforward operation requiring minimal training, providing manual override/correction workflows preserving teacher judgment, securing leadership support enabling pilot testing, and facilitating peer observation and sharing among teachers.
A more recent study by Kim et al. (2023) examined technology adoption trajectories over time, finding that initial adoption depends on relative advantage and ease of use, but sustained adoption depends on demonstrated value, perceived community support, and alignment with teacher professional identity. Importantly, technology initially adopted may be abandoned if perceived benefits diminish, technical problems emerge, or institutional support declines. This research suggests that Iskorly's deployment strategy should attend not only to initial adoption but to sustained use through: ongoing technical support addressing OCR errors and system bugs, regular professional development reinforcing analytics interpretation and instructional application, and building a community of practice where teachers share successful uses of Iskorly and troubleshoot challenges collectively.






Grand Synthesis
The literature across these themes collectively establishes a robust foundation for Iskorly's design and anticipated impact. Optical Character Recognition technology has achieved sufficient accuracy (90–98% on quality images, 80–95% on typical classroom captures) and cost-effectiveness to enable practical educational applications. Cloud-based OCR services like Google Cloud Vision and OCR.Space provide reliable, affordable infrastructure, and intelligent post-processing (answer parsing, format filtering) further improves accuracy beyond raw OCR capabilities. Automated grading systems, when applied to objective-type assessments, achieve accuracy comparable to or exceeding manual grading while reducing scoring time by 35–75%, and automated systems have been shown to accelerate feedback delivery improving student engagement. Mobile-first technological approaches are particularly appropriate for developing regions like the Philippines, where smartphone penetration is high but institutional IT infrastructure may be limited, provided applications accommodate variable connectivity and function on diverse device capabilities. Analytics focused on item-level difficulty and common misconceptions are most effectively used by teachers when presented in simple, actionable formats clearly suggesting instructional implications. Finally, teacher adoption of new technologies depends critically on demonstrated relative advantage, compatibility with existing practices, simplicity of use, institutional support, and peer influence all factors that can be intentionally cultivated through thoughtful deployment strategy.
For Iskorly specifically, the research literature validates: the core technical approach (cloud-based OCR with intelligent parsing), the choice to focus on objective-type assessments where automation delivers maximum benefit, the specific analytics dashboard design emphasizing simple, actionable insights, the mobile-first platform choice appropriate to Philippine contexts, and the implementation of manual correction workflows preserving teacher autonomy and accounting for inevitable OCR errors. The literature also identifies critical success factors beyond technical performance: transparent communication about system capabilities and limitations, robust teacher training and support, school leadership buy-in enabling pilot testing and adoption, and cultivation of peer learning communities sustaining long-term use.


Research Gap
While the literature establishes the technical feasibility and pedagogical potential of OCR-based automated assessment, significant gaps remain particularly relevant to Iskorly's development context:
* Limited evidence from Philippine educational contexts: Research on OCR-based assessment systems has been conducted primarily in developed countries (North America, Europe, East Asia developed regions) or generalized developing contexts. Evidence specifically addressing performance and adoption in Philippine classrooms with characteristic variable lighting, diverse handwriting styles, intermittent connectivity, specific teacher workload patterns, and culturally particular assessment practices remains limited.
* Incomplete guidance on image quality optimization: While research establishes that image quality critically affects OCR accuracy, practical guidance for educational end-users on optimal capture conditions remains under-addressed. How do teachers with varying technical expertise achieve adequate image quality in real classrooms?
* Limited attention to manual correction workflows: Research on automated systems addresses either fully automated workflows or manual regrading post-automation, but gives limited attention to integrated workflows where teachers quickly review and manually correct OCR errors before finalizing scores. How does this hybrid approach affect actual time savings and accuracy?
* Insufficient evidence on analytics interpretation by teachers: While research identifies which analytics formats teachers prefer, empirical evidence on how teachers actually interpret and act upon item-level analytics generated by automated systems in real classrooms remains limited, particularly in resource-constrained educational contexts.
* Gaps in sustained adoption research: Most studies address initial adoption or short-term impacts (semester or year-long). Evidence on sustained adoption factors, maintenance of technical support, and long-term integration into teaching practice remains sparse.






























CHAPTER III


METHODOLOGY


This chapter describes the research design, materials, procedures, and analytical methods employed to evaluate Iskorly, the Android-based automated answer sheet scoring application using Optical Character Recognition (OCR). The methodology is designed to address the five research questions while maintaining rigor appropriate for Grade 12 STEM research and feasibility within the constraints of a single academic year development and evaluation cycle.


Research Design
        This study employs a developmental research design with descriptive‑evaluative components to assess Iskorly (Version 1.8.1), an Android‑based automated answer sheet scoring application. The methodology integrates quantitative descriptive statistics with qualitative user feedback to comprehensively evaluate the application’s User Interface (UI) and User Experience (UX) quality in authentic Philippine classroom contexts.


Quantitative component: Two 10‑item Likert‑scale instruments (UI and UX) administered to teacher‑users (N = 30). Descriptive statistics include mean (x̅), standard deviation (σ), and verbal interpretation (VI) for each indicator and overall dimensions. Data are analyzed to determine whether mean ratings meet or exceed the “Agree” threshold (x̅ ≥ 4.00 on a 5‑point scale).


Qualitative component: Open‑ended survey responses capturing perceived strengths, limitations, and improvement suggestions, alongside optional brief interviews (n = 3–5 teachers) exploring analytics interpretation and instructional decision‑making based on Iskorly results.


Table 1
Research Material/Equipment
Item/Tool
	Description
	Usage
	Android Smartphone
	Android 7.0+ with 8MP+ camera, 2GB+ RAM, 100MB+ storage
	Device for running Iskorly app, capturing answer sheet images
	Answer Sheets
	Standard 8.5×11" paper with printed or clearly handwritten multiple- choice responses
	Test material for accuracy and performance evaluation
	Answer Key
	Pre-prepared correct answers for each assessment
	Ground truth for scoring comparison
	Iskorly App
	Functional Android application v1.0
	Primary tool for automated scoring
	Google Cloud Vision API
	Cloud OCR service with active API key
	Primary OCR engine
	OCR.Space API
	Cloud OCR service with active API key
	Fallback OCR engine
	Stopwatch/Timer App
	Android timer application or physical stopwatch
	Measuring scoring time for both manual and automated methods
	CSV Template
	Spreadsheet template for data organization
	Recording and analyzing results
	Desktop/Laptop Computer
	Computer with spreadsheet software (Excel, Google Sheets)
	Data analysis, statistics computation
	

Table 1. Research Materials and Equipment. This table summarizes all hardware, software, and materials required to develop, test, and evaluate Iskorly in accordance with the study methodology.




















Project Design
  























Project Development/Experimental Procedure
Project Development/Experimental Procedure
Figure 2
Schematic Diagram of the Project Development 
  

This schematic diagram illustrates the systematic development process for Iskorly, an Android-based automated answer sheet scoring application utilizing Optical Character Recognition (OCR) technology. The development follows a sequential workflow beginning with requirements identification and sample answer sheet collection, followed by application framework and user interface design to ensure an intuitive, teacher-friendly platform.
The core technical development involves integrating the OCR module through Google Cloud Vision API (primary) and OCR.Space API (fallback), creating a standardized answer sheet template compatible with OCR processing, and implementing the scoring algorithm with answer key encoding and automated checking functionality. A local SQLite database is established to securely store student scores and assessment records, enabling efficient data retrieval and CSV export for further analysis.
Testing and refinement comprise the final stages of development. The system undergoes systematic testing to identify and resolve recognition errors, followed by pilot testing with teachers and students to gather real-world usability feedback. Based on pilot results, features are optimized to improve accuracy, workflow efficiency, and user experience. The development concludes with comprehensive performance evaluation documenting scoring accuracy, time efficiency, common error patterns, and user satisfaction metrics. Findings from this evaluation inform recommendations for future improvements and guide deployment strategies for classroom implementation.
Throughout the development cycle, the team maintains iterative feedback loops, ensuring that technical capabilities align with authentic classroom needs and that the application remains accessible to teachers with varying levels of technological proficiency.


Research Settings
        The research was conducted at Lyceum of Alabang (Tunasan, Muntinlupa City) where the researchers had institutional access to teachers and classrooms, as well as at Landayan, San Pedro, Laguna, Philippines, where the development team conducted coding, integration testing, and pilot evaluations. The evaluation context includes both controlled settings (dedicated testing space with standardized lighting) and authentic classroom environments (variable lighting, typical classroom furniture, realistic noise/activity levels) to ensure findings are applicable to real-world deployment conditions.




Testing Procedure 
The testing procedure for evaluating Iskorly comprises six sequential phases: preparation, automated scoring, accuracy comparison, time efficiency assessment, user satisfaction evaluation, and OCR settings effectiveness testing. Each phase is designed to generate specific data addressing the research questions while maintaining methodological rigor appropriate for Grade 12 STEM research.


Phase 1: Preparation and Baseline Establishment
Step 1: Answer Sheet Collection and Manual Scoring
* Collect 10-100 answer sheets from actual Grade 7–12 classroom assessments representing diverse conditions: printed responses, handwritten responses, varied handwriting legibility, and images captured under different lighting conditions (bright natural daylight, indoor fluorescent lighting, mixed lighting).
* Identify one trained teacher scorer to conduct manual scoring of all answer sheets, serving as the gold standard baseline. The teacher scorer marks each response against the established answer key, records total correct items and percentage score, and documents any ambiguous responses or marking challenges.
Step 2: Answer Key Setup in Iskorly
* Create and input the answer key into Iskorly, specifying: section name, exam title, total item count, item numbers (1 through N), and correct response for each item.
* Document baseline system status: storage available, API key functionality confirmed, OCR engines responsive.






Phase 2: Automated Scoring via Iskorly 
Step 3: Image Capture and Initial Processing
For each of the answer sheets, following the procedure illustrated in Figure 3:  
















































  













Figure 3. Image Capture Procedure Flow. Detailed steps from application launch through image acquisition and orientation correction, including optional cropping workflow.


Step 4: OCR Processing and Answer Parsing
For each captured image, execute the procedure illustrated in Figure 4:
  



























  

























  



















Figure 4. OCR Processing and Answer Parsing Procedure. Detailed steps from API submission through text extraction, format parsing, and alignment to answer key.


Step 5: User Review and Manual Correction
For each answer sheet processed via Iskorly:
* Display parsed answers on-screen with visual indicators: green checkmarks for items successfully parsed, yellow warnings for ambiguous parsing, red flags for unparsed items.
* Allow the user to review each parsed answer and manually correct any OCR misreads before finalizing the score.
* Record which items required manual correction and categorize error types (e.g., digit confusion: 0/O, 1/I, 5/S; skipped items; Roman numeral misrecognition; illegible handwriting).
* Document whether enabling two-column mode or high-contrast enhancement improved parsing accuracy for specific problematic sheets.
* Once confirmed, finalize and save the score to SQLite history database with timestamp.


Phase 3: Time and Accuracy Measurement
Step 6: Data Collection for Accuracy and Efficiency
For each answer sheet, record:
Data Point
	Recording Method
	Purpose
	Manual teacher score (%)
	Record from gold standard baseline
	Accuracy comparison baseline
	Iskorly automated score (%)
	Read from app after finalization
	Accuracy comparison test
	Manual scoring time per sheet
	Calculate from total batch time ÷ sheet count
	Time efficiency baseline
	Iskorly scoring time per sheet
	Timer from capture to finalized score
	Time efficiency test
	OCR errors encountered
	Document type and frequency
	Error categorization
	Settings used (normal/two-column/high-contrast)
	Record for each problematic sheet
	Settings effectiveness tracking
	Image quality notes (lighting, handwriting legibility)
	Subjective observation
	Error analysis by condition
	

Table 2. Data Collection Matrix for Accuracy and Efficiency Assessment.
Step 7: Calculate Accuracy Metrics
* Percent agreement: Count sheets where Iskorly score = manual teacher score; express as (# matching ÷ total sheets) × 100.
* Mean score difference: Calculate |Iskorly score – manual score| for each sheet; report average of all differences in percentage points.
* Error distribution: Categorize errors by magnitude (0–5 points, 6–10 points, 11+ points difference) and by answer type (printed vs. handwritten, multiple-choice vs. short-answer).
Step 8: Calculate Efficiency Metrics
* Average time per 100 sheets: Manually: total time for 100 sheets ÷ 100. Via Iskorly: total time from first capture to last finalized score ÷ 100.
* Percentage time saved: [(manual time – Iskorly time) ÷ manual time] × 100.
* Time variability: Note whether time per sheet changed as operators became more familiar with Iskorly (learning curve effect).


Phase 4: OCR Settings Effectiveness Testing 
Step 9: Settings Comparison on Problem Sheets
On a subset of 20 problematic answer sheets (identified during earlier processing as having OCR difficulties):
  























  





































Figure 5. OCR Settings Effectiveness Testing Procedure. Each problematic sheet tested across three OCR modes, with accuracy recorded and compared to identify optimal settings for specific image conditions.


Phase 5: User Satisfaction and Qualitative Feedback 
Step 10: Administer Satisfaction Survey
Distribute 5-item Likert survey to 8–12 teachers and 15–20 students who used Iskorly:


Survey Item
	Scale
	Purpose
	1. The Iskorly app was easy to use
	1=Strongly Disagree to 5=Strongly Agree
	Usability perception
	2. Iskorly saved me time compared to manual checking
	1=Strongly Disagree to 5=Strongly Agree
	Efficiency perception
	3. Iskorly's scores were accurate
	1=Strongly Disagree to 5=Strongly Agree
	Accuracy perception
	4. The results and analytics were clear and easy to understand
	1=Strongly Disagree to 5=Strongly Agree
	Clarity perception
	5. I would use Iskorly for future assessments
	1=Strongly Disagree to 5=Strongly Agree
	Adoption likelihood
	Open-ended: What did you like about Iskorly?
	Text response
	Perceived strengths
	Open-ended: What could be improved?
	Text response
	Perceived limitations
	Table 3. User Satisfaction Survey Instrument.


Step 11: Optional Qualitative Interviews
Conduct brief 3-5 minute interviews with teachers exploring:
* How did you interpret the Mean Percentage Score (MPS) by section?
* Did the item difficulty statistics (% correct per item) influence your instructional decisions?
* What technical problems, if any, did you encounter?
* What would encourage you to adopt Iskorly in your regular teaching practice?
* What data privacy or security concerns do you have?
Record key themes and representative quotes for qualitative analysis.


Statistical Treatment
Inferential Statistics (Analysis of Variance) and Descriptive Statistics (Mean): The data to be gathered in this study will be subjected to the following statistical treatment in order to analyze and test hypotheses and address the research questions.
Group Mean is used to summarize the average value of a set of data points for easier comprehension and to facilitate comparison between groups. The formula is as follows:
 = 
Where:
* = mean value of the set of given data
*  = frequency of each class
*  = mid-interval value of each class
Using the data gathered during the accuracy comparison and time efficiency testing, the group mean will be utilized to calculate: (1) the mean accuracy (% agreement with manual scoring) across all 100–150 answer sheets processed via Iskorly, (2) the mean time per 100 sheets for both manual and automated scoring methods, and (3) the mean satisfaction rating for each of the five Likert survey items.
The group mean denotes the average performance metric within each tested condition or group. For example, if Iskorly accuracy ranges from 85–98% across the sample, the group mean accuracy would represent the typical or central performance level. By calculating group means for both Iskorly automated scoring and manual teacher scoring separately, the deviation of individual sheets from each group's mean can be quantified. This deviation is critical for calculating the Within-Groups Sum of Squares (SSW), which reflects the variance in performance within each method and determines whether observed differences between methods are due to systematic differences or merely random variation.


Grand Mean is used to calculate the mean of all observations across every data point of multiple subsamples. The formula is as follows:
= 
Where:
   *  = total number of sets (groups)
   * ∑= sum of the mean of all sets
The grand mean is used for the calculation of the mean score across all testing conditions and methods (manual vs. Iskorly, across all 10-100 sheets). For time efficiency, the grand mean represents the overall average time per 100 sheets across both methods. The grand mean serves as the reference point for calculating the Between-Groups Sum of Squares (SSB), which corresponds to how much each group mean differs from the overall (grand) mean. This variance is the key to determining whether the differences between groups are large enough to be statistically significant and not merely due to chance.


Standard Deviation
Standard Deviation is a statistical measurement that shows how spread out the data are in relation to the mean. The formula is as follows:
 
Where:
      *  = the value in the data distribution (individual sheet score or time)
      *  = the sample mean
      *  = total number of observations
The standard deviation will be used in this study to identify how unevenly the data is distributed across both Iskorly and manual scoring methods. Standard deviation is particularly useful when comparing two datasets with identical or very similar means; even if Iskorly and manual scoring achieve similar average accuracy, standard deviation allows us to compare the variability between the methods. For example, if Iskorly has lower standard deviation than manual scoring, this indicates that Iskorly produces more consistent, predictable results, whereas manual scoring shows more unpredictable variation. This comparison of variability is essential when evaluating whether Iskorly is not only accurate on average but also reliable.


Application to Iskorly Study
These statistical treatments will be applied as follows:
         * Descriptive Statistics (Group Mean, Standard Deviation): Used to characterize the distribution of scores and times for both manual and Iskorly methods, providing baseline performance metrics.
         * User satisfaction statistics: Descriptive statistics (mean, standard deviation) summarized for each five-item Likert survey item to characterize overall user satisfaction (Hypothesis H4).


All statistical calculations will be performed using spreadsheet software (Microsoft Excel or Google Sheets) or statistical analysis tools accessible to Grade 12 students. Results will be presented in tables with clearly labeled columns, and interpretations will be written in non-technical language emphasizing practical significance (e.g., "Iskorly reduced grading time by 35%, saving approximately 5 hours per 100 sheets") alongside statistical significance (e.g., "This time saving was statistically significant, p < 0.05"). Any hypothetical or placeholder values will be clearly marked as such, with notation indicating where observed data should be substituted upon completion of data collection.


Ethical Consideration
        This research adheres to established ethical principles protecting the rights, privacy, and well-being of research participants while ensuring responsible and transparent conduct throughout all stages of the study. The researchers recognize their obligation to conduct research with integrity, respect participant autonomy, safeguard confidential information, and use technology responsibly.


Informed Consent and Voluntary Participation. All teachers and students participating in user satisfaction surveys, interviews, or any data collection were provided with clear, written informed consent explaining the study's purpose, procedures, duration, and how their responses would be used. Participation was entirely voluntary, with participants explicitly informed that they could withdraw at any time without penalty or explanation. Consent forms were written in clear, accessible language explaining that the research aimed to evaluate Iskorly's technical performance and usability, that their feedback would help improve the application, and that their responses would be treated confidentially. For student participants, parental consent was obtained where required by school policy, and student assent was secured separately. Researchers ensured that participants understood they could decline to participate or withdraw consent at any point without affecting their grades, relationship with teachers, or access to school services.


Data Privacy and Confidentiality. Student answer sheets and scores were handled with strict confidentiality protections. All personally identifying information (student names, ID numbers, class sections) was removed from data records used in statistical analysis and reporting; data were instead identified by anonymous numeric codes. Survey responses and interview transcripts were kept confidential, and participant identity was not disclosed in any research reports or presentations. No student faces, personal images, or identifying photographs were captured or stored; only images of the answer area of sheets were photographed, and these images were deleted after OCR processing and score verification. No unnecessary personal data were collected or retained; only information directly relevant to evaluating Iskorly's performance was recorded. Raw data files containing any identifiable information were stored securely on password-protected devices, and access was restricted to the research team only.


API Key Management and Data Security. Google Cloud Vision API keys and OCR.Space API credentials were treated as sensitive, confidential information critical to system security. API keys were stored securely using standard authentication and credential management practices, never embedded in public-facing code, never shared with unauthorized individuals, and never exposed in version control systems such as GitHub. API usage was monitored to ensure compliance with service terms of service, to prevent quota violations, and to avoid unexpected costs. The researchers understood that submitting answer sheet images to cloud OCR services involves temporary data transmission to external servers and communicated this transparently to school administrators and participants. While Google Cloud Vision and OCR.Space comply with data protection standards and delete submitted images after processing, the researchers acknowledged this data transmission in privacy discussions and obtained institutional approval for this practice.


Responsible Use of Artificial Intelligence and Automation. The researchers employed commercial OCR services (Google Cloud Vision API and OCR.Space API) for legitimate educational assessment purposes with transparent acknowledgment that automated systems have inherent limitations and require human oversight. Iskorly was explicitly designed to support, not replace, teacher professional judgment; manual review and correction workflows were integrated to ensure teachers retained authority over scoring decisions. The researchers recognized that OCR can make errors, particularly on poor-quality images or ambiguous handwriting, and deliberately incorporated workflows allowing teachers to review, correct, and override automated scores before finalizing results. Findings will be reported honestly and completely, including transparent discussion of OCR limitations, documented error rates, confidence intervals, and cases where automated scoring proved inappropriate or unreliable. The researchers avoided over-claiming the capabilities or accuracy of Iskorly and instead presented realistic, evidence-based performance characteristics that teachers and administrators could use to make informed adoption decisions.


Institutional Approval and Compliance. The study was conducted under institutional approval from Lyceum of Alabang administration and research oversight authorities. All protocols were reviewed for compliance with school policies regarding data handling, student privacy, technology use, and research ethics. The researchers obtained necessary institutional approvals before beginning data collection and maintained ongoing compliance with institutional guidelines throughout the study. The research protocol adhered to principles outlined in the Philippine Health Research Ethics Board (PHREB) guidelines and followed best practices for research involving human subjects in educational settings, even though the research did not involve medical or sensitive interventions.


Occupational Safety and Well-Being of Researchers. The researchers followed safe practices during extended testing and data entry activities, including proper ergonomic positioning to minimize eye strain and repetitive stress injury, appropriate breaks during extended sessions involving computer screens and camera work, and mental health awareness given the potentially repetitive nature of data collection. While the research did not involve hazardous materials or dangerous activities, the researchers maintained awareness of their own well-being and sought support from supervisors or advisors if research activities caused excessive stress.


Accessibility and Inclusive Research Practices. Procedures and materials were designed to be accessible to participants with diverse backgrounds, technology experience levels, and English proficiency. On-screen instructions in Iskorly were clear and concise, with consideration given to teachers unfamiliar with educational technology or technical terminology. Survey instruments and consent forms were available in both English and Filipino where feasible, ensuring that language barriers did not prevent participation or understanding. Researchers ensured that accommodations were available for participants with accessibility needs and that no individuals were excluded from participation based on technology experience, language fluency, or learning differences.


Transparency and Honest Reporting. All research findings will be reported truthfully and completely, including negative or unexpected results, limitations of the study, and areas where Iskorly underperformed expectations. The researchers will not selectively report only positive results or omit findings that complicate the narrative about the application's effectiveness. Acknowledgment of limitations and constraints will be explicit and prominent in research reports. The researchers committed to distinguishing between conclusions supported by the data and interpretations or recommendations that go beyond the evidence, clearly labeling the latter as such.


CHAPTER IV




RESULTS AND DISCUSSION
 
This chapter presents the descriptive statistical findings addressing two research questions examining the User Interface (UI) and User Experience (UX) quality of Iskorly, the Android-based automated answer sheet scoring application. Results are organized to systematically address each research question using mean scores, standard deviations, and verbal interpretations derived from a 10-item Likert-scale instrument administered to teacher-users. The findings provide empirical evidence of Iskorly’s perceived quality in both interface design and user experience dimensions, offering insights for continued refinement and deployment in Philippine classroom contexts.


Descriptive Results: User Interface
Research Question 1 examined: What is the perceived quality of Iskorly’s User Interface (UI) as measured by a 10-item instrument (Mean, SD, VI)? Table 1 presents the descriptive statistics for all UI indicators, revealing consistently high ratings across visual design, navigational clarity, functional feedback mechanisms, and screen compatibility dimensions.












Table 1 
Descriptive Statistics: User Interface Quality 
Item
	x̅
	σ
	VI
	1. The app’s visual design is clean and professional.
	4.80
	0.40
	Strongly Agree
	2. Text and Icons are easy to read and understand.
	4.83
	0.37
	Strongly Agree
	3. The color scheme and layout do not distract me from my tasks.
	4.66
	0.54
	Strongly Agree
	4. Buttons and menus are clearly labeled and easy to locate.
	4.86
	0.43
	Strongly Agree
	5. The camera preview screen provides adequate guidance for capturing images.
	4.60
	0.62
	Strongly Agree
	6. Visual feedback (e.g., green checks, red flags) helps me identify errors.
	4.66
	0.66
	Strongly Agree
	7. The analytics dashboard displays information in a clear format.
	4.76
	0.50
	Strongly Agree
	8. Navigation between screens (capture, review, history) is intuitive.
	4.73
	0.44
	Strongly Agree
	9. The app works well on my Android device’s;s screen size.
	4.76
	0.43
	Strongly Agree
	10. Overall, I find the app’s interface is visually appealing.
	4.76
	0.43
	Strongly Agree
	Overall UI Mean
	4.74
	—
	Strongly Agree
	 
Table 1 reveals that all ten UI indicators received mean scores ranging from 4.60 to 4.86 on the 5-point Likert scale, with an overall UI mean of approximately 4.74, consistently interpreted as “Strongly Agree.” This pattern indicates that teacher-users perceived Iskorly’s interface as highly satisfactory across multiple design dimensions. The highest-rated UI indicator was Item 4 (“Buttons and menus are clearly labeled and easy to locate”) at x̅ = 4.86, suggesting exceptional clarity in navigational elements and labeling conventions. This finding aligns with usability principles emphasizing the importance of clear affordances and intuitive menu structures in reducing cognitive load during task execution. Item 2 (“Text and Icons are easy to read and understand”) followed closely at x̅ = 4.83, reinforcing that visual legibility and iconographic clarity were effectively implemented.
Comparatively lower though still high ratings were observed for Items 5 and 6 (both x̅ = 4.60–4.66). Item 5 (“The camera preview screen provides adequate guidance for capturing images”) received x̅ = 4.60 with the highest standard deviation (σ = 0.62), indicating slightly greater variability in user perceptions. This suggests that while most teachers found camera guidance adequate, a minority may have experienced uncertainty during image capture, possibly due to varying familiarity with smartphone camera interfaces or differences in classroom lighting conditions. Item 6 (“Visual feedback helps me identify errors”) similarly scored x̅ = 4.66 with σ = 0.66, the highest variability among all UI items. This variability may reflect differences in individual preferences for feedback mechanisms or situational factors affecting error detection visibility.
Items addressing specific interface components, analytics presentation (Item 7, x̅ = 4.76), navigation intuitiveness (Item 8, x̅ = 4.73), screen compatibility (Item 9, x̅ = 4.76), and overall visual appeal (Item 10, x̅ = 4.76), all received consistently high ratings with relatively low standard deviations (σ = 0.43–0.50), indicating consensus among users. The analytics dashboard’s clarity (Item 7) particularly validates design decisions prioritizing simple, actionable data visualizations over complex statistical displays, aligning with research emphasizing that teachers benefit most from straightforward analytics formats suggesting immediate instructional implications.
The overall UI mean of 4.74 demonstrates that Iskorly’s interface design successfully balances professional aesthetics, functional clarity, and navigational simplicity. Standard deviations across all items remained relatively low (0.37–0.66), indicating that user perceptions were generally consistent and that the interface performed reliably across diverse user contexts. These findings validate design principles emphasizing minimalist layouts, clear labeling, intuitive navigation hierarchies, and contextually appropriate visual feedback mechanisms, all critical factors in technology acceptance within educational settings.


Descriptive Results: User Experience
Research Question 2 examined: What is the perceived quality of Iskorly’s User Experience (UX) as measured by a 10-item instrument (Mean, SD, VI)? Table 2 presents descriptive statistics for all UX indicators, encompassing learnability, task completion efficiency, workflow integration, system responsiveness, error correction ease, data management convenience, export functionality, analytics utility, time savings, and adoption likelihood.












Table 2
Descriptive Statistics: User Experience Quality
 
Item
	x̅
	σ
	VI
	1. Learning to use Iskorly was quick and easy.
	4.66
	0.54
	Strongly Agree
	2. I can complete scoring tasks without confusion or frustration.
	4.56
	0.56
	Strongly Agree
	3. The image capture process is straightforward and efficient.
	4.60
	0.62
	Strongly Agree
	4. The app responds quickly to my actions without lag.
	4.60
	0.62
	Strongly Agree
	5. Manual correction of OCR errors is simple and does not disrupt workflow.
	4.67
	0.54
	Strongly Agree
	6. Saving and accessing answer keys is convenient.
	4.76
	0.43
	Strongly Agree
	7. Exporting scores to CSV meets my record-keeping needs.
	4.86
	0.34
	Strongly Agree
	8. The analytics (MPS, item stats) provide useful insights for teaching.
	4.70
	0.46
	Strongly Agree
	9. Iskorly saves me significant time compared to manual checking.
	4.73
	0.44
	Strongly Agree
	10. I would recommend Iskorly to other teachers.
	4.83
	0.37
	Strongly Agree
	Overall UX Mean
	4.70
	—
	Strongly Agree
	 
Table 2 demonstrates that all UX indicators received mean scores ranging from 4.56 to 4.86, with an overall UX mean of approximately 4.70, consistently interpreted as “Strongly Agree.” This pattern indicates high user satisfaction across diverse experiential dimensions encompassing learnability, efficiency, workflow integration, and perceived value. The highest-rated UX indicator was Item 7 (“Exporting scores to CSV meets my record-keeping needs”) at x̅ = 4.86 with the lowest standard deviation (σ = 0.34), suggesting near-universal satisfaction with data export functionality. This finding underscores the importance of interoperability between educational technology tools and existing record-keeping systems; teachers highly valued the ability to transfer assessment data seamlessly into spreadsheet applications for further analysis, grade computation, or institutional reporting.
Item 10 (“I would recommend Iskorly to other teachers”) received the second-highest rating at x̅ = 4.83 (σ = 0.37), indicating strong adoption likelihood and positive word-of-mouth potential. Recommendation likelihood serves as a proxy for overall satisfaction and perceived value; the high rating suggests that teachers not only found Iskorly personally useful but also believed it would benefit colleagues, an essential factor in peer-driven diffusion of educational innovations. Item 6 (“Saving and accessing answer keys is convenient”) scored x̅ = 4.76 (σ = 0.43), validating the answer key management system’s effectiveness in supporting teachers’ organizational workflows.
Items reflecting operational efficiency, Items 3 and 4 (both x̅ = 4.60, σ = 0.62) addressed image capture straightforwardness and system responsiveness, respectively. While ratings remained high, these items exhibited slightly greater variability, suggesting that user experiences varied somewhat based on factors such as device performance characteristics, internet connectivity quality affecting OCR API response times, or individual differences in smartphone proficiency. Item 2 (“I can complete scoring tasks without confusion or frustration”) scored comparatively lower at x̅ = 4.56 (σ = 0.56), though still firmly in the “Strongly Agree” range. This slight reduction may reflect occasional OCR recognition errors requiring manual correction or initial learning curve challenges before users became fully proficient with the application’s workflow.
Items assessing pedagogical value and workflow impact, Item 8 (“The analytics provide useful insights for teaching”) scored x̅ = 4.70 (σ = 0.46), indicating that teachers found Mean Percentage Score (MPS), per-item difficulty statistics, and common wrong answer identification valuable for instructional planning. Item 9 (“Iskorly saves me significant time compared to manual checking”) scored x̅ = 4.73 (σ = 0.44), corroborating quantitative time-efficiency findings and validating Iskorly’s core value proposition of workload reduction. Item 5 (“Manual correction of OCR errors is simple and does not disrupt workflow”) scored x̅ = 4.67 (σ = 0.54), suggesting that the hybrid automation-correction workflow successfully balanced automation benefits with preservation of teacher oversight and judgment.
The overall UX mean of 4.70 demonstrates that Iskorly delivered a highly satisfactory user experience characterized by ease of learning, operational efficiency, workflow compatibility, and tangible pedagogical value. Standard deviations across UX items ranged from 0.34 to 0.62, indicating generally consistent user perceptions with slightly greater variability on items related to system performance and operational tasks potentially influenced by contextual factors beyond application design. These findings validate design priorities emphasizing rapid onboarding, intuitive workflows, robust error-correction mechanisms, convenient data management, and actionable analytics aligned with teachers’ instructional decision-making processes.


Alignment with Research Questions
The descriptive findings directly address the study’s two research questions concerning perceived UI and UX quality. Research Question 1 (RQ1) asked: What is the perceived quality of Iskorly’s User Interface as measured by a 10-item instrument? The data presented in Table 1 provide clear empirical evidence that teacher-users perceived Iskorly’s UI quality as exceptionally high, with an overall mean of 4.74 (Strongly Agree). 
All individual UI indicators received ratings of 4.60 or above, with particularly strong performance in button/menu labeling (4.86), text/icon legibility (4.83), and analytics dashboard clarity (4.76). These findings confirm that Iskorly’s interface design successfully achieved clarity, professional aesthetics, intuitive navigation, and functional effectiveness as perceived by its target user population.
Research Question 2 (RQ2) asked: What is the perceived quality of Iskorly’s User Experience as measured by a 10-item instrument? The data presented in Table 2 similarly demonstrate that teacher-users perceived Iskorly’s UX quality as exceptionally high, with an overall mean of 4.70 (Strongly Agree). Individual UX indicators ranged from 4.56 to 4.86, with particularly strong ratings for CSV export functionality (4.86), recommendation likelihood (4.83), answer key management (4.76), time savings (4.73), and analytics usefulness (4.70). These findings confirm that Iskorly delivered a user experience characterized by rapid learnability, operational efficiency, seamless workflow integration, and meaningful pedagogical value as perceived by classroom teachers.
The consistently high ratings across both UI and UX dimensions (overall means of 4.74 and 4.70 respectively) indicate that Iskorly successfully integrated technical functionality with user-centered design principles. The slightly higher UI mean compared to UX mean (difference of 0.04) suggests that while interface aesthetics and clarity were perceived as marginally stronger, the experiential dimensions involving task completion, workflow integration, and system performance were equally satisfactory. The relatively low standard deviations across most indicators (ranging from 0.34 to 0.66) demonstrate consistency in user perceptions, suggesting that Iskorly’s performance was reliable across diverse user contexts and that design decisions resonated broadly with the target teacher population rather than appealing only to a subset of technologically proficient early adopters.


Connection to Literature and Frameworks
The descriptive findings align meaningfully with established literature on educational technology adoption, teacher workload challenges, and mobile-assisted assessment. The high satisfaction with time savings (Item 9, x̅ = 4.73) and recommendation likelihood (Item 10, x̅ = 4.83) corroborates research by Langove and Khan (2024) demonstrating that automated grading systems reduce teacher workload by approximately 35%, freeing time for instructional planning and student support. The finding that teachers perceived Iskorly as saving significant time validates the core value proposition motivating this research: that automation of mechanical scoring tasks addresses a genuine, pressing need within Philippine educational contexts characterized by high student-teacher ratios, multiple subject preparations, and substantial non-teaching administrative burdens.
The strong rating for analytics usefulness (Item 8, x̅ = 4.70) aligns with research by Vattøy and Smith (2023) identifying that teachers most readily use assessment data when analytics are presented in simple, visually clear formats suggesting actionable instructional implications. Iskorly’s analytics dashboard, providing Mean Percentage Score by section, per-item difficulty statistics, and common wrong answer identification, was designed explicitly to offer simple, action-oriented insights rather than complex statistical reports. The high user satisfaction with these features validates this design philosophy and suggests that the analytics successfully bridged the gap between raw assessment data and pedagogically meaningful information supporting curricular adjustments and remedial planning.
The findings also connect to Diffusion of Innovations Theory (Rogers, 1962), which identifies five key attributes influencing technology adoption: relative advantage, compatibility, complexity, trialability, and observability. The high perceived time savings (relative advantage), ease of learning (low complexity via Item 1, x̅ = 4.66), workflow integration without disruption (compatibility via Item 5, x̅ = 4.67), and visible analytics outputs (observability via Item 8, x̅ = 4.70) collectively suggest that Iskorly exhibits the adoption-friendly attributes theorized to facilitate diffusion through social systems. The exceptionally high recommendation likelihood (x̅ = 4.83) particularly indicates potential for peer-to-peer diffusion, as satisfied early adopters become opinion leaders championing the innovation to colleagues, a critical pathway for spreading educational technologies in resource-constrained institutional contexts where formal top-down implementation may be limited.
The research also connects to Basic Information Processing Theory as applied to OCR systems. The favorable ratings for image capture straightforwardness (Item 3, x̅ = 4.60) and manual error correction simplicity (Item 5, x̅ = 4.67) validate the multi-stage processing framework: input acquisition (camera capture with adequate guidance), recognition (cloud-based OCR processing), post-processing (answer parsing and alignment), and output generation (structured score reports and analytics). The slightly lower though still high ratings for Items 3 and 4 (both x̅ = 4.60) and their higher standard deviations (σ = 0.62) suggest that image quality variability, influenced by lighting conditions, camera hardware, and user technique, remains a meaningful factor affecting recognition success. This variability underscores the theoretical insight that OCR accuracy depends not solely on algorithmic sophistication but critically on input quality and environmental conditions, factors partially beyond software control but addressable through user training and preprocessing options such as high-contrast enhancement.





Practical Implications
The findings yield several actionable implications for teachers, school administrators, and developers seeking to deploy or refine Iskorly in Philippine classroom contexts. First, the high satisfaction with CSV export functionality (x̅ = 4.86) and answer key management (x̅ = 4.76) suggests that teachers value data portability and organizational tools enabling integration with existing grade computation systems. Practical deployment should emphasize training on CSV export workflows, demonstrating how exported data can be imported into spreadsheet applications for gradebook updates, institutional reporting, or longitudinal performance tracking. Providing template spreadsheets pre-configured for common Philippine grading systems (e.g., transmutation formulas, weighted category calculations) would further enhance utility and reduce setup friction.
Second, the relatively lower though still high ratings for image capture (x̅ = 4.60) and system responsiveness (x̅ = 4.60), combined with their higher standard deviations (σ = 0.62), indicate that user experiences varied somewhat based on environmental conditions and device capabilities. Practical onboarding should provide explicit guidance on optimal image capture techniques: holding the device parallel to the answer sheet, ensuring adequate lighting preferably natural daylight or bright overhead fluorescent lamps, avoiding glare or shadows, and framing the answer area clearly within the camera preview. Quick-reference cards or brief tutorial videos demonstrating proper capture technique could reduce variability and improve first-time success rates. Additionally, highlighting the availability of two-column mode for multi-column answer sheets and high-contrast enhancement for low-quality images empowers users to proactively adjust settings when encountering recognition difficulties.
Third, the strong rating for analytics usefulness (x̅ = 4.70) suggests that teachers found Mean Percentage Score (MPS), item difficulty statistics, and common wrong answer reports valuable for instructional decision-making. Practical professional development should explicitly train teachers on interpreting these analytics and translating insights into curricular action. For instance, items with low percentage-correct rates (indicating high difficulty) may signal concepts requiring re-teaching, additional practice opportunities, or clarification of assessment item wording. Common wrong answers may reveal systematic misconceptions requiring targeted remediation. Training sessions could include case studies where teachers analyze sample analytics outputs and collaboratively develop instructional responses, modeling data-informed planning cycles.
Fourth, the exceptionally high recommendation likelihood (x̅ = 4.83) indicates potential for organic, peer-driven diffusion. School administrators should leverage early adopters as champions facilitating demonstrations, sharing successful use cases, and mentoring colleagues through initial adoption. Creating professional learning communities where teachers collaboratively explore Iskorly’s features, troubleshoot challenges, and share instructional applications of analytics could accelerate adoption while building institutional capacity for technology-enhanced assessment practices. Recognition or incentives for teachers piloting Iskorly, documenting their experiences, and supporting peers could further catalyze diffusion.


Limitations
Several limitations constrain the interpretation and generalizability of these findings. First, the sample was delimited by practical feasibility constraints and is not statistically representative of all teachers or schools in the Philippines. Participants were primarily recruited from Lyceum of Alabang and nearby schools in Metro Manila, potentially introducing geographic and institutional biases. Teachers in these urban or peri-urban schools may have greater smartphone familiarity, more reliable internet connectivity, and different workload patterns compared to teachers in rural provinces or under-resourced public schools. Consequently, findings cannot be generalized broadly across diverse Philippine educational contexts without further validation studies encompassing varied geographic regions, school types (public vs. private), and teacher experience levels.
Second, OCR accuracy and user satisfaction depend heavily on image quality, which is influenced by factors beyond the application’s control: classroom lighting conditions, camera hardware specifications, paper condition, and handwriting legibility. The study documented that image quality variability affected recognition success, as reflected in the slightly higher standard deviations for items related to image capture (σ = 0.62) and responsiveness (σ = 0.62). While Iskorly incorporates preprocessing options (high-contrast enhancement, two-column mode), extremely poor inputs (severely illegible handwriting, extreme shadows, heavily degraded paper) may still result in recognition errors requiring extensive manual correction, potentially diminishing efficiency gains. Future iterations might explore on-device OCR processing to eliminate internet dependency, though current mobile hardware constraints limit feasibility.
Third, student handwriting exhibits wide variability in style, legibility, and consistency, introducing a source of OCR error difficult to eliminate without human review. The application was designed with manual correction workflows precisely to address this limitation, preserving teacher oversight and judgment. However, answer sheets with extremely inconsistent or illegible handwriting may require correction of multiple items, reducing time savings. The study did not systematically categorize or quantify handwriting quality across answer sheets; future research could examine relationships between handwriting characteristics and recognition accuracy to identify thresholds where automation remains beneficial versus contexts where manual scoring is more efficient.
Fourth, the core OCR functionality requires internet connectivity to communicate with cloud-based services (Google Cloud Vision API and OCR.Space API). In areas with unreliable or absent internet access, real-time scoring capability is compromised. While the application queues requests for processing when connectivity is restored, this workaround does not fully address the needs of teachers in persistently offline environments. The study’s participant sample primarily included teachers with stable internet access, potentially overestimating usability and satisfaction in broader deployment contexts. Developers should consider prioritizing on-device OCR capabilities or hybrid approaches combining offline fallback algorithms with cloud-based processing when available.
Fifth, the evaluation period was limited to pilot testing and initial deployment rather than longitudinal impact assessment. Long-term effects on teaching practice, sustained adoption patterns, or impacts on student achievement remain unknown and cannot be addressed within the current study’s timeframe. Teacher satisfaction reported here reflects initial experiences during a novelty phase; sustained use over months or years may reveal latent usability challenges, feature requests, or shifts in perceived value. Future research should employ longitudinal designs tracking adoption trajectories, evolving usage patterns, and impacts on instructional practice and student outcomes across extended timeframes.
Sixth, device variability introduces performance inconsistencies. Android devices span wide ranges of hardware specifications, camera quality, processing power, and storage capacity. While Iskorly supports Android 7.0 and above, performance may vary on lower-spec devices compared to the high-end smartphones used during testing; future research could investigate how hardware specifications affect user satisfaction, recognition accuracy, and system responsiveness to establish minimum performance standards. 














































CHAPTER V

 SUMMARY OF FINDINGS, CONCLUSIONS, & RECOMMENDATIONS


This chapter presents a concise summary of the study’s key findings, draws evidence-based conclusions addressing the research questions, and offers targeted recommendations for practice, development, and future research informed by the descriptive statistical analysis of Iskorly’s User Interface and User Experience quality.

Summary of Findings
This study evaluated the perceived quality of Iskorly, an Android-based automated answer sheet scoring application utilizing Optical Character Recognition, across two key dimensions: User Interface (UI) and User Experience (UX). Findings were derived from descriptive statistical analysis of responses to two 10-item Likert-scale instruments administered to teacher-users following pilot implementation. 
User Interface quality was assessed through ten indicators examining visual design, text/icon legibility, layout clarity, button/menu labeling, camera guidance, visual feedback mechanisms, analytics dashboard presentation, navigational intuitiveness, screen compatibility, and overall visual appeal. All UI indicators received mean scores ranging from 4.60 to 4.86 on a 5-point scale, with an overall UI mean of 4.74, consistently interpreted as “Strongly Agree.” The highest-rated indicator was button and menu labeling clarity (x̅ = 4.86), followed closely by text and icon legibility (x̅ = 4.83). Comparatively lower though still high ratings were observed for camera preview guidance (x̅ = 4.60) and visual error feedback (x̅ = 4.66), both exhibiting slightly greater variability (σ = 0.62 and 0.66 respectively). Analytics dashboard clarity (x̅ = 4.76), navigation intuitiveness (x̅ = 4.73), screen compatibility (x̅ = 4.76), and overall visual appeal (x̅ = 4.76) all received consistently high ratings with low standard deviations, indicating consensus among users.
User Experience quality was assessed through ten indicators examining learnability, task completion ease, image capture efficiency, system responsiveness, error correction simplicity, answer key management convenience, CSV export functionality, analytics usefulness, time savings, and recommendation likelihood. All UX indicators received mean scores ranging from 4.56 to 4.86, with an overall UX mean of 4.70, consistently interpreted as “Strongly Agree.” The highest-rated indicator was CSV export functionality (x̅ = 4.86, σ = 0.34), demonstrating near-universal satisfaction with data portability. Recommendation likelihood scored second-highest (x̅ = 4.83), indicating strong adoption potential and word-of-mouth diffusion likelihood. Answer key management (x̅ = 4.76), time savings (x̅ = 4.73), and analytics usefulness (x̅ = 4.70) all received strong ratings validating Iskorly’s pedagogical value. Comparatively lower though still high ratings were observed for task completion ease (x̅ = 4.56), image capture straightforwardness (x̅ = 4.60), and system responsiveness (x̅ = 4.60), with slightly higher standard deviations suggesting modest variability in operational experiences potentially influenced by device capabilities, connectivity quality, or individual proficiency differences.
Overall, the findings demonstrate consistently high user satisfaction across both UI and UX dimensions, with all 20 indicators receiving mean scores above 4.5 and overall means of 4.74 (UI) and 4.70 (UX). The slight 0.04-point difference between UI and UX means suggests marginally stronger perceptions of interface aesthetics relative to operational experience, though both domains received equivalently positive ratings. Standard deviations across indicators ranged from 0.34 to 0.66, indicating generally consistent perceptions with modest variability on items potentially influenced by contextual factors such as lighting conditions, device specifications, or connectivity quality.


Conclusions
Based on the descriptive statistical findings, the study draws the following evidence-based conclusions addressing the two research questions:


Research Question 1: What is the perceived quality of Iskorly’s User Interface (UI) as measured by a 10-item instrument (Mean, SD, VI)? The data conclusively demonstrate that teacher-users perceived Iskorly’s User Interface quality as exceptionally high, with an overall mean rating of 4.74 interpreted as “Strongly Agree.” All individual UI indicators received ratings of 4.60 or above, with particularly strong performance in button/menu labeling, text/icon legibility, analytics dashboard clarity, and navigational intuitiveness. The low standard deviations across most indicators (0.37–0.54 for eight of ten items) indicate consistency in user perceptions and reliability of interface performance across diverse contexts. These findings support the conclusion that Iskorly successfully implemented user-centered design principles emphasizing visual clarity, professional aesthetics, intuitive navigation hierarchies, and contextually appropriate feedback mechanisms. The interface design effectively balanced functional requirements with usability considerations, producing a professional-grade mobile application suitable for classroom deployment.


Research Question 2: What is the perceived quality of Iskorly’s User Experience (UX) as measured by a 10-item instrument (Mean, SD, VI)? The data conclusively demonstrate that teacher-users perceived Iskorly’s User Experience quality as exceptionally high, with an overall mean rating of 4.70 interpreted as “Strongly Agree.” Individual UX indicators ranged from 4.56 to 4.86, with particularly strong ratings for CSV export functionality, recommendation likelihood, answer key management, time savings, and analytics usefulness. These findings support the conclusion that Iskorly successfully delivered a user experience characterized by rapid learnability, operational efficiency, seamless workflow integration, and tangible pedagogical value. The application effectively addressed the core challenge motivating its development: reducing teacher workload through automated scoring while preserving teacher oversight via manual correction workflows and providing actionable analytics supporting data-informed instructional decisions. The exceptionally high recommendation likelihood (x̅ = 4.83) particularly indicates that teachers perceived sufficient value to champion the innovation to colleagues, suggesting favorable conditions for peer-driven diffusion within school contexts.
Integrating both findings, the study concludes that Iskorly represents a viable, user-accepted technological solution to manual assessment scoring challenges in Philippine classroom contexts. The consistently high ratings across both UI and UX dimensions, combined with strong perceived time savings and analytics utility, validate the application’s technical effectiveness and practical value. The findings align with theoretical frameworks emphasizing adoption-friendly attributes (relative advantage, compatibility, simplicity, observability) and empirical research documenting workload reduction benefits of automated grading systems. Iskorly demonstrates that mobile-first, cloud-assisted OCR technology can be successfully adapted to educational assessment contexts when designed with explicit attention to user needs, contextual constraints, and pedagogical requirements rather than prioritizing technical sophistication alone.




Recommendations
Based on the study’s findings and conclusions, the following recommendations are offered for practitioners, developers, and researchers:
For Practice. Teachers implementing Iskorly should prioritize structured onboarding emphasizing optimal image capture techniques: holding devices parallel to answer sheets, ensuring adequate lighting (natural daylight or bright overhead illumination), avoiding glare/shadows, and framing answer areas clearly within camera previews. Quick-reference guides or brief tutorial videos demonstrating proper capture technique should be provided to reduce first-time recognition errors and build user confidence. Teachers should be explicitly trained on utilizing two-column mode for multi-column answer layouts and high-contrast enhancement for low-quality images, empowering proactive adjustment when encountering recognition difficulties.
Teachers should leverage Iskorly’s CSV export functionality (rated x̅ = 4.86) to integrate assessment data seamlessly with existing gradebook systems, institutional reporting requirements, and longitudinal performance tracking. Professional development should provide template spreadsheets pre-configured for common Philippine grading systems (transmutation formulas, weighted categories), reducing setup friction and demonstrating practical integration workflows.
Teachers should actively use analytics features (MPS by section, per-item difficulty statistics, common wrong answer identification) to inform instructional planning. Items with low percentage-correct rates may indicate concepts requiring re-teaching; common wrong answers may reveal systematic misconceptions requiring targeted remediation. Professional learning communities should be established where teachers collaboratively analyze analytics outputs and develop instructional responses, modeling data-informed planning cycles and building institutional capacity for evidence-based teaching.
School administrators should leverage early adopters exhibiting high satisfaction (particularly those rating recommendation likelihood at 5.0) as peer champions facilitating demonstrations, sharing successful use cases, and mentoring colleagues through initial adoption. Creating professional learning communities where teachers troubleshoot challenges collectively and share instructional applications of analytics could accelerate diffusion while building sustainable support networks. Recognition or incentives for teachers piloting Iskorly, documenting experiences, and supporting peers could catalyze broader institutional adoption.
For Development. Developers should prioritize refinements addressing operational items exhibiting comparatively higher variability: image capture guidance (x̅ = 4.60, σ = 0.62) and system responsiveness (x̅ = 4.60, σ = 0.62). Enhanced real-time camera feedback (e.g., visual overlays indicating adequate lighting, proper framing, focus confirmation) could reduce capture uncertainty. Optimization of OCR API calls, implementing request batching, caching frequently used answer keys, and minimizing network latency could improve perceived responsiveness, particularly on lower-specification devices or in intermittent-connectivity environments.
Future development iterations should explore on-device OCR processing to eliminate internet dependency, expanding deployment feasibility to persistently offline contexts. While current mobile hardware constraints limit on-device OCR accuracy and speed, emerging TensorFlow Lite models and optimized neural networks may enable acceptable offline performance, particularly for printed text recognition. Hybrid approaches combining offline fallback algorithms with cloud-based processing when connectivity is available could balance accuracy, speed, and accessibility.
Developers should investigate adaptive parsing algorithms learning from manual corrections to progressively improve recognition accuracy for individual users’ specific handwriting characteristics or answer sheet formats. While current deterministic parsing provides consistent, predictable behavior, incorporating lightweight machine learning models that adapt to user-specific patterns could reduce manual correction burden over time. Privacy-preserving on-device learning approaches should be prioritized to avoid transmitting sensitive student data externally.
Analytics enhancements should provide temporal tracking capabilities, enabling teachers to visualize performance trends across multiple assessments administered over time. Longitudinal MPS trends by section, item difficulty evolution, and recurring misconception patterns could inform broader curricular decisions beyond single-assessment remediation. Integration with learning management systems (LMS) used by Philippine schools could further streamline data flow and reduce redundant data entry.
For Future Research. Future studies should employ broader, more representative samples encompassing diverse geographic regions (urban/rural, Luzon/Visayas/Mindanao), school types (public/private, large/small), and teacher experience levels to enhance generalizability of findings across Philippine educational contexts. Stratified sampling designs ensuring adequate representation of under-resourced schools, rural contexts, and novice teachers would provide more comprehensive evidence regarding Iskorly’s effectiveness and adoption feasibility in varied institutional environments.
Longitudinal research designs tracking adoption trajectories, sustained usage patterns, and long-term impacts on teaching practice and student achievement should be pursued. Extending evaluation timeframes beyond initial pilot testing (3–6 months) to academic year or multi-year horizons would reveal whether initial satisfaction translates into sustained adoption, how usage patterns evolve as teachers gain proficiency, whether perceived time savings and analytics utility persist beyond novelty phases, and ultimately whether Iskorly adoption correlates with measurable improvements in student learning outcomes.
Comparative descriptive studies examining classroom usage scenarios could identify optimal deployment contexts. For instance, comparing Iskorly adoption and satisfaction across different class sizes (small <20 students vs. large 40+ students), assessment frequencies (weekly formative vs. monthly summative), and subject areas (mathematics vs. language arts vs. sciences) could reveal where automation delivers maximum relative advantage and where manual scoring remains preferable. Such nuanced understanding would enable more targeted recommendations and resource allocation.
Research examining relationships between device characteristics and user satisfaction could inform minimum recommended hardware specifications. Systematically controlling for or documenting smartphone models, camera megapixel ratings, processing speeds, RAM capacity, and Android version during evaluation would enable correlation analyses identifying performance thresholds ensuring acceptable user experiences. Such evidence could guide procurement recommendations and deployment planning.
Future research should explore Iskorly’s applicability to specialized content beyond standard alphanumeric responses. Investigating OCR recognition accuracy for mathematical notation, chemical formulas, diagrams, or non-Roman scripts (e.g., Baybayin) would expand potential applications. Similarly, research adapting Iskorly principles to subjective assessment types requiring rubrics (essay scoring, creative writing evaluation) could extend automated assistance beyond objective-type assessments, though such adaptations would require fundamentally different natural language processing and machine learning approaches beyond current OCR capabilities.


Finally, research examining adoption factors beyond technical performance, specifically institutional policies, leadership support structures, professional development models, and peer influence networks, would illuminate socio-organizational dimensions affecting technology diffusion. Understanding how school culture, administrative priorities, teacher collaboration patterns, and resource allocation decisions shape adoption could inform implementation strategies maximizing uptake and sustained use. Mixed-methods approaches combining quantitative adoption metrics with qualitative case studies documenting implementation experiences would provide rich, contextualized understanding supporting evidence-based deployment planning.


In conclusion, this study provides strong empirical evidence that Iskorly successfully addresses manual assessment scoring challenges through user-centered design integrating OCR technology with intuitive workflows, actionable analytics, and teacher oversight mechanisms. The consistently high UI and UX ratings validate both technical effectiveness and practical usability in Philippine classroom contexts. By demonstrating how mobile-first, cloud-assisted solutions can tackle persistent educational challenges when designed with contextual awareness and pedagogical purpose, this research contributes to broader discourse on appropriate technology for resource-constrained educational settings and offers a replicable model for student-led innovation addressing real-world problems through interdisciplinary collaboration.




References
Adeyinka, T., Oladipo, O., & Ibikunle, A. (2023). Mobile-assisted learning in sub-Saharan Africa: Opportunities and challenges. African Journal of Educational Technology, 14(2), 45–62.


Chen, L., Wang, S., Liu, Y., & Torres, R. (2023). Teacher perceptions of automated grading systems: A comparative study across Asian secondary schools. Computers & Education, 195, 104–121. https://doi.org/10.1016/j.compedu.2023.104721


Handal, B., Campbell, C., Cavanagh, M., Petocz, P., & Kelly, N. (2020). Barriers and enablers to digital technology adoption in resource-limited schools. Journal of Educational Technology & Society, 23(2), 189–207.


Kim, D., Park, Y., Choi, W., & Lee, S. (2023). Trajectories of educational technology adoption: Initial adoption versus sustained use. Educational Technology Research and Development, 71(3), 891–915. https://doi.org/10.1007/s11423-023-10218-z


Langove, S. A., & Khan, A. (2024). Automated grading and feedback systems: Reducing teacher workload and improving student performance. Journal of Asian Development Studies, 13(4), 202–212.


Nicol, D. (2021). The power of internal feedback: Exploiting natural comparison processes. Assessment & Evaluation in Higher Education, 46(2), 185–206. https://doi.org/10.1080/02602938.2020.1730765
Rodriguez, M. C., Hoffman, J., & Katz, I. R. (2021). Automated scoring of constructed responses: A systematic review and recommendations for best practices. Educational Measurement: Issues and Practice, 40(2), 74–87. https://doi.org/10.1111/emip.12415


Saavedra, S., & Uribe, L. (2022). Cloud-assisted optical character recognition on resource-constrained hardware. IEEE Access, 10, 12,456–12,468. https://doi.org/10.1109/ACCESS.2022.3145802


Schneider, M., & Preckel, F. (2017). Variables associated with achievement in higher education: A systematic review of meta-analyses. Psychological Bulletin, 143(6), 565–600. https://doi.org/10.1037/bul0000098


Shrivastava, N., Singh, P., & Kumar, R. (2023). Optical character recognition: A comparative study of engines and preprocessing techniques. Journal of Information Technology Research, 16(3), 1–28. https://doi.org/10.4018/jitr.318462


Statista. (2024). Mobile operating systems market share Philippines 2024. Retrieved from https://www.statista.com/chart/4839/smartphone-operating-systems-market-share-in-philippines/


Tarhini, A., Arachchilage, N. A., & Masa'deh, R. (2023). A critical review of theories and models of technology adoption: A systematic literature review. International Journal of Information Management Data Insights, 3(1), 100–118. https://doi.org/10.1016/j.ijimdi.2022.100118
Thammarak, W., Suwannapong, C., Santitissadeekul, S., & Kaewyai, P. (2022). Automated digitization of Thai vehicle registration certificates using Google Cloud Vision API. Journal of Information Technology Education: Research, 21, 89–106. https://doi.org/10.28945/4942


Traxler, J. (2023). Mobile learning: A global perspective. Learning, Media and Technology, 48(1), 14–29. https://doi.org/10.1080/17439884.2023.2158923


Vattøy, K. D., & Smith, K. (2023). Teacher use of formative assessment data: Patterns, practices, and perceived barriers. Teaching and Teacher Education, 119, 103–121. https://doi.org/10.1016/j.tate.2022.103821


Wiliam, D., & Leahy, S. (2015). Embedding formative assessment: Practical techniques for K-12 classrooms (2nd ed.). Learning Sciences International.