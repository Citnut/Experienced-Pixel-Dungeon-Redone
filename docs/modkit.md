# ModKit

ModKit là công cụ dòng lệnh hỗ trợ tạo mod, build jar (nếu có code‑mod), kiểm tra cấu trúc, và đóng gói thành `.zip`.

## 1. Cài đặt

Yêu cầu:
- Python 3
- (Tuỳ chọn) `javac` và `jar` nếu có code‑mod

## 2. Tạo mod mới

```bash
python3 tools/modkit/modkit.py init my-mod
```

Tạo mod data‑only:

```bash
python3 tools/modkit/modkit.py init my-mod --data-only
```

## 3. Build JAR cho code‑mod

```bash
./gradlew core:compileJava
python3 tools/modkit/modkit.py build-jar my-mod \
  --classpath core/build/classes/java/main
```

## 4. Đóng gói ZIP

```bash
python3 tools/modkit/modkit.py pack my-mod
```

Tuỳ chọn:
- `--enable` hoặc `--disable`: tạo `mod.enabled`.
- `--api-version`, `--min-game`, `--max-game`: cập nhật compat trong `mod.json`.
- `--no-verify`: bỏ qua kiểm tra schema.

## 5. Build + ZIP tự động

```bash
python3 tools/modkit/modkit.py build my-mod \
  --classpath core/build/classes/java/main
```

## 6. Kiểm tra nhanh

ModKit sẽ kiểm tra:
- Có `mod.json`.
- `items.json` hợp lệ và có `items[]`.
- Item có `id` và `category`.
- `item_type` nằm trong danh sách hợp lệ.

## 7. Kết quả

Sau khi đóng gói, file `.zip` sẽ được tạo ngay cạnh thư mục mod:

```
my-mod/
my-mod.zip
```

Bạn chỉ cần copy file zip này vào thư mục `mods/` của game.
