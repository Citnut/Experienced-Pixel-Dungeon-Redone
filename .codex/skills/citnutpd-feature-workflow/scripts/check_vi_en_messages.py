#!/usr/bin/env python3
"""Check English/Vietnamese message file parity for CitnutPD."""

from __future__ import annotations

import argparse
import pathlib
import re
import sys


KEY_RE = re.compile(r"^\s*([^#!\s][^=:\s]*)\s*[:=]")


def parse_keys(path: pathlib.Path) -> set[str]:
    keys: set[str] = set()
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.rstrip()
        if not line or line.lstrip().startswith("#") or line.lstrip().startswith("!"):
            continue
        match = KEY_RE.match(line)
        if match:
            keys.add(match.group(1).strip())
    return keys


def find_base_files(messages_root: pathlib.Path) -> list[pathlib.Path]:
    base_files: list[pathlib.Path] = []
    for path in sorted(messages_root.rglob("*.properties")):
        name = path.stem
        if name.endswith("_vi"):
            continue
        if "_" in name:
            # Skip localized files like items_ja.properties.
            continue
        base_files.append(path)
    return base_files


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Validate EN/VI properties parity under core/src/main/assets/messages"
    )
    parser.add_argument(
        "--messages-root",
        default="core/src/main/assets/messages",
        help="Path to messages root (default: core/src/main/assets/messages)",
    )
    args = parser.parse_args()

    messages_root = pathlib.Path(args.messages_root)
    if not messages_root.exists():
        print(f"[ERROR] Messages root not found: {messages_root}")
        return 2

    issues = 0
    for base_file in find_base_files(messages_root):
        vi_file = base_file.with_name(f"{base_file.stem}_vi.properties")
        if not vi_file.exists():
            print(f"[MISSING_FILE] {vi_file}")
            issues += 1
            continue

        base_keys = parse_keys(base_file)
        vi_keys = parse_keys(vi_file)

        missing_in_vi = sorted(base_keys - vi_keys)
        missing_in_en = sorted(vi_keys - base_keys)

        if missing_in_vi:
            print(f"[MISSING_KEYS_VI] {vi_file}")
            for key in missing_in_vi:
                print(f"  - {key}")
            issues += len(missing_in_vi)
        if missing_in_en:
            print(f"[EXTRA_KEYS_VI] {vi_file}")
            for key in missing_in_en:
                print(f"  - {key}")
            issues += len(missing_in_en)

    if issues:
        print(f"\nFAILED: {issues} issue(s) found.")
        return 1

    print("OK: English/Vietnamese message keys are in sync.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
