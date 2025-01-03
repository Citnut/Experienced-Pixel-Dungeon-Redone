/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2019-2024 Evan Debenham
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

package com.shatteredpixel.citnutpixeldungeon.services.news;

import com.shatteredpixel.citnutpixeldungeon.services.news.guideEntries.*;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class IngameNews extends NewsService {

	@Override
	public void checkForArticles(boolean useMetered, boolean forceHTTPS, NewsResultCallback callback) {

		if (!useMetered && !Game.platform.connectedToUnmeteredNetwork()){
			callback.onConnectionFailed();
			return;
		}

		//turn on to test connection failure
		if (false){
			callback.onConnectionFailed();
			return;
		}

		ArrayList<NewsArticle> articles = new ArrayList<>();
		articles.add(new FateLock());
		articles.add(new Bbat());
		articles.add(new PotionsOfExp());
		articles.add(new Bartering());
		articles.add(new ExpGen());
		articles.add(new Fishing());
		articles.add(new TreasureBags());
		articles.add(new Spells());
		articles.add(new Blacky());
		articles.add(new Overhaul());
		articles.add(new Resets());
		articles.add(new Other());

		callback.onArticlesFound(articles);

	}
}
