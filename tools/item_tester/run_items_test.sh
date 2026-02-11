#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

./gradlew :core:test --tests com.shatteredpixel.citnutpixeldungeon.items.ItemSanityTest
