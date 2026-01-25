# Security Policy

## Reporting a Vulnerability

The Iskorly team takes security issues seriously. We appreciate your efforts to responsibly disclose your findings.

### How to Report

If you discover a security vulnerability in Iskorly, please report it **privately** by one of these methods:

1. **GitHub Security Advisory** (Preferred): Use GitHub's private security advisory feature at `https://github.com/traumereixd/Iskorly/security/advisories`
2. **Email**: Contact the development team directly (check repository for contact information)

**Note**: Please do NOT create public issues for security vulnerabilities until they have been addressed.

### What to Include

When reporting a vulnerability, please include:

- A clear description of the vulnerability
- Steps to reproduce the issue
- Potential impact and severity
- Any suggested fixes or mitigations (optional)
- Your contact information for follow-up

### Response Timeline

- **Initial Response**: We aim to acknowledge receipt within 48 hours
- **Status Update**: We will provide a status update within 7 days
- **Fix Timeline**: Security fixes will be prioritized based on severity

### Disclosure Policy

- Please do not publicly disclose the vulnerability until we have addressed it
- We will coordinate with you on the disclosure timeline
- We will credit you in the security advisory (unless you prefer to remain anonymous)

## Security Considerations for Iskorly

### API Keys

- **Never commit API keys** to the repository
- Store API keys in `local.properties` (automatically gitignored)
- Required: `GCLOUD_VISION_API_KEY`
- Optional: `OCR_SPACE_API_KEY`

### Data Privacy

- All student data is stored **locally on device** only
- No data is transmitted to external servers except:
  - Images sent to OCR APIs (Google Vision, OCR.Space) for text extraction
  - No personally identifiable information is sent with images
- CSV exports contain student data - handle with care
- No analytics or tracking data is collected by the app

### Permissions

Iskorly requires the following Android permissions:

- **Camera**: To capture answer sheet photos
- **Storage**: To read/write images and export CSV files
- All permissions are requested at runtime with clear explanations

### Third-Party Dependencies

We regularly update dependencies to address known vulnerabilities. Current OCR providers:

- Google Cloud Vision API
- OCR.Space API (fallback)
- OpenRouter API (for reparse endpoint, optional)

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.3.x   | :white_check_mark: |
| 1.2.x   | :white_check_mark: |
| < 1.2   | :x:                |

## Security Best Practices

For users and administrators:

1. **Keep the app updated** to the latest version
2. **Protect your API keys** - never share them publicly
3. **Review CSV exports** before sharing to ensure no sensitive data is exposed
4. **Use device encryption** to protect locally stored student data
5. **Regular backups** of important data via CSV export

---

For questions or concerns about security, please open an issue on GitHub or contact the development team.
