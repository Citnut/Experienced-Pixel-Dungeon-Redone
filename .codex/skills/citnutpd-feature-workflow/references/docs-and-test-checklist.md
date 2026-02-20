# Docs and Test Checklist

Use this checklist before finishing any feature update.

## 1. Changelog entry

- Update `core/src/main/java/com/shatteredpixel/citnutpixeldungeon/ui/changelist/CitnutPDChanges.java`.
- Keep all added notes in English.

## 2. Localization

- Add/update English keys in base files under `core/src/main/assets/messages`.
- Add/update matching Vietnamese keys in `*_vi.properties`.
- Run:

```bash
python .codex/skills/citnutpd-feature-workflow/scripts/check_vi_en_messages.py
```

## 3. Documentation impact

Update docs when user-facing behavior, developer workflow, or architecture changed.

- `README.md` for setup, high-level features, and usage changes.
- `CHANGELOG.md` for release narrative beyond in-game changelist if needed.
- `docs/` files for subsystem-specific behavior.

If no doc file needs changes, state why in final handoff.

## 4. Verification commands

Run relevant checks and fix failures:

```bash
./gradlew test
./gradlew desktop:compileJava
```

Add targeted module/test commands as needed for the change scope.
