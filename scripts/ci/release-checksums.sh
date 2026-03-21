#!/usr/bin/env bash
# Write SHA256 checksums for packaged artifacts to target/artifacts/SHA256SUMS for release traceability.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
ART="${ROOT}/target/artifacts"
OUT="${ART}/SHA256SUMS"

echo "[ci] release-checksums: ARTIFACT_DIR=${ART}"

if [[ ! -d "${ART}" ]]; then
  echo "[ci] ERROR: artifact directory missing: ${ART}" >&2
  exit 2
fi

(
  cd "${ART}"
  shopt -s nullglob
  files=( *.deb *.rpm )
  if [[ ${#files[@]} -eq 0 ]]; then
    echo "[ci] ERROR: no .deb or .rpm to checksum" >&2
    exit 2
  fi
  if command -v sha256sum >/dev/null 2>&1; then
    sha256sum "${files[@]}" > SHA256SUMS
  elif command -v shasum >/dev/null 2>&1; then
    shasum -a 256 "${files[@]}" > SHA256SUMS
  else
    echo "[ci] ERROR: need sha256sum or shasum" >&2
    exit 2
  fi
)

echo "[ci] wrote ${OUT}"
cat "${OUT}"
