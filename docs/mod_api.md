# Mod API (v1) cho Experienced Pixel Dungeon

Tài liệu này mô tả API mod (data + code mod) và cách đóng gói mod theo chuẩn hiện tại của dự án.

## 1. Tổng quan

Mod có 2 phần:
- Data mod: khai báo item qua `mod.json` + `items.json`.
- Code mod (desktop): class Java implement `GameMod` và được load từ `jar`.

Mod có thể đặt dưới dạng thư mục hoặc `.zip` trong `mods/`.

## 2. Cấu trúc mod tối thiểu

```
mods/
  my-mod/
    mod.json
    items.json
```

Hoặc dạng zip:

```
mods/
  my-mod.zip
```

Khi dùng zip, game sẽ tự giải nén vào `mods/.cache/<ten-zip>/` và chạy từ đó.

## 3. `mod.json`

Ví dụ:

```json
{
  "id": "my-mod",
  "name": "My Mod Pack",
  "version": "1.0.0",
  "api_version": 1,
  "min_game_version": 0,
  "max_game_version": 0,
  "items": "items.json",
  "entrypoint": "com.my.mod.MyMod",
  "jar": "mod.jar",
  "enabled": true
}
```

Giải thích:
- `id`: định danh mod (nên dùng dạng `my-mod`).
- `name`: tên hiển thị.
- `version`: phiên bản mod.
- `description`: mô tả mod.
- `author`: tác giả mod.
- `homepage`: link/thông tin trang chủ (tuỳ chọn).
- `icon`: đường dẫn icon (png) tương đối thư mục mod.
- `api_version`: phiên bản API mod (hiện tại: `1`).
- `min_game_version`: versionCode tối thiểu của game (0 = bỏ qua).
- `max_game_version`: versionCode tối đa của game (0 = bỏ qua).
- `items`: đường dẫn tới file items (tương đối với thư mục mod).
- `entrypoint`: class Java implement `GameMod` (chỉ desktop).
- `jar`: file jar chứa code mod (chỉ desktop).
- `enabled`: bật/tắt mặc định (có thể bị override bởi `mod.enabled`).

## 4. `items.json`

```json
{
  "items": [
    {
      "id": "my-mod:healing_leaf",
      "name": "Healing Leaf",
      "desc": "Hồi 10 HP ngay lập tức.",
      "category": "POTION",
      "sprite": 241,
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

### 4.1 Trường chung
- `id`: bắt buộc, duy nhất toàn bộ mod (gợi ý dạng `modid:item`).
- `name`, `desc`: hiển thị.
- `category`: bảng rơi (xem `Generator.Category`).
- `item_type`: `ITEM`, `MELEE_WEAPON`, `MISSILE_WEAPON`, `ARMOR`.
- `quantity`: số lượng ban đầu (chỉ áp dụng nếu `stackable=true`).
- `sprite`: index trong `ItemSpriteSheet`.
- `sprite_path`: đường dẫn sprite ngoài (tương đối thư mục mod).
- `sprite_x`, `sprite_y`, `sprite_w`, `sprite_h`: khung cắt sprite.
- `stackable`, `value`, `spawn_weight`, `use_time`.

### 4.2 Use effect
- `use.type`: `HEAL`, `SATIETY`, `BUFF`.
- `use.amount`: lượng heal/satiate.
- `use.buff_class`: class buff đầy đủ (vd `com.shatteredpixel.citnutpixeldungeon.actors.buffs.Haste`).
- `use.duration`: thời gian buff.
- `use.message`: thông báo.

### 4.3 Thêm cho vũ khí
- `tier`, `internal_tier`, `accuracy`, `delay`, `reach`.
- `min_base`, `max_base`, `min_scale`, `max_scale`.
- `str_req`.

### 4.4 Thêm cho giáp
- `tier`, `str_req`.

### 4.5 Thêm cho vũ khí ném (MISSILE_WEAPON)

- `item_type`: `MISSILE_WEAPON`
- `tier`, `internal_tier`, `accuracy`, `delay`, `reach`.
- `min_base`, `max_base`, `min_scale`, `max_scale`, `str_req`.
- `missile_base_uses`: số lượt dùng cơ bản (float).
- `missile_durability`: độ bền ban đầu (float).
- `missile_sticky`: có rơi lại sau khi ném (boolean).

## 5. Code mod API

### 5.1 GameMod

```java
public interface GameMod {
    String id();
    default void onLoad() {}
    void onRegister(ModRegistry registry);
}
```

### 5.2 ModRegistry

Các helper mới:
- `modId()`
- `newItem(id, category)`
- `newMeleeWeapon(id, category, tier)`
- `newArmor(id, category, tier)`
- `newMissileWeapon(id, category, tier)`
- `register(def)` / `registerAll(defs...)`
- `registerFactory(id, category, weight, factory)`
- `registerClass(id, category, weight, ItemClass)`

### 5.3 ModItemDef
`ModItemDef` là dữ liệu cho một item. Bạn có thể set các field giống `items.json` và gọi `registry.register(def)`.

## 6. Code mẫu

```java
package com.example;

import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.mod.GameMod;
import com.shatteredpixel.citnutpixeldungeon.mod.ModItemDef;
import com.shatteredpixel.citnutpixeldungeon.mod.ModRegistry;

public class ExampleMod implements GameMod {
    @Override
    public String id() {
        return "example-mod";
    }

    @Override
    public void onRegister(ModRegistry registry) {
        ModItemDef leaf = registry.newItem("example-mod:healing_leaf", Generator.Category.POTION);
        leaf.name = "Healing Leaf";
        leaf.desc = "Hồi 10 HP ngay lập tức.";
        leaf.stackable = true;
        leaf.value = 20;
        leaf.spawnWeight = 1.0f;
        leaf.use = new ModItemDef.UseEffect();
        leaf.use.type = ModItemDef.UseEffect.Type.HEAL;
        leaf.use.amount = 10f;
        leaf.use.message = "Bạn cảm thấy khỏe hơn.";

        ModItemDef baton = registry.newMeleeWeapon("example-mod:steel_baton", Generator.Category.WEP_T2, 2);
        baton.name = "Steel Baton";
        baton.desc = "Gậy thép nặng.";
        baton.minBase = 4;
        baton.maxBase = 10;
        baton.minScale = 1;
        baton.maxScale = 2;

        registry.registerAll(leaf, baton);
    }
}
```

### 6.1 Đăng ký item bằng class (code‑mod)

```java
public class MyMod implements GameMod {
    @Override
    public String id() { return "my-mod"; }

    @Override
    public void onRegister(ModRegistry registry) {
        registry.registerClass(\"my-mod:custom_wand\", Generator.Category.WAND, 1.0f, MyCustomWand.class);
        registry.registerClass(\"my-mod:custom_ring\", Generator.Category.RING, 0.5f, MyCustomRing.class);
        registry.registerClass(\"my-mod:custom_artifact\", Generator.Category.ARTIFACT, 0.2f, MyCustomArtifact.class);
    }
}
```

## 7. Bật/tắt mod

Trạng thái mod được lưu ở file `mod.enabled` trong thư mục mod (hoặc cache nếu là zip). Tắt mod trong menu **Settings > Data > Mods**.

## 8. Gợi ý đóng gói

- Data mod: chỉ cần zip thư mục chứa `mod.json`, `items.json` và assets.
- Code mod: build `mod.jar` và đặt cùng thư mục.

Tool hỗ trợ có sẵn tại `tools/modkit/` (xem thêm `docs/modkit.md`).
Mod zip mẫu: `docs/mod_template_zip.zip`.
