---
name: citnutpd-feature-workflow
description: "Implement and update features in Experienced Pixel Dungeon with project-specific release discipline. Use when changing gameplay, UI, content, systems, balancing, or bug fixes that affect Java code, assets, localization, or player-facing behavior. Enforce four rules: always add English changelog notes in core/src/main/java/com/shatteredpixel/citnutpixeldungeon/ui/changelist/CitnutPDChanges.java, always provide both English and Vietnamese texts through core/src/main/assets/messages/*.properties files, update relevant docs when behavior or workflows change, and always run tests/build checks then fix failures before finishing."
---

# CitnutPD Feature Workflow

Use this workflow for every feature/code update in this repository.

## Workflow

1. Clarify scope and changed surfaces
- Identify changed Java packages, UI scenes/windows, items/mechanics, and message keys.
- List player-facing effects before editing code.

2. Implement code change
- Update code/assets as requested.
- Keep modifications minimal and coherent with existing architecture.

3. Update changelog entries in English (mandatory)
- Edit `core/src/main/java/com/shatteredpixel/citnutpixeldungeon/ui/changelist/CitnutPDChanges.java`.
- Add or update grouped `ChangeButton` entries for the new feature/fix.
- Keep notes concise, searchable, and English-only.
- Do not skip this step for gameplay/UI/content changes.

4. Apply bilingual localization via message files (mandatory)
- Never hardcode new player-facing text in Java when it belongs in messages.
- Add/update keys in English base file `*.properties` and Vietnamese pair `*_vi.properties`.
- Message files live under `core/src/main/assets/messages`.
- Run `python .codex/skills/citnutpd-feature-workflow/scripts/check_vi_en_messages.py` to detect missing Vietnamese files/keys.

5. Update documentation when behavior changes
- If player behavior, setup steps, commands, modding flow, or architecture changes, update relevant docs.
- Check `README.md`, `CHANGELOG.md`, and impacted files in `docs/`.
- Use checklist in `references/docs-and-test-checklist.md`.

6. Verify by running tests/build checks (mandatory)
- Run the smallest meaningful command first, then broader checks as needed.
- Typical commands:
```bash
./gradlew test
./gradlew desktop:compileJava
```
- If a command fails, fix the issue and rerun until clean.
- Do not end with known failing checks.

7. Final quality gate
- Confirm changelog step is done.
- Confirm EN/VI localization parity for added keys.
- Confirm docs were updated or explicitly deemed unnecessary.
- Confirm tests/checks were run and are passing.

## Localization Rules

- Use the same message key namespace/style as nearby files.
- Preserve placeholders and formatting tokens consistently across languages (`%s`, `%d`, `\\n`, markdown-like markers).
- When adding a new base message file under a category, create the Vietnamese file in the same folder.

## Resources

### scripts/check_vi_en_messages.py
Validate English-Vietnamese message coverage and key parity in `core/src/main/assets/messages`.

### references/docs-and-test-checklist.md
Use as the final pre-handoff checklist for docs and verification commands.
