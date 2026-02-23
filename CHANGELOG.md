# Changelog

All notable changes to this project are documented in this file.

The format is based on Keep a Changelog and follows semantic versioning where practical.

## [3.1.0] - 2026-02-11

Author: Unknown (local git user not configured)

### Summary
Added an integrated Debug Item Browser for `ScrollOfDebug`, including searchable item browsing, item detail and quantity controls, and direct item acquisition from a graphical window flow.

### New Files / Classes Added
- `core/src/main/java/com/zrp200/scrollofdebug/DebugItemBrowserWindow.java`
- `core/src/main/java/com/zrp200/scrollofdebug/DebugItemDetailWindow.java`
- `core/src/test/java/com/zrp200/scrollofdebug/DebugItemBrowserFeatureTest.java`

### Behavior Changes
- `ScrollOfDebug` read/use behavior now opens `DebugItemBrowserWindow` by default instead of the command text input.
- Existing command functionality is preserved and is available through the new browser's `Commands` button.

### UI Additions
- New `Debug Item Browser` window:
  - Scrollable icon grid of instantiable item classes
  - Search input with case-insensitive partial matching
  - Built-in scroll thumb behavior through `ScrollPane`
  - Hover and selection visual highlighting for icon slots
  - Close button and command console shortcut button
- New `Debug Item Detail` window:
  - Item icon and name header
  - Description + type/stats/attributes panel
  - Quantity input with validation
  - `Take Item` action button and close button

### Test Cases Added
- `ScrollOfDebug` read target window is the Debug Item Browser.
- Item search filtering is case-insensitive and supports partial matches.
- Browser scroll calculations detect overflow for large item sets.
- Detail text rendering includes item type/stats/description content.
- Item acquisition adds requested quantities and clamps to item stack limits when applicable.

### Changes History (Incremental Commits)
- `debug-browser-ui`: Added new debug browser window and icon grid rendering.
- `debug-browser-search`: Added search input and query-based filtering.
- `debug-item-detail`: Added item detail panel with stats/description and quantity entry.
- `debug-item-acquire`: Added validated inventory acquisition logic with stack-limit clamping.
- `debug-browser-tests`: Added unit coverage for browser behavior and acquisition flow.

## [3.0.0] - 2026-02-11

Author: Unknown (local git user not configured)

### Summary
This release introduces a first-party modding framework and in-game mod management UI, modernizes the build/toolchain to Java 17 + updated Android Gradle tooling, and expands ScrollOfDebug with inline ghost-text autocomplete and broader command/runtime improvements.

### Added
- New mod system under `core/src/main/java/com/shatteredpixel/citnutpixeldungeon/mod/`:
  - Mod metadata/model classes (`GameMod`, `ModItemDef`, `ModRegistry`, etc.)
  - Runtime loader and cache manager (`ModManager`)
  - Mod item implementations (`ModItem`, `ModMeleeWeapon`, `ModMissileWeapon`, `ModArmor`)
- In-game mod UI:
  - `WndMods` for listing, enabling/disabling, importing, reloading, and cache clearing
  - `WndModDetails` for per-mod metadata, item previews/stats, and uninstall flow
- ScrollOfDebug inline autocomplete infrastructure:
  - `InlineSuggestionEngine` interface
  - Ghost-text rendering support in text input widgets
  - Accept suggestion via `Tab` and `Right Arrow`
- Expanded localization surface for new mod UI strings across language packs.
- Synced EN/VI message parity across the existing message bundle (missing/extra key cleanup, added missing `services_vi.properties`).
- New guidebook/modding journal support (`Document.GUIDE_MODS`).

### Modding
- Added startup-time mod loading via `ModManager.load()` in `ShatteredPixelDungeon`.
- Added in-game Mod Manager window (`WndMods`) in settings:
  - List installed mods
  - Enable/disable per mod
  - Import mod from file path or platform file picker
  - Reload mods and clear mod cache
- Added per-mod detail window (`WndModDetails`):
  - View mod metadata (id/version/author/homepage)
  - Show mod path and code metadata (entrypoint/package/class/jar) when present
  - Inspect mod item definitions and preview item stats
  - Uninstall mod with confirmation flow
- Integrated mod item generation into core loot generation path (`Generator.random*`).
- Added guidebook/document support for modding section (`Document.GUIDE_MODS`).

### Changed
- Version bump:
  - `appVersionName`: `ExpPD-2.19.0` -> `CitnutExpPD-3.0.0`
  - `appVersionCode`: retained at `666`
- Build/toolchain modernization:
  - Java target moved to 17 project-wide (`appJavaCompatibility = JavaVersion.VERSION_17`)
  - AGP/R8 updated (`com.android.tools.build:gradle:8.13.2`, `com.android.tools:r8:8.13.19`)
  - Android compile/target SDK moved to 36
  - JDK toolchains configured for `core`, `desktop`, `services`
- Desktop packaging/runtime config updated for JDK 17 and tuned JVM GC options.
- `Generator` now integrates mod item rolls via `ModManager.rollItem(...)`.
- `ShatteredPixelDungeon` initialization now loads mods on startup.
- ScrollOfDebug internals significantly refactored and extended:
  - Macro handling and persistence improvements
  - Additional command features (`warp`, macro flow, argument coercion updates)
  - Better reflection/argument parsing resilience and error reporting

### Fixed
- Android build lint blockers:
  - Fixed manifest class resolution issues by aligning launcher/support class file paths with declared package.
  - Corrected invalid `dependencies {}` placement inside `buildTypes` in `android/build.gradle`.
  - Suppressed predictive-back lint blocker for intentional no-op `onBackPressed()` handling.
- ScrollOfDebug input UX:
  - Real-time inline suggestion refresh and safe acceptance behavior at end-of-input cursor.
- Class lookup reliability updates in `PackageTrie` and variable name completion support in `Variable`.

### Updated Modules
- `SPD-classes`
- `core`
- `android`
- `desktop`
- `ios`
- `services`
- `docs`

### Build/Test Status
- `./gradlew test`: PASSED
- `./gradlew build`: PASSED

[3.1.0]: https://example.invalid/releases/3.1.0
[3.0.0]: https://example.invalid/releases/3.0.0
