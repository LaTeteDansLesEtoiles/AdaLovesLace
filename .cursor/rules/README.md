# Development Rules

This directory contains all development rules for the AdaLovesLace project.

## Rules Files

Rules are numbered and versioned (v000, v010, v020, etc.) to ensure proper ordering and versioning:

- [000_code_style.md](000_code_style.md) (v000) - Size limits for packages, methods, and files
- [010_honesty.md](010_honesty.md) (v010) - Brutal honesty requirement in all communications
- [020_rules.md](020_rules.md) (v020) - Organization and structure of rules files
- [030_language.md](030_language.md) (v030) - English language requirement for all program content
- [040_tdd.md](040_tdd.md) (v040) - TDD with unit, integration, and Selenide tests
- [050_enforcement.md](050_enforcement.md) (v050) - Mechanisms to ensure rules compliance

## Automated Enforcement

The following tools are configured in `pom.xml` to automatically enforce these rules:

- **PMD**: Code quality and size limits (Rule 000)
- **SpotBugs**: Bug detection and security issues
- **JaCoCo**: Test coverage monitoring (Rule 040)
- **OWASP Dependency-Check**: Security vulnerability scanning
- **SonarLint**: IDE-level code quality checks

See [050_enforcement.md](050_enforcement.md) for detailed configuration information.
