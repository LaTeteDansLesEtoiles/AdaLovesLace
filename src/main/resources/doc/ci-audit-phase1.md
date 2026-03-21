# Phase 1 — Repository-grounded CI audit (pre-hardening baseline)

This document captures the state **before** the product-oriented pipeline redesign. It is intentionally specific to AdaLovesLace.

## A. What was already good

- **Explicit Java 24 + JavaFX** assumptions in Jenkins (`JAVA_HOME`, Temurin path).
- **Test taxonomy** by package (`unittest`, `integrationtest`, `functionaltest`) and JUnit 5 tags (`unit`, `integration`, `functional`).
- **Maven profiles** to isolate unit, integration, and functional runs via `skipUTs` / `skipITs` / `skipFTs`.
- **Failsafe** configured for JavaFX (`--add-opens`, `useSystemClassLoader`, JUnit parallelism disabled) — appropriate for TestFX.
- **Xvfb** wrapping for functional tests — correct pattern for headed JavaFX on Linux agents.
- **OWASP dependency-check** moved to Maven (no Jenkins-only `dependencyCheck` step); **OSS Index** disabled in POM with documented reason (401 without credentials).
- **PMD 7** + quickstart + `NcssCount` with explicit `failurePriority=1` philosophy documented in rules.
- **Protobuf** `dependencyManagement` alignment to avoid gencode/runtime skew.
- **JaCoCo** thresholds (60% line at package and class level) defined in the POM.
- **JavaPackager** Linux profile with **AppImage disabled** for a documented upstream tooling reason (404 on `appimagetool` download URL).

## B. What was structurally weak, duplicated, or fragile

- **Scripted Jenkinsfile** with a non-idiomatic `environment { }` block inside `node { }` (declarative-only pattern), making behavior dependent on Jenkins interpretation.
- **Failsafe bound to `test` phase** with both `integration-test` and `verify` goals on each execution — non-standard versus Maven conventions (`integration-test` then `verify`).
- **Jenkins never ran a coherent `mvn verify`** aligned with POM bindings: quality tools were invoked ad hoc (`pmd:cpd`, `pmd:check`, `spotbugs:spotbugs`) while **SpotBugs `check`**, **JaCoCo `check`**, and **verify-bound PMD/CPD** could diverge from what developers get from a plain lifecycle run.
- **`spotbugs:spotbugs` in Jenkins** produced XML for Warnings NG but did not necessarily match **`failOnError`** semantics from the POM’s verify execution (`check`).
- **Repeated `clean`** (`clean` stage plus `clean` inside unit test stage) — extra churn without a clear product-level benefit.
- **Functional stage used `mvn ... test`** while integration used `mvn ... test` — both relied on the non-standard Failsafe phase binding instead of lifecycle clarity.
- **Low-signal Warnings NG parsers** (`java` on compiled bytecode paths, `mavenConsole` without an explicit log file) added noise versus actionable findings.
- **Packaging stage** archived artifacts without structured, scriptable **product smoke** (existence, size, integrity metadata).
- **Shared `failsafe.groups` property** applied to both Failsafe executions — incorrect for a hypothetical “run all ITs in one go” profile because integration and functional executions need **different** tag filters.

## C. Code quality vs product quality (distinction)

**Code / source quality (partially validated before hardening)**

- Compiler, unit tests, integration tests, functional tests (when run).
- PMD / CPD / SpotBugs signal (partially via CLI goals, partially only on `verify`).
- JaCoCo coverage (report generation in Jenkins; **check** enforcement tied to `verify`, not consistently exercised by Jenkins).

**Product quality (weak before hardening)**

- Linux `.deb` / `.rpm` presence and sanity were not checked by a dedicated script.
- No **checksum file** tying release artifacts to the build.
- No structured extension point for install/startup smoke or visual/performance validation beyond functional tests.

## D. What was missing for product-grade CI

- **Explicit pipeline modes** (PR vs broader scheduled vs release) with documented blocking scope.
- **Single coherent `verify`** after tests so Maven remains the source of truth for plugin ordering.
- **Failsafe phase normalization** without breaking the ability to run integration and functional ITs in **one** Maven invocation (to avoid clobbering Failsafe reports).
- **Dedicated `scripts/ci/`** for packaging smoke and release checksums.
- **Scaffolding** for visual regression and performance sanity with honest “not implemented” markers.
- **Freshness reports** (dependencies/plugins) decoupled from PR speed, for nightly observability.

## E. Ordered work (impact vs risk)

1. **Normalize Failsafe phases + fix per-execution tag groups + add `ci-all-it`** — high impact, medium risk; mitigated by keeping profile semantics and Xvfb.
2. **Declarative Jenkinsfile + `PIPELINE_MODE`** — high clarity, medium risk; mitigated by parameters and documentation.
3. **Single `verify` quality gate + `-Ddependency-check.skip=true` + dedicated OWASP stage** — removes duplicate dependency-check, aligns static gates with POM.
4. **Packaging smoke + SHA256SUMS** — high product signal, low risk.
5. **Nightly-only freshness + scaffolds** — low risk, informational.
6. **Tighten SpotBugs / JaCoCo on PR** — deferred until backlog addressed (explicit compromise).

## F. What was explicitly not changed yet

- **No SpotBugs mass-fix** or silent suppression campaign.
- **No AppImage re-enable** until JavaPackager upstream URL is fixed or a supported workaround exists.
- **No real visual regression baselines** or pixel suites.
- **No signing** of release artifacts.
- **No change** to JaCoCo 60% thresholds (only **which pipeline modes** enforce `jacoco-check`).
- **iconFile** path `/opt/adaloveslace/adaloveslace.png` in JavaPackager — agent-specific; not reworked in this pass.

## Trade-off (strictest quality vs maintainability)

- **PR** mode intentionally skips integration/functional tests and **JaCoCo `check`** to keep merge feedback fast; **nightly/release** retain full IT/FT and `check`. This is stricter about **honesty** (no misleading coverage gate on partial runs) than about **maximal per-PR enforcement**.
