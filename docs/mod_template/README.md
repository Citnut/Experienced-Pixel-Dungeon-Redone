# Example Mod Template

Thư mục này là mẫu modpack cơ bản. Bạn có thể copy vào thư mục `mods/` của game và đổi `id`, `name` tùy ý.

Tài liệu API chi tiết: `docs/mod_api.md`.

## Cấu trúc

- `mod.json`: thông tin mod + entrypoint (code‑mod desktop)
- `items.json`: định nghĩa item data‑driven
- `code/ExampleMod.java`: ví dụ code‑mod (desktop)

## Dùng data‑mod nhanh

1. Copy `docs/mod_template` thành thư mục mới trong `mods/`.
2. Sửa `mod.json` và `items.json`.
3. Vào menu Mods trong Settings để bật/tắt mod.

## Dùng code‑mod (desktop)

1. Biên dịch `ExampleMod.java` thành `example-mod.jar`.
2. Đặt jar vào cùng thư mục với `mod.json`.
3. Sửa `entrypoint` trong `mod.json` nếu đổi package/class.

Lưu ý: code‑mod yêu cầu classpath của game để biên dịch (các class trong `core`).

## Dùng ModKit (khuyên dùng)

Tạo mod, build jar, và đóng gói zip:
```bash
python3 tools/modkit/modkit.py init my-mod
python3 tools/modkit/modkit.py build my-mod --classpath core/build/classes/java/main
```

### Cách build code‑mod thành JAR

#### Cách 1: Dùng Gradle (Khuyên dùng)

Nếu mod của bạn trong project này, hãy sử dụng Gradle:

```bash
./gradlew build
```

JAR file sẽ được tạo trong `build/libs/`.

#### Cách 2: Biên dịch thủ công với javac

1. **Tìm classpath của game** - cần các compiled classes từ `core` module:
   ```bash
   # Build core module trước
   ./gradlew core:build
   ```

2. **Biên dịch file Java**:
   ```bash
   # Tạo thư mục output
   mkdir -p bin
   
   # Biên dịch với classpath
   javac -cp core/build/classes/java/main code/ExampleMod.java -d bin
   ```

3. **Đóng gói thành JAR**:
   ```bash
   # Tạo MANIFEST
   echo "Manifest-Version: 1.0" > MANIFEST.MF
   
   # Đóng gói JAR
   cd bin
   jar cfm ../example-mod.jar ../MANIFEST.MF .
   cd ..
   ```

4. **Xác minh JAR**:
   ```bash
   jar tf example-mod.jar
   ```

#### Cách 3: Script build tự động

Tạo file `build.sh` (Linux/Mac) hoặc `build.bat` (Windows):

**build.sh** (Linux/Mac):
```bash
#!/bin/bash
MOD_NAME="example-mod"
ENTRY_CLASS="com.example.ExampleMod"

# Build core dependency
./gradlew core:build

# Biên dịch
mkdir -p bin
javac -cp core/build/classes/java/main code/*.java -d bin

# Đóng gói JAR
cd bin
jar cf ../$MOD_NAME.jar .
cd ..

echo "✓ Built: $MOD_NAME.jar"
```

**build.bat** (Windows):
```batch
@echo off
set MOD_NAME=example-mod
set ENTRY_CLASS=com.example.ExampleMod

REM Build core dependency
call gradlew.bat core:build

REM Biên dịch
if not exist bin mkdir bin
javac -cp core/build/classes/java/main code/*.java -d bin

REM Đóng gói JAR
cd bin
jar cf ..\%MOD_NAME%.jar .
cd ..

echo OK: %MOD_NAME%.jar created
```

Chạy:
```bash
chmod +x build.sh
./build.sh
```

### Troubleshooting

- **Lỗi: "cannot find symbol"** → Kiểm tra classpath, builder không tìm được core classes
- **Lỗi: "No such file or directory"** → Kiểm tra đường dẫn thư mục, cấu trúc folder phải đúng
- **JAR không load trong game** → Kiểm tra `entrypoint` trong `mod.json` khớp với package/class name
