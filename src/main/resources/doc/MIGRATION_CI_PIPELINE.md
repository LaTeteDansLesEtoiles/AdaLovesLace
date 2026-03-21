# Migration: product-oriented CI pipeline

## What changed

- **Jenkinsfile** is now a **declarative** pipeline with an explicit `PIPELINE_MODE` parameter (`pr` | `nightly` | `release`).
- **Maven Failsafe** executions use the conventional **`integration-test`** phase for test runs and a single **`verify`** phase goal for Failsafe verification, instead of binding both `integration-test` and `verify` to the **`test`** phase (which is non-standard and easy to mis-order with other plugins). **Separate tag filters** use **`failsafe.integrationGroups`** / **`failsafe.functionalGroups`**; profile **`ci-all-it`** runs both IT and FT in **one** `integration-test` invocation for CI.
- **Jenkins** stages are aligned with **Surefire vs Failsafe** report collection (Surefire XML vs Failsafe XML).
- **Product-level** steps were added: `scripts/ci/verify-packaged-artifacts.sh` and `scripts/ci/release-checksums.sh` for release mode.
- **Scaffolding** (non-blocking) was added for future **visual regression** and **performance** work: Maven profiles, shell entry points, and documentation in `src/main/resources/doc/ci-pipeline.md`.
- **OWASP dependency-check** remains a **dedicated stage** for nightly/release; `mvn verify` is invoked with **`-Ddependency-check.skip=true`** to avoid running the same check twice in one build.

## Why it changed

- The previous pipeline was largely a sequence of ad hoc `mvnw` invocations. Several quality plugins are bound to **`verify`**, but Jenkins did not run a coherent `verify` after tests, so **JaCoCo `check`**, **PMD/CPD verify executions**, and **SpotBugs `check`** were not consistently enforced by the same lifecycle that the POM describes.
- **Integration and functional** tests were driven through `mvn test`, which relied on Failsafe being bound to **`test`**; that made local and CI lifecycle semantics diverge from common Maven practice.
- **Packaged artifacts** were archived without structured, scriptable checks on whether deliverables exist and are minimally sane.

## What remains out of scope (honest)

- **SpotBugs fail-on-bugs** for every build: the repository can carry a large backlog of findings. The hardened pipeline keeps **XML publication** for Warnings NG and documents that turning **`spotbugs:check`** into a hard gate for `pr` requires a dedicated remediation or suppression pass. **Profile `ci-pr`** temporarily sets SpotBugs **`failOnError`** to **false** for the `verify` invocation only; nightly/release use **`ci-nightly`** / **`ci-release`** with **`failOnError` true** when those profiles are used (if the backlog fails the build, that is signal, not noise).
- **JaCoCo `check` on PR**: enforcing **60%** line thresholds on a **unit-only** run can be misleading. **Profile `ci-pr`** disables the **`jacoco-check`** execution; **nightly/release** keep it on full **`verify`** after all tests. PRs still produce coverage **reports** when `jacoco:report` runs.
- **AppImage** remains disabled in JavaPackager (upstream download URL breakage).
- **Visual regression** and **performance** scaffolding does not run real baselines or benchmarks yet.

## What should come next

1. Reduce SpotBugs findings or add reviewed suppressions, then set **`ci-pr`** to use **`failOnError=true`** for SpotBugs.
2. Decide on **PR coverage policy** (incremental coverage vs full-project threshold) and re-enable **`jacoco-check`** on PR if appropriate.
3. Extend `scripts/ci/verify-packaged-artifacts.sh` with unpack or controlled install smoke.
4. Implement signing hooks for release artifacts (no pretend signing in the repository).

Target design and staged migration rationale: **`src/main/resources/doc/ci-target-phase2.md`**.

## Local commands (quick reference)

```text
./mvnw clean test -P unit-tests
./mvnw clean integration-test -P integration-tests
./mvnw clean integration-test -P functional-tests
./mvnw clean integration-test -P ci-all-it
./mvnw clean verify -P ci-nightly
./mvnw clean verify -P ci-pr -DskipTests -DskipITs -DskipFTs
./mvnw clean package -P linux -DskipTests -DskipITs -DskipFTs -Ddependency-check.skip=true
```

Functional tests require a display (e.g. Xvfb on Linux CI).
