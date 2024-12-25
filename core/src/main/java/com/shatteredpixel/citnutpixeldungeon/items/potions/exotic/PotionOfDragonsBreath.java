/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Experienced Pixel Dungeon
 * Copyright (C) 2019-2024 Trashbox Bobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.citnutpixeldungeon.items.potions.exotic;

import com.shatteredpixel.citnutpixeldungeon.Assets;
import com.shatteredpixel.citnutpixeldungeon.Dungeon;
import com.shatteredpixel.citnutpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.citnutpixeldungeon.actors.Actor;
import com.shatteredpixel.citnutpixeldungeon.actors.Char;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.citnutpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.citnutpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.citnutpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.citnutpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.citnutpixeldungeon.journal.Catalog;
import com.shatteredpixel.citnutpixeldungeon.levels.Level;
import com.shatteredpixel.citnutpixeldungeon.levels.Terrain;
import com.shatteredpixel.citnutpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.citnutpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class PotionOfDragonsBreath extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_DRGBREATH;
	}

	protected static boolean identifiedByUse = false;

	@Override
	//need to override drink so that time isn't spent right away
	protected void drink(final Hero hero) {

		if (!isKnown()) {
			identify();
			curItem = detach( hero.belongings.backpack );
			identifiedByUse = true;
		} else {
			identifiedByUse = false;
		}

		GameScene.selectCell(targeter);
	}
	
	private CellSelector.Listener targeter = new CellSelector.Listener() {

		private boolean showingWindow = false;
		private boolean potionAlreadyUsed = false;

		@Override
		public void onSelect(final Integer cell) {

			if (showingWindow){
				return;
			}
			if (potionAlreadyUsed){
				potionAlreadyUsed = false;
				return;
			}

			if (cell == null && identifiedByUse){
				showingWindow = true;
				ShatteredPixelDungeon.runOnRenderThread(new Callback() {
					@Override
					public void call() {
						GameScene.show( new WndOptions(new ItemSprite(PotionOfDragonsBreath.this),
								Messages.titleCase(name()),
								Messages.get(ExoticPotion.class, "warning"),
								Messages.get(ExoticPotion.class, "yes"),
								Messages.get(ExoticPotion.class, "no") ) {
							@Override
							protected void onSelect( int index ) {
								showingWindow = false;
								switch (index) {
									case 0:
										curUser.spendAndNext(1f);
										identifiedByUse = false;
										break;
									case 1:
										GameScene.selectCell( targeter );
										break;
								}
							}
							public void onBackPressed() {}
						} );
					}
				});
			} else if (cell != null) {
				if (!identifiedByUse) {
					curItem.detach(curUser.belongings.backpack);
				}
				potionAlreadyUsed = true;
				identifiedByUse = false;
				curUser.busy();
				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				curUser.sprite.operate(curUser.pos, new Callback() {
					@Override
					public void call() {

						curUser.sprite.idle();
						curUser.sprite.zap(cell);
						Sample.INSTANCE.play( Assets.Sounds.BURNING );

						final Ballistica bolt = new Ballistica(curUser.pos, cell, Ballistica.WONT_STOP);

						int maxDist = 6;
						int dist = Math.min(bolt.dist, maxDist);

						final ConeAOE cone = new ConeAOE(bolt, 6, 60, Ballistica.STOP_SOLID | Ballistica.STOP_TARGET | Ballistica.IGNORE_SOFT_SOLID );

						//cast to cells at the tip, rather than all cells, better performance.
						for (Ballistica ray : cone.outerRays){
							((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
									MagicMissile.FIRE_CONE,
									curUser.sprite,
									ray.path.get(ray.dist),
									null
							);
						}
						
						MagicMissile.boltFromChar(curUser.sprite.parent,
								MagicMissile.FIRE_CONE,
								curUser.sprite,
								bolt.path.get(dist / 2),
								new Callback() {
									@Override
									public void call() {
										ArrayList<Integer> adjacentCells = new ArrayList<>();
										for (int cell : cone.cells){
											//ignore caster cell
											if (cell == bolt.sourcePos){
												continue;
											}

											//knock doors open
											if (Dungeon.level.map[cell] == Terrain.DOOR){
												Level.set(cell, Terrain.OPEN_DOOR);
												GameScene.updateMap(cell);
											}

											//only ignite cells directly near caster if they are flammable
											if (Dungeon.level.adjacent(bolt.sourcePos, cell) && !Dungeon.level.flamable[cell]){
												adjacentCells.add(cell);
											} else {
												GameScene.add( Blob.seed( cell, 5, Fire.class ) );
											}
											
											Char ch = Actor.findChar( cell );
											if (ch != null) {
												
												Buff.affect( ch, Burning.class ).reignite( ch );
												Buff.affect(ch, Cripple.class, 5f);
											}
										}

										//ignite cells that share a side with an adjacent cell, are flammable, and are further from the source pos
										//This prevents short-range casts not igniting barricades or bookshelves
										for (int cell : adjacentCells){
											for (int i : PathFinder.NEIGHBOURS4){
												if (Dungeon.level.trueDistance(cell+i, bolt.sourcePos) > Dungeon.level.trueDistance(cell, bolt.sourcePos)
														&& Dungeon.level.flamable[cell+i]
														&& Fire.volumeAt(cell+i, Fire.class) == 0){
													GameScene.add( Blob.seed( cell+i, 5, Fire.class ) );
												}
											}
										}

										curUser.spendAndNext(1f);

										if (!anonymous) {
											Catalog.countUse(PotionOfDragonsBreath.class);
											if (Random.Float() < talentChance) {
												Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
											}
										}
									}
								});
						
					}
				});
			}
		}
		
		@Override
		public String prompt() {
			return Messages.get(PotionOfDragonsBreath.class, "prompt");
		}
	};
}
