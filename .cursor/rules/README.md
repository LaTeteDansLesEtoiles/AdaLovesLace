# Development Rules

This directory contains all development rules for the AdaLovesLace project.

## Purpose

This project is a JavaFX desktop application (not a backend) that:

- manipulates `.lace` files (ZIP archives)
- uses Protocol Buffers as the primary persistence format
- supports legacy XML (`save.xml`) as input-only compatibility
- must run efficiently on standard desktop hardware
- must remain testable in Linux headless CI (TestFX + JUnit 5)

These rules exist to:
- prevent architectural drift
- avoid performance regressions
- keep UI tests stable and non-flaky
- enforce clean boundaries between layers
- maintain long-term maintainability

---

## Rules Files

Rules are numbered and versioned (v000, v010, v020, etc.) to ensure proper ordering and versioning:

- [000_constitution.mdc](000_constitution.mdc)      (v000)  - Core constitution for rule arbitration and code quality in a JavaFX desktop application using .lace archives and protobuf
- [005_code_style.mdc](005_code_style.mdc)          (v005)  - Size limits for packages, methods, and files
- [010_honesty.mdc](010_honesty.mdc)                (v010)  - Brutal honesty requirement in all communications
- [020_rules.mdc](020_rules.mdc)                    (v020)  - Organization and structure of rules files
- [030_language.mdc](030_language.mdc)              (v030)  - English language requirement for all program content
- [040_tdd.mdc](040_tdd.mdc)                        (v040)  - TDD with unit, integration, and functional tests
- [050_test_javafx.mdc](050_test_javafx.mdc)        (v050)  - General best practices for JavaFX functional tests
- [060_lace_file_workflows_testfx.mdc](060_lace_file_workflows_testfx.mdc)   (v060)  - Rules for UI workflows involving `.lace` files
- [070_lace_persistence_unit_tests.mdc](070_lace_persistence_unit_tests.mdc) (v070)  - Rules for non-UI persistence tests (critical layer)
- [080_javafx_architecture.mdc](080_javafx_architecture.mdc)                 (v080)  - Defines the layered architecture
- [090_lace_serialization.mdc](090_lace_serialization.mdc)                   (v090)  - Defines `.lace` format and serialization rules
- [100_lace_performance.mdc](100_lace_performance.mdc)                       (v100)  - Protects performance on large files (100–300 MB)
- [110_domain_boundaries.mdc](110_domain_boundaries.mdc)                     (v110)  - Protects the domain model
- [120_integration_tests.mdc](120_integration_tests.mdc)                     (v120)  - Enforce robust integration testing practices for a JavaFX desktop application handling .lace archives, protobuf persistence, legacy XML compatibility, and local file infrastructure 
- [130_code_review.mdc](130_code_review.mdc)        (v130)  - Enforce brutally honest code review standards for a JavaFX desktop application using .lace archives, protobuf persistence, legacy XML compatibility, and TestFX on Linux headless CI
- [140_test_structure_and_scope.mdc](140_test_structure_and_scope.mdc)       (v140)  - Enforce strict separation of unit, integration, and functional tests in a JavaFX desktop application using Maven and JUnit 5
- [150_rule_conflict_resolution.mdc](150_rule_conflict_resolution.mdc)       (v150)  - Resolve conflicts between Cursor rules in a deterministic and brutally honest way for a JavaFX desktop application 
- [160_auto_refactor_trigger.mdc](160_auto_refactor_trigger.mdc)             (v160)  - Force refactoring before feature work when structural debt, boundary violations, flaky tests, or hot-path waste would make new code harmful
- [170_enforcement.mdc](170_enforcement.mdc)        (v170)  - Mechanisms to ensure rules compliance

## Automated Enforcement

The following tools are configured in `pom.xml` to automatically enforce these rules:

- **PMD**: Code quality and size limits (Rule 000)
- **SpotBugs**: Bug detection and security issues
- **JaCoCo**: Test coverage monitoring (Rule 040)
- **OWASP Dependency-Check**: Security vulnerability scanning
- **SonarLint**: IDE-level code quality checks

See [130_enforcement.mdc](130_enforcement.mdc) for detailed configuration information.


## How to use these rules

- Cursor applies them automatically — no manual action needed
- When modifying code:
    - improve the area toward the rules
    - do not copy existing bad patterns
- When adding new features:
    - respect boundaries (UI ≠ persistence ≠ domain)
    - write tests at the right level
    - keep `.lace` contract explicit

---

## Key principles (non-negotiable)

- `.lace` = ZIP archive
- `descriptor.pb` = primary format
- `save.xml` = legacy input only
- no `Thread.sleep` in UI tests
- no Protobuf classes in domain
- no persistence logic in controllers
- no silent fallback on corrupted data
- tests must verify meaning, not just existence

---

## If you add new rules

- keep them focused
- write them in English without any emojis
- avoid redundancy with existing rules
- document them here

---

## Final note

These rules are here to prevent slow decay of the codebase.

They are especially important because:
- the app handles large files
- UI + persistence + serialization interact heavily
- bad patterns become expensive very quickly

If in doubt: prefer clarity, boundaries, and explicit behavior over convenience