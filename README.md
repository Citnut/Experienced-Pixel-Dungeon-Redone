# Experienced Pixel Dungeon (Redone)

Experienced Pixel Dungeon (Redone) là một nhánh mở rộng từ [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon), tập trung vào gameplay dạng sandbox/grind, thêm hệ thống mod và nhiều thay đổi nội dung.

## Trạng thái dự án hiện tại

- Nền tảng đang build chính thức từ repo này: **Android** và **Desktop**.
- Toolchain chính: **Gradle Wrapper + Java 17**.
- Cấu hình version trong mã nguồn hiện tại:
  - `appVersionName`: `CitnutExpPD-26.0.0`
  - `appVersionCode`: `696`

## Build nhanh

Yêu cầu: JDK 17.

```bash
# chạy desktop debug
./gradlew desktop:debug

# build desktop release (JAR)
./gradlew desktop:release

# build android debug APK
./gradlew android:assembleDebug

# chạy test
./gradlew test
```

## Cấu trúc module

- `core/`: gameplay, logic game, UI, assets.
- `SPD-classes/`: engine/util dùng chung.
- `android/`: launcher và cấu hình Android.
- `desktop/`: launcher và cấu hình Desktop.
- `services/`: update/news services.
- `docs/`: tài liệu build, modding, hướng dẫn dev.
- `tools/`: công cụ hỗ trợ cho modding/test.

## Tài liệu

### Build & setup

- [Android setup](docs/getting-started-android.md)
- [Desktop setup](docs/getting-started-desktop.md)
- [iOS setup (tham khảo, không nằm trong module build hiện tại)](docs/getting-started-ios.md)

### Modding

- [Modpack guide](docs/modpack.md)
- [Mod API](docs/mod_api.md)
- [ModKit guide](docs/modkit.md)
- [Mod template](docs/mod_template/README.md)
- [Mod template (zip)](docs/mod_template_zip/README.md)

### Dev notes

- [Hướng dẫn code](docs/huong-dan-code.md)
- [Recommended changes](docs/recommended-changes.md)
- [Changelog](CHANGELOG.md)

### Công cụ

- [ModKit tool README](tools/modkit/README.md)
- [Item tester README](tools/item_tester/README.md)

## Releases

- [GitHub Releases](https://github.com/TrashboxBobylev/Experienced-Pixel-Dungeon-Redone/releases)

## License

Dự án phát hành theo [GPLv3](LICENSE.txt).
