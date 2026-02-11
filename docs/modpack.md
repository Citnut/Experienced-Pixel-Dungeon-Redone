# Modpack (data-driven) cho Experienced Pixel Dungeon

Tài liệu này mô tả cơ chế modpack (dạng dữ liệu + code mod desktop) để thêm/bớt vật phẩm mà không cần sửa code gốc. Cơ chế này ưu tiên **an toàn và giảm lỗi**, nên phạm vi hiện tại tập trung vào item, vũ khí cận chiến, giáp, và spawn theo bảng rơi.

## 1. Vị trí thư mục modpack

Game sẽ đọc mod từ thư mục `mods` trong **thư mục dữ liệu của game**:

- Windows: `C:\Users\<you>\AppData\Roaming\.<vendor>\<title>\mods`
- Linux: `~/.local/share/.<vendor>/<title>/mods`
- macOS: `~/Library/Application Support/<title>/mods`

Trong đó `<vendor>` và `<title>` là thông tin package của bản build (được game dùng cho lưu save).

Bạn có thể bật/tắt mod trong **Settings → Data → Mods**.

## 1.1 Mod dạng `.zip` (tự cache)

Bạn có thể thả trực tiếp một tệp `.zip` vào `mods/`. Trình nạp sẽ:

- Kiểm tra trong zip có `mod.json` hay không.
- Tự giải nén vào `mods/.cache/<ten-zip>/`.
- Load mod từ thư mục cache đó.

Thư mục cache sẽ chứa đủ:

- `mod.json`
- các `*.json` cấu hình
- thư mục `assets/` hoặc sprite
- file `.jar` (nếu có code mod)

Khi zip thay đổi (dựa vào `lastModified` và `length`), cache sẽ tự làm mới.
Nếu cần xóa cache, dùng nút **Clear Cache** trong menu Mods.

Trên Android, bạn có thể dùng nút **Import** trong menu Mods để chọn file zip từ bộ nhớ thiết bị.

## 2. Cấu trúc modpack tối thiểu

Mỗi mod là một thư mục con trong `mods/`:

```
mods/
  my-mod/
    mod.json
    items.json
```

### 2.1 `mod.json`

Ví dụ:

```json
{
  "id": "my-mod",
  "name": "My Mod Pack",
  "version": "1.0.0",
  "description": "Mô tả mod ngắn gọn.",
  "author": "Your Name",
  "homepage": "https://example.com",
  "icon": "icon.png",
  "api_version": 1,
  "min_game_version": 0,
  "max_game_version": 0,
  "items": "items.json",
  "entrypoint": "com.my.mod.MyMod",
  "jar": "mod.jar",
  "enabled": true
}
```

### 2.2 `items.json`

Ví dụ:

```json
{
  "items": [
    {
      "id": "my-mod:healing_leaf",
      "name": "Healing Leaf",
      "desc": "Hồi 10 HP ngay lập tức.",
      "category": "POTION",
      "sprite": 241,
      "sprite_path": "sprites/healing_leaf.png",
      "sprite_x": 0,
      "sprite_y": 0,
      "sprite_w": 16,
      "sprite_h": 16,
      "stackable": true,
      "value": 20,
      "spawn_weight": 1.0,
      "use_time": 1.0,
      "use": {
        "type": "HEAL",
        "amount": 10,
        "message": "Bạn cảm thấy khỏe hơn."
      }
    }
  ]
}
```

## 3. Trường dữ liệu hỗ trợ

### Trường chính

- `id`: bắt buộc, duy nhất toàn bộ modpack (dạng `modid:item`).
- `name`: tên hiển thị.
- `desc`: mô tả.
- `category`: bảng rơi (xem phần 4).
- `item_type`: `ITEM`, `MELEE_WEAPON`, `MISSILE_WEAPON`, `ARMOR`.
- `sprite`: chỉ số sprite trong `ItemSpriteSheet` (dùng sprite có sẵn).
- `sprite_path`: đường dẫn file sprite ngoài (tương đối từ thư mục mod).
- `sprite_x`, `sprite_y`, `sprite_w`, `sprite_h`: vùng cắt nếu sprite là spritesheet.
- `stackable`: có cộng dồn hay không.
- `quantity`: số lượng ban đầu (chỉ áp dụng nếu `stackable=true`).
- `value`: giá trị vàng cơ bản.
- `spawn_weight`: trọng số rơi tương đối trong category.
- `use_time`: thời gian tiêu hao khi dùng.

### `use` (hiệu ứng khi dùng)

- `type`: `HEAL`, `SATIETY`, `BUFF`.
- `amount`: số lượng cho `HEAL` hoặc `SATIETY`.
- `buff_class`: tên class buff (vd: `com.shatteredpixel.citnutpixeldungeon.actors.buffs.Haste`).
- `duration`: thời gian buff (nếu có).
- `message`: thông báo hiển thị khi dùng.

### Trường cho vũ khí (MELEE_WEAPON)

- `tier`: tier vũ khí (1-6).
- `internal_tier`: tier nội bộ (mặc định = `tier`).
- `accuracy`: hệ số chính xác (mặc định 1.0).
- `delay`: hệ số tốc độ đánh (mặc định 1.0).
- `reach`: tầm đánh (mặc định 1).
- `min_base`, `max_base`: sát thương cơ bản.
- `min_scale`, `max_scale`: sát thương tăng theo level.
- `str_req`: STR yêu cầu (nếu muốn override).

### Trường cho giáp (ARMOR)

- `tier`: tier giáp (1-5+).
- `str_req`: STR yêu cầu (nếu muốn override).

### Trường cho vũ khí ném (MISSILE_WEAPON)

- `tier`, `internal_tier`, `accuracy`, `delay`, `reach`.
- `min_base`, `max_base`, `min_scale`, `max_scale`, `str_req`.
- `missile_base_uses`: số lượt dùng cơ bản.
- `missile_durability`: độ bền ban đầu.
- `missile_sticky`: có rơi lại sau khi ném.

## 4. Category hợp lệ

`Generator.Category` hiện có thể dùng trực tiếp, ví dụ:

- `FOOD`, `POTION`, `SCROLL`, `SEED`, `STONE`, `TRINKET`, `ARTIFACT`, `RING`, `WAND`, `GOLD`
- `WEAPON`, `WEP_T1` ... `WEP_T6` (vũ khí cận chiến)
- `ARMOR`

## 5. Code mod (desktop)

Code mod chạy **chỉ trên desktop**, cho phép đăng ký item bằng Java giống ý tưởng Fabric.

Yêu cầu trong `mod.json`:

- `jar`: file jar chứa code mod.
- `entrypoint`: class implements `com.shatteredpixel.citnutpixeldungeon.mod.GameMod`.

Ví dụ code:

```java
public class MyMod implements GameMod {
    @Override
    public String id() { return "my-mod"; }

    @Override
    public void onRegister(ModRegistry registry) {
        ModItemDef def = registry.newItem("my-mod:steel_baton", Generator.Category.WEP_T2);
        def.itemType = ModItemDef.ItemType.MELEE_WEAPON;
        def.tier = 2;
        def.minBase = 4;
        def.maxBase = 10;
        def.minScale = 1;
        def.maxScale = 2;
        def.name = "Steel Baton";
        def.desc = "Gậy thép nặng.";
        registry.register(def);
    }
}
```

## 6. Lưu ý & giới hạn

- Sprite ngoài dùng `sprite_path` trong mod, hiện là ảnh tĩnh (không hỗ trợ animation).
- Mod item vẫn tuân thủ hệ thống cân bằng hiện tại, nên hãy test kỹ trước khi phát hành.
- Bật/tắt mod được lưu ở file `mod.enabled` trong thư mục mod (hoặc trong cache nếu là zip), không sửa trực tiếp `mod.json`.

## 7. Tài liệu API và công cụ

- API chi tiết: `docs/mod_api.md`
- Công cụ đóng gói: `tools/modkit/` (xem `docs/modkit.md`)

---

Nếu bạn muốn mở rộng phạm vi, mình sẽ bổ sung loader/registry tương ứng.
