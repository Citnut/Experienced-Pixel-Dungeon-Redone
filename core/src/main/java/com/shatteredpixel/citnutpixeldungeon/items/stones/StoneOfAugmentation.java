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

package com.shatteredpixel.citnutpixeldungeon.items.stones;

import com.shatteredpixel.citnutpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.items.armor.Armor;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.citnutpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.citnutpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.citnutpixeldungeon.journal.Catalog;
import com.shatteredpixel.citnutpixeldungeon.messages.Messages;
import com.shatteredpixel.citnutpixeldungeon.scenes.GameScene;
import com.shatteredpixel.citnutpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.citnutpixeldungeon.ui.RedButton;
import com.shatteredpixel.citnutpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.citnutpixeldungeon.ui.Window;
import com.shatteredpixel.citnutpixeldungeon.windows.IconTitle;

public class StoneOfAugmentation extends InventoryStone {
	
	{
		preferredBag = Belongings.Backpack.class;
		image = ItemSpriteSheet.STONE_AUGMENTATION;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return ScrollOfEnchantment.enchantable(item);
	}

	@Override
	protected void onItemSelected(Item item) {
		
		GameScene.show(new WndAugment( item));
		
	}
	
	public void apply( Weapon weapon, Weapon.Augment augment ) {
		
		weapon.augment = augment;
		useAnimation();
		ScrollOfUpgrade.upgrade(curUser);
		curItem.detach( curUser.belongings.backpack );
		Catalog.countUse(getClass());
	}
	
	public void apply( Armor armor, Armor.Augment augment ) {
		
		armor.augment = augment;
		useAnimation();
		ScrollOfUpgrade.upgrade(curUser);
		curItem.detach( curUser.belongings.backpack );
		Catalog.countUse(getClass());
	}
	
	@Override
	public long value() {
		return 30 * quantity;
	}

	@Override
	public long energyVal() {
		return 5 * quantity;
	}
	
	public class WndAugment extends Window {
		
		private static final int WIDTH			= 120;
		private static final int MARGIN 		= 2;
		private static final int BUTTON_WIDTH	= WIDTH - MARGIN * 2;
		private static final int BUTTON_HEIGHT	= 20;
		
		public WndAugment( final Item toAugment ) {
			super();
			
			IconTitle titlebar = new IconTitle( toAugment );
			titlebar.setRect( 0, 0, WIDTH, 0 );
			add( titlebar );
			
			RenderedTextBlock tfMesage = PixelScene.renderTextBlock( Messages.get(this, "choice"), 8 );
			tfMesage.maxWidth(WIDTH - MARGIN * 2);
			tfMesage.setPos(MARGIN, titlebar.bottom() + MARGIN);
			add( tfMesage );
			
			float pos = tfMesage.top() + tfMesage.height();
			
			if (toAugment instanceof Weapon){
				for (final Weapon.Augment aug : Weapon.Augment.values()){
					if (((Weapon) toAugment).augment != aug){
						RedButton btnSpeed = new RedButton( Messages.get(this, aug.name()) ) {
							@Override
							protected void onClick() {
								hide();
								StoneOfAugmentation.this.apply( (Weapon)toAugment, aug );
							}
						};
						btnSpeed.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
						add( btnSpeed );
						
						pos = btnSpeed.bottom();
					}
				}
				
			} else if (toAugment instanceof Armor){
				for (final Armor.Augment aug : Armor.Augment.values()){
					if (((Armor) toAugment).augment != aug){
						RedButton btnSpeed = new RedButton( Messages.get(this, aug.name()) ) {
							@Override
							protected void onClick() {
								hide();
								StoneOfAugmentation.this.apply( (Armor) toAugment, aug );
							}
						};
						btnSpeed.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
						add( btnSpeed );
						
						pos = btnSpeed.bottom();
					}
				}
			}
			
			RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
				@Override
				protected void onClick() {
					hide();
					StoneOfAugmentation.this.collect();
				}
			};
			btnCancel.setRect( MARGIN, pos + MARGIN, BUTTON_WIDTH, BUTTON_HEIGHT );
			add( btnCancel );
			
			resize( WIDTH, (int)btnCancel.bottom() + MARGIN );
		}
		
		@Override
		public void onBackPressed() {
			StoneOfAugmentation.this.collect();
			super.onBackPressed();
		}
	}
}
