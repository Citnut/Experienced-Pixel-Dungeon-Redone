# ModKit

Tool nhỏ hỗ trợ tạo mod, build jar (nếu có code), và đóng gói zip.

## Yêu cầu

- Python 3
- Nếu build code mod: cần `javac` và `jar` trong PATH

## Cách dùng

### 1) Tạo mod từ template

```bash
python3 tools/modkit/modkit.py init my-mod
```

Dùng template data‑only:

```bash
python3 tools/modkit/modkit.py init my-mod --data-only
```

### 2) Build jar cho code‑mod

Cần classpath trỏ tới game API (ví dụ classes của `core`):

```bash
./gradlew core:compileJava
python3 tools/modkit/modkit.py build-jar my-mod \
  --classpath core/build/classes/java/main
```

### 3) Đóng gói zip

```bash
python3 tools/modkit/modkit.py pack my-mod
```

Tùy chọn:
- `--enable` hoặc `--disable`: tạo `mod.enabled`.
- `--api-version`, `--min-game`, `--max-game`: cập nhật compat trong `mod.json`.
- `--no-verify`: bỏ qua kiểm tra schema.

### 4) Build + zip (tự động nếu có code/)

```bash
python3 tools/modkit/modkit.py build my-mod \
  --classpath core/build/classes/java/main
```

Kết quả sẽ tạo `my-mod.zip` cùng cấp với thư mục mod.

## Lưu ý

- Nếu dùng các flag compat, tool sẽ cập nhật `mod.json`.
- Khi dùng zip, game sẽ tự cache vào `mods/.cache/`.
