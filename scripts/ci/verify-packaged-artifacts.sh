#!/usr/bin/env bash
# Product-level smoke checks for Linux installers produced under target/artifacts/.
# Extension point: add dpkg-deb -c, fakeroot install, or headless launcher checks when robust.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
ART="${ROOT}/target/artifacts"

echo "[ci] verify-packaged-artifacts: ARTIFACT_DIR=${ART}"

if [[ ! -d "${ART}" ]]; then
  echo "[ci] ERROR: artifact directory missing: ${ART}" >&2
  exit 2
fi

shopt -s nullglob
debs=( "${ART}"/*.deb )
rpms=( "${ART}"/*.rpm )

if [[ ${#debs[@]} -eq 0 && ${#rpms[@]} -eq 0 ]]; then
  echo "[ci] ERROR: no .deb or .rpm found under ${ART}" >&2
  exit 2
fi

check_file() {
  local f="$1"
  if [[ ! -f "$f" ]]; then
    echo "[ci] ERROR: missing file: $f" >&2
    return 1
  fi
  local sz
  sz="$(wc -c < "$f" | tr -d ' ')"
  if [[ "${sz}" -lt 4096 ]]; then
    echo "[ci] ERROR: suspiciously small artifact (${sz} bytes): $f" >&2
    return 1
  fi
  if command -v file >/dev/null 2>&1; then
    local ty
    ty="$(file -b "$f")"
    echo "[ci] OK size=${sz} type=${ty} path=$f"
  else
    echo "[ci] OK size=${sz} path=$f (file(1) not installed)"
  fi
}

for f in "${debs[@]}"; do check_file "$f"; done
for f in "${rpms[@]}"; do check_file "$f"; done

if [[ ${#debs[@]} -gt 0 ]] && command -v dpkg-deb >/dev/null 2>&1; then
  echo "[ci] dpkg-deb -I (first deb only)"
  dpkg-deb -I "${debs[0]}" | head -20 || true
fi

echo "[ci] verify-packaged-artifacts: OK"
