# Hướng dẫn code cho Experienced Pixel Dungeon (Redone)

Tài liệu này tập trung vào cấu trúc dự án, vai trò các gói/class chính, và cách thêm vật phẩm/nội dung game. Mục tiêu là đủ thực dụng để bạn bắt tay chỉnh sửa nhanh, nhưng vẫn chỉ ra các điểm “mấu chốt” trong codebase.

## 1. Cấu trúc thư mục tổng quan

- `core/`: toàn bộ logic game, gameplay, assets, UI, scene.
- `desktop/`: launcher và cấu hình cho bản desktop (LWJGL3).
- `android/`, `ios/`: launcher và cấu hình mobile.
- `SPD-classes/`: các lớp engine/tiện ích dùng chung (Noosa, utils, v.v.).
- `services/`: các service như cập nhật, news, v.v.
- `docs/`: tài liệu biên dịch và hướng dẫn thay đổi.

## 2. Luồng chạy chính

- Desktop: `desktop/src/main/java/.../DesktopLauncher.java` tạo `Lwjgl3Application` và khởi chạy game.
- Lớp game chính: `core/src/main/java/.../ShatteredPixelDungeon.java` khởi tạo game và scene.
- Scene trọng yếu: `core/src/main/java/.../scenes/GameScene.java` (vòng lặp gameplay, render, input).
- Trạng thái game: `core/src/main/java/.../Dungeon.java` giữ trạng thái dungeon hiện tại, hero, level, v.v.

## 3. Các class/gói quan trọng nên biết

### 3.1 Gameplay nền tảng

- `Dungeon`: trung tâm trạng thái (depth, level, hero, seed, v.v.).
- `Level` và các subclass trong `core/src/main/java/.../levels`: map generation, mobs, items, traps, tiles.
- `Actor`/`Char` trong `core/src/main/java/.../actors`: base cho entity hoạt động theo lượt.
- `Mob` trong `core/src/main/java/.../actors/mobs`: quái vật, AI, loot.
- `Hero` trong `core/src/main/java/.../actors/hero`: nhân vật người chơi.
- `Item` trong `core/src/main/java/.../items`: base cho tất cả vật phẩm.
- `Generator` trong `core/src/main/java/.../items/Generator.java`: bảng spawn/loot và phân loại item.
- `MobSpawner` trong `core/src/main/java/.../actors/mobs/MobSpawner.java`: luân phiên mob theo tầng.

### 3.2 Tài nguyên & bản địa hóa

- `Assets`: định nghĩa đường dẫn texture/âm thanh trong `core/src/main/java/.../Assets.java`.
- Sprites item: `core/src/main/java/.../sprites/ItemSpriteSheet.java` đọc từ `Assets.Sprites.ITEMS`.
- Strings: `core/src/main/assets/messages/**` (mỗi group có các file `*_vi.properties`, `*_en.properties`, ...).
- Các mô tả journal: `core/src/main/assets/messages/journal/`.

## 4. Hướng dẫn thêm vật phẩm (Item)

### 4.1 Tạo class item

1. Tạo class mới trong package phù hợp, ví dụ `core/src/main/java/.../items/food/MyFood.java`.
2. Kế thừa từ `Item` hoặc subclass hợp lý (`Food`, `Potion`, `Wand`, `Armor`, ...).
3. Gán sprite/icon:
   - `image = ItemSpriteSheet.MY_ITEM;`
   - (tuỳ loại) `icon = ItemSpriteSheet.Icons.SOME_ICON;`
4. Override hành vi:
   - `actions(Hero hero)` để thêm action tùy chỉnh.
   - `execute(Hero hero, String action)` để xử lý action.
   - Các hook như `onThrow`, `onPickUp`, `onDetach`, `onDestroy`, ...

### 4.2 Thêm sprite cho item

1. Thêm tile vào sheet `core/src/main/assets/sprites/idk.png` (theo vị trí grid 16x16).
2. Thêm hằng số trong `ItemSpriteSheet.java`:
   - Tạo `public static final int MY_ITEM = ...;`
   - Gọi `assignItemRect(MY_ITEM, w, h);` nếu cần kích thước đặc biệt.

### 4.3 Đăng ký spawn/loot

- Nếu item xuất hiện ngẫu nhiên, thêm vào `Generator`:
  - Thêm class vào category phù hợp, hoặc tạo category mới.
- Nếu item rơi từ mob cụ thể, thêm vào class mob đó (thường trong `die()` hoặc `loot`/`drop` logic).
- Nếu item xuất hiện trong level, thêm vào `Level.createItems()` của level phù hợp.

### 4.4 Text mô tả

- Thêm key/description vào file messages tương ứng:
  - `core/src/main/assets/messages/items/items_vi.properties` (hoặc group cụ thể khác).
- Trong code, dùng `Messages.get(this, "desc")` hoặc `Messages.get(ItemClass.class, "name")`.

## 5. Hướng dẫn thêm quái (Mob)

### 5.1 Tạo class mob

1. Tạo class mới trong `core/src/main/java/.../actors/mobs`.
2. Kế thừa `Mob` hoặc subclass gần nhất.
3. Thiết lập chỉ số và hành vi:
   - `HP`, `defenseSkill`, `attackSkill`, `damageRoll()`.
   - `attackProc()`, `defenseProc()`, `die()` để drop/hiệu ứng đặc biệt.
4. Gán sprite class bằng `spriteClass = MyMobSprite.class`.

### 5.2 Thêm sprite cho mob

1. Tạo sprite class trong `core/src/main/java/.../sprites`.
2. Thêm texture mới vào `core/src/main/assets/sprites/`.
3. Đăng ký đường dẫn trong `Assets.Sprites`.

### 5.3 Thêm vào rotation spawn

- Cập nhật `MobSpawner.getMobRotation()` và `standardMobRotation()`:
  - Bổ sung class mob vào tầng phù hợp.
- Với boss/special level, cập nhật `createMobs()` trong level đó.

### 5.4 Text mô tả mob

- Thêm key vào `core/src/main/assets/messages/actors/actors_vi.properties`.

## 6. Thêm nội dung game khác

### 6.1 Level/biome mới

- Tạo subclass của `Level` hoặc `RegularLevel` trong `core/src/main/java/.../levels`.
- Implement `build()`, `createMobs()`, `createItems()`.
- Nếu dùng painter: thêm class trong `core/src/main/java/.../levels/painters`.
- Kết nối level vào flow bằng cách chỉnh `Dungeon`/`LevelTransition` nếu cần branch.

### 6.2 Traps/Plants/Buffs

- Traps: `core/src/main/java/.../levels/traps`.
- Plants: `core/src/main/java/.../plants`.
- Buffs: `core/src/main/java/.../actors/buffs`.
- Nhớ cập nhật spawn list hoặc logic khởi tạo tương ứng trong level.

### 6.3 Journal, quest, UI

- Journal entries trong `core/src/main/assets/messages/journal/` và các class trong `core/src/main/java/.../journal`.
- Windows/Dialogs trong `core/src/main/java/.../windows`.
- UI controls trong `core/src/main/java/.../ui`.

## 7. Modpack (data-driven)

Bạn có thể dùng modpack dạng dữ liệu để thêm item mà không sửa code gốc. Tài liệu chi tiết nằm ở:

- `docs/modpack.md`

## 8. Checklist thêm nội dung nhanh

1. Code class mới (Item/Mob/Level/...)
2. Sprite/texture + đăng ký trong `Assets`/`ItemSpriteSheet`
3. Localization strings (`messages/**`)
4. Thêm vào spawn tables hoặc logic tạo (Generator, MobSpawner, createItems/createMobs)
5. Build & chạy thử (desktop: `./gradlew desktop:debug`)

## 9. Gợi ý nơi bắt đầu đọc code

- `core/src/main/java/.../Dungeon.java`
- `core/src/main/java/.../scenes/GameScene.java`
- `core/src/main/java/.../actors/mobs/MobSpawner.java`
- `core/src/main/java/.../items/Generator.java`
- `core/src/main/java/.../sprites/ItemSpriteSheet.java`

---

Nếu bạn muốn, mình có thể bổ sung phần hướng dẫn cụ thể hơn theo nhu cầu (ví dụ: tạo potion mới, vũ khí mới, boss mới, hay branch dungeon riêng).
