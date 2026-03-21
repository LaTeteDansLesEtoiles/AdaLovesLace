# Phase 2 — Target pipeline design and migration plan

## Quality layers

| Layer | Intent | Primary mechanisms |
|-------|--------|--------------------|
| Source / build | Compile, code generation | `compile`, `test-compile`, protobuf |
| Test | Unit, integration, functional confidence | Surefire, Failsafe, JUnit publishers |
| Product | Deliverables sane and traceable | `scripts/ci/verify-packaged-artifacts.sh`, `release-checksums.sh`, artifact archival |
| Maintainability / security | Static analysis, supply chain | PMD/CPD/SpotBugs on `verify`, OWASP dependency-check (nightly/release) |

## Pipeline modes (target)

### `pr`

| Topic | Choice |
|-------|--------|
| Stages | Checkout, Toolchain, Compile, Unit tests, Quality gate (`verify` + `ci-pr`), Warnings NG (PMD/CPD/SpotBugs XML) |
| Blocking | Compile failure, unit test failure, PMD/CPD as configured on `verify`, SpotBugs **non-failing** on PR via `ci-pr` (`failOnError=false`) |
| Non-blocking (explicit) | JaCoCo **check** disabled on PR (`jacoco-check` execution disabled); integration/functional tests **not run** |
| Archived | Surefire XML, JaCoCo site/exec, static XML consumed by Warnings NG |
| Rationale | Fast feedback; avoids misleading coverage gate on partial test runs |

### `nightly`

| Topic | Choice |
|-------|--------|
| Stages | All PR stages plus Integration+functional (`integration-test` + `ci-all-it` under Xvfb), OWASP, informational freshness file, visual + performance **scaffolds** |
| Blocking | Same as PR for unit + verify (with **full** JaCoCo check and SpotBugs `failOnError=true`), plus IT/FT failures, OWASP CVSS gate |
| Non-blocking | Freshness report (best-effort), scaffold stages (always exit 0 until implemented) |
| Archived | Failsafe XML, dependency-check reports, `target/ci-freshness.txt`, `target/ci/**` scaffold outputs |
| Rationale | Deeper validation without blocking every merge on long-running or flaky externals |

### `release`

| Topic | Choice |
|-------|--------|
| Stages | Same test + analysis bar as nightly, plus Linux `package` (`-P linux`), packaged smoke, `SHA256SUMS` |
| Blocking | Nightly-equivalent gates plus smoke script failure (missing/empty packages) |
| Non-blocking | (none added beyond nightly’s informational items) |
| Archived | `.deb`, `.rpm`, `SHA256SUMS`, prior reports |
| Rationale | Traceable installers aligned with commit/build metadata already on Jenkins |

## Build time and flakiness controls

- **PR** omits IT/FT and OWASP and JaCoCo check.
- **Single** `integration-test -P ci-all-it` under Xvfb avoids multiple Maven invocations clobbering Failsafe state.
- **OWASP** once per nightly/release build; `verify` always passes `-Ddependency-check.skip=true`.
- TestFX tuning remains via existing system properties in the integration stage.

## Migration risk reduction

- Failsafe phase move is paired with **per-execution tag properties** and **`ci-all-it`**.
- **Declarative** pipeline replaces ambiguous scripted `environment` nesting.
- **Temporary compromises** documented in `src/main/resources/doc/MIGRATION_CI_PIPELINE.md` (SpotBugs/JaCoCo on PR).

## “No new crap” rule

- New profiles (`ci-pr`, `ci-nightly`, `ci-release`, `ci-all-it`) are markers and configuration, not new product features.
- Scaffolds write explicit `STATUS.txt` files stating maturity.

## Migration steps (execution order)

1. Land POM Failsafe + dependency-check execution id + profiles.
2. Land `scripts/ci/*.sh` and chmod.
3. Replace Jenkinsfile with declarative + `PIPELINE_MODE`.
4. Update documentation (`src/main/resources/doc/ci-pipeline.md`, `MIGRATION`, audit, this file).
5. Run `nightly` on agent; then `release` on tag or manual job.
