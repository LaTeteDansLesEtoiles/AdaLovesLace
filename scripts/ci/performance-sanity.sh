#!/usr/bin/env bash
# Scaffold for future performance sanity (startup or load representative diagram), not microbenchmarks.
# Exits 0; writes a marker under target/ci/ for archival.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
MARKER_DIR="${ROOT}/target/ci/performance"
mkdir -p "${MARKER_DIR}"

cat > "${MARKER_DIR}/STATUS.txt" << 'EOF'
Status: scaffold only.

Intended evolution:
- Time bounded operations (cold start, open representative .lace, export PDF/image) with generous thresholds.
- Publish timing text or JUnit-style report consumed by Jenkins.

This script intentionally performs no timing measurement yet.
EOF

echo "[ci] performance-sanity scaffold: wrote ${MARKER_DIR}/STATUS.txt"
