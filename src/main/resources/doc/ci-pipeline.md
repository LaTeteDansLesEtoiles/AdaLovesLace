# CI pipeline (Jenkins + Maven)

This document describes how continuous integration is structured for AdaLovesLace after the product-oriented pipeline hardening. It complements `src/main/resources/doc/MIGRATION_CI_PIPELINE.md` and the Phase 2 target design in `src/main/resources/doc/ci-target-phase2.md`.

## Philosophy

- CI exists to reduce the risk of shipping regressions and to increase confidence in **delivered artifacts**, not to maximize the number of executed tools.
- Source-level checks (compile, static analysis) matter; **product-level checks** (installable artifacts, integrity metadata) matter for release credibility.
- Legacy debt may exist. The near-term rule is **do not make it worse**: new changes must meet current gates; widening gates requires an explicit decision and documentation.

## Pipeline modes

The Jenkins job exposes `PIPELINE_MODE`:

| Mode | Purpose | Blocking scope |
|------|---------|----------------|
| `pr` | Fast feedback on merge requests | Compile, unit tests, `mvn verify -P ci-pr` (PMD/CPD + SpotBugs XML with `failOnError=false`), **no** IT/FT, **no** JaCoCo check |
| `nightly` | Deeper validation | PR bar plus **one** `mvn integration-test -P ci-all-it` under Xvfb, `mvn verify` with JaCoCo check + SpotBugs `failOnError=true`, OWASP, freshness text, scaffolds |
| `release` | Deliverable traceability | Nightly-equivalent test and analysis bar, Linux packaging (`-P linux`), `scripts/ci/verify-packaged-artifacts.sh`, `release-checksums.sh` |

Non-blocking items are still archived (reports, logs) where practical so trends remain visible in Warnings NG or artifacts.

## Quality layers

1. **Source / build confidence**: Java 24 compile, protobuf generation, dependency resolution.
2. **Test confidence**: Surefire (unit), Failsafe (integration + functional), JUnit reports published separately for Surefire vs Failsafe.
3. **Product confidence**: Packaged `.deb` / `.rpm` exist, non-empty, type-sane; checksum file for release; extension points for install/startup smoke (see `scripts/ci/`).
4. **Maintainability / security confidence**: PMD + CPD, SpotBugs, OWASP dependency-check (NVD; OSS Index disabled in POM due to unauthenticated 401), JaCoCo thresholds on full runs.

## Maven responsibilities vs Jenkins responsibilities

- **Maven** owns lifecycle ordering, plugin configuration, profiles (`unit-tests`, `integration-tests`, `functional-tests`, `linux`, `windows`, `ci-pr`, `ci-nightly`, `ci-release`, `visual-regression-tests`, `performance-tests`), and reproducible local commands.
- **Jenkins** owns pipeline mode selection, Xvfb wrapping for functional tests, credential injection for `NVD_API_KEY`, artifact and report archival, and Warnings NG `recordIssues` / `publishIssues` where used.

Avoid running the same expensive Maven goal twice in one build unless the second run is clearly incremental (for example OWASP skipped on `verify` via `-Ddependency-check.skip=true` after a dedicated OWASP stage).

## Test layout and profiles

- **Unit**: `src/test/java/**/unittest/**`, JUnit 5 tag `unit` when groups are active; profile `unit-tests` sets `skipITs`/`skipFTs`.
- **Integration**: `**/integrationtest/**`, tag `integration`; profile `integration-tests`.
- **Functional**: `**/functionaltest/**`, tag `functional`; profile `functional-tests`; TestFX requires a display (Xvfb on Linux CI).

Local examples:

```text
./mvnw clean test -P unit-tests
./mvnw clean integration-test -P integration-tests
./mvnw clean integration-test -P functional-tests
./mvnw clean integration-test -P ci-all-it
./mvnw clean verify -P ci-nightly
./mvnw clean verify -P ci-pr -DskipTests -DskipITs -DskipFTs
./mvnw clean package -P linux -DskipTests -DskipITs -DskipFTs -Ddependency-check.skip=true
```

(Adjust skips for release if you already ran the full test pyramid in the same workspace.)

## Static analysis

- **PMD**: `pmd-rules.xml` — quickstart + `NcssCount`; `failurePriority=1` (only priority 1 fails the build; other severities are warnings until backlog work).
- **CPD**: bound to `verify` via `cpd-check`.
- **SpotBugs**: `spotbugs-include.xml` / `spotbugs-exclude.xml`; `verify` runs `check` with `failOnError=true`. Jenkins historically used `spotbugs:spotbugs` for XML without failing; the hardened pipeline keeps **report** publication compatible with Warnings NG while documenting that **fail-on-bugs** for SpotBugs is gated until the known violation backlog is addressed (see migration note).

## Coverage

- JaCoCo agent is attached for test runs; **check** enforces 60% line coverage per package and class on runs that execute the `jacoco-check` execution (typically full `verify`).
- PR mode may skip the JaCoCo **check** execution while still producing **reports** so coverage is visible without blocking merges on incomplete partial runs (explicit trade-off; see migration note).

## OWASP dependency-check

- Configured in `pom.xml` with `failBuildOnCVSS` 7, NVD API key via `nvd.api.key` / `NVD_API_KEY`, OSS Index analyzer disabled (Sonatype 401 without credentials).
- Runs in **nightly** and **release** (and optional PR if you enable it); reports archived under `target/dependency-check-report.*`.

## Visual regression (scaffold)

- Profile `visual-regression-tests` and script `scripts/ci/visual-regression.sh` define the extension point. There is **no** pixel-based baseline suite in-tree yet; the stage is non-blocking and documents how to add baselines later.

## Performance sanity (scaffold)

- Profile `performance-tests` and script `scripts/ci/performance-sanity.sh` define the extension point for future timing budgets (startup, load representative diagram). Non-blocking until implemented.

## Limitations (honest)

- AppImage generation is disabled in `linux` JavaPackager config (upstream `appimagetool` URL 404).
- SpotBugs may report many issues; full fail-on-bugs for every mode is not enabled until the backlog is addressed.
- Packaged-artifact smoke tests validate presence, size, and basic file typing — not a full install or GUI smoke run.

## Next hardening steps

1. Enable SpotBugs `check` as blocking on `pr` after reducing or suppressing the known backlog with review.
2. Add JaCoCo check to PR using incremental coverage or a dedicated PR coverage budget once tooling is chosen.
3. Extend `scripts/ci/verify-packaged-artifacts.sh` with controlled unpack or headless install smoke.
4. Implement visual regression with stable rendering hooks (not raw pixel hunts on full windows).
5. Add cryptographic signing of release artifacts (pipeline hook only; no fake signing in-repo).
