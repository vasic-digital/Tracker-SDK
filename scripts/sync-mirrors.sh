#!/usr/bin/env bash
# scripts/sync-mirrors.sh — push this repo to all configured upstreams.
# 2-upstream scope per spec deviation 2026-04-30 (GitHub + GitLab; reduced
# from the original 4-upstream policy).
set -euo pipefail
cd "$(dirname "$0")/.."

UPSTREAMS=(
  "github  git@github.com:vasic-digital/Tracker-SDK.git"
  "gitlab  git@gitlab.com:vasic-digital/Tracker-SDK.git"
)

for entry in "${UPSTREAMS[@]}"; do
  read -r name url <<<"$entry"
  if ! git remote get-url "$name" >/dev/null 2>&1; then
    git remote add "$name" "$url"
  else
    git remote set-url "$name" "$url"
  fi
done

declare -A SHAS
for entry in "${UPSTREAMS[@]}"; do
  read -r name url <<<"$entry"
  echo ">>> Pushing to $name"
  git push "$name" --tags --force-with-lease
  git push "$name" master --force-with-lease
  SHAS[$name]=$(git ls-remote "$name" master | awk '{print $1}')
done

EXPECTED=$(git rev-parse master)
for entry in "${UPSTREAMS[@]}"; do
  read -r name _ <<<"$entry"
  if [[ "${SHAS[$name]}" != "$EXPECTED" ]]; then
    echo "MIRROR MISMATCH: $name=${SHAS[$name]}, expected=$EXPECTED" >&2
    exit 1
  fi
done
echo "All upstreams converged on $EXPECTED"
