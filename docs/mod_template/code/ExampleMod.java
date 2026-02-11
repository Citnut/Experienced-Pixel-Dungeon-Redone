package example.mod;

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
