#!/usr/bin/env bash
# Scaffold for future visual regression (stable rendering hooks, not brittle full-window pixels).
# Exits 0; writes a marker under target/ci/ for archival.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
MARKER_DIR="${ROOT}/target/ci/visual-regression"
mkdir -p "${MARKER_DIR}"

cat > "${MARKER_DIR}/STATUS.txt" << 'EOF'
Status: scaffold only.

Intended evolution:
- Run a small set of deterministic render-to-image tests (e.g. off-screen scene or export path).
- Store baselines in-repo or as versioned CI artifacts.
- Fail only on meaningful image diffs with a documented threshold policy.

This script intentionally performs no pixel comparison yet.
EOF

echo "[ci] visual-regression scaffold: wrote ${MARKER_DIR}/STATUS.txt"
