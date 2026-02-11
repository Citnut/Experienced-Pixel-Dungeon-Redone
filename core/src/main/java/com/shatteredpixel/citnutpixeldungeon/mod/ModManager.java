package com.shatteredpixel.citnutpixeldungeon.mod;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.shatteredpixel.citnutpixeldungeon.items.Generator;
import com.shatteredpixel.citnutpixeldungeon.items.Item;
import com.shatteredpixel.citnutpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.FileUtils;
import com.watabou.utils.Random;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class ModManager {
	private static final String MODS_DIR = "mods";
	private static final String MODS_CACHE_DIR = ".cache";
	private static final String MOD_FILE = "mod.json";
	private static final String MOD_STATE_FILE = "mod.enabled";
	private static final String MOD_CACHE_META = "mod.cache";
	private static final int MOD_API_VERSION = 1;

	public static final int DELETE_FAILED = 0;
	public static final int DELETE_OK = 1;
	public static final int DELETE_CACHE_ONLY = 2;

	private static final HashMap<String, ModItemDef> itemsById = new HashMap<>();
	private static final HashMap<String, ModItemFactory> factoriesById = new HashMap<>();
	private static final EnumMap<Generator.Category, ArrayList<ModEntry>> itemsByCategory =
			new EnumMap<>(Generator.Category.class);
	private static boolean loaded = false;

	private ModManager() {}

	public static class ModInfo {
		public String id;
		public String name;
		public String version;
		public boolean enabled;
		public FileHandle manifest;
		public FileHandle modDir;

		public String meta() {
			if (version == null || version.isEmpty()) {
				return id;
			}
			return id + "  v" + version;
		}
	}

	public static class ModData {
		public ModInfo info;
		public String description;
		public String author;
		public String homepage;
		public FileHandle icon;
		public ArrayList<ModItemDef> items = new ArrayList<>();
	}

	public static void load() {
		if (loaded) return;
		loaded = true;

		itemsById.clear();
		factoriesById.clear();
		itemsByCategory.clear();

		FileHandle modsRoot = FileUtils.getFileHandle(MODS_DIR);
		if (modsRoot == null) return;
		if (!modsRoot.exists()) {
			modsRoot.mkdirs();
			return;
		}

		for (FileHandle modDir : discoverModRoots(modsRoot)) {
			FileHandle manifest = modDir.child(MOD_FILE);
			if (!manifest.exists()) continue;

			try {
				loadMod(modDir, manifest);
			} catch (Exception e) {
				DeviceCompat.log("ModManager", "Failed to load mod in " + modDir.path());
				Game.reportException(e);
			}
		}
	}

	public static void reload() {
		loaded = false;
		load();
	}

	public static ArrayList<ModInfo> listMods() {
		ArrayList<ModInfo> result = new ArrayList<>();

		FileHandle modsRoot = FileUtils.getFileHandle(MODS_DIR);
		if (modsRoot == null) return result;
		if (!modsRoot.exists()) {
			modsRoot.mkdirs();
			return result;
		}

		for (FileHandle modDir : discoverModRoots(modsRoot)) {
			FileHandle manifest = modDir.child(MOD_FILE);
			if (!manifest.exists()) continue;

			try {
				JsonValue obj = new JsonReader().parse(manifest);
				ModInfo info = new ModInfo();
				info.id = obj.getString("id", modDir.name());
				info.name = obj.getString("name", info.id);
				info.version = obj.getString("version", "");
				info.manifest = manifest;
				info.modDir = modDir;

				Boolean override = readEnabledOverride(modDir);
				boolean enabled = obj.getBoolean("enabled", true);
				if (override != null) {
					enabled = override;
				}
				info.enabled = enabled;

				result.add(info);
			} catch (Exception e) {
				DeviceCompat.log("ModManager", "Failed to read mod.json in " + modDir.path());
			}
		}

		return result;
	}

	public static ModData loadModData(ModInfo info) {
		if (info == null || info.modDir == null) return null;
		return loadModData(info.modDir);
	}

	public static ModData loadModData(FileHandle modDir) {
		if (modDir == null) return null;
		FileHandle manifest = modDir.child(MOD_FILE);
		if (!manifest.exists()) return null;

		ModData data = new ModData();
		try {
			JsonValue root = new JsonReader().parse(manifest);
			ModInfo info = new ModInfo();
			info.id = root.getString("id", modDir.name());
			info.name = root.getString("name", info.id);
			info.version = root.getString("version", "");
			info.manifest = manifest;
			info.modDir = modDir;
			info.enabled = root.getBoolean("enabled", true);

			Boolean override = readEnabledOverride(modDir);
			if (override != null) {
				info.enabled = override;
			}

			data.info = info;
			data.description = root.getString("description", "");
			data.author = root.getString("author", "");
			data.homepage = root.getString("homepage", "");

			String iconPath = root.getString("icon", null);
			if (iconPath != null && !iconPath.trim().isEmpty()) {
				FileHandle iconFile = modDir.child(iconPath.trim());
				if (iconFile.exists()) {
					data.icon = iconFile;
				}
			}

			JsonValue itemsNode = resolveItemsNode(root, modDir);
			if (itemsNode != null) {
				JsonValue itemsArray = itemsNode.has("items") ? itemsNode.get("items") : itemsNode;
				if (itemsArray != null && itemsArray.isArray()) {
					for (JsonValue itemNode = itemsArray.child; itemNode != null; itemNode = itemNode.next) {
						ModItemDef def = parseItem(info.id, modDir, itemNode);
						if (def != null) {
							data.items.add(def);
						}
					}
				}
			}

		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to read mod data in " + modDir.path());
			Game.reportException(e);
			return null;
		}
		return data;
	}

	public static boolean setEnabled(ModInfo info, boolean enabled) {
		if (info == null || info.modDir == null) return false;
		return writeEnabledOverride(info.modDir, enabled);
	}

	public static boolean importPath(String absolutePath) {
		if (absolutePath == null || absolutePath.trim().isEmpty()) return false;
		FileHandle handle = Gdx.files.absolute(absolutePath.trim());
		return importFile(handle) != null;
	}

	public static FileHandle importFile(FileHandle src) {
		if (src == null || !src.exists()) return null;
		FileHandle modsRoot = FileUtils.getFileHandle(MODS_DIR);
		if (modsRoot == null) return null;
		if (!modsRoot.exists()) modsRoot.mkdirs();

		try {
			File srcFile = src.file();
			File modsDir = modsRoot.file();
			if (srcFile != null && modsDir != null) {
				String srcPath = srcFile.getCanonicalPath();
				String modsPath = modsDir.getCanonicalPath();
				if (srcPath.startsWith(modsPath + File.separator)) {
					return src;
				}
			}
		} catch (Exception ignored) {
		}

		FileHandle dest = modsRoot.child(src.name());
		if (dest.exists()) {
			String base = src.nameWithoutExtension();
			String ext = src.extension();
			int i = 1;
			while (dest.exists()) {
				String candidate = base + "-" + i + (ext.isEmpty() ? "" : "." + ext);
				dest = modsRoot.child(candidate);
				i++;
			}
		}

		try {
			src.copyTo(dest);
			return dest;
		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to import: " + src.path());
			Game.reportException(e);
			return null;
		}
	}

	public static void clearCache() {
		FileHandle modsRoot = FileUtils.getFileHandle(MODS_DIR);
		if (modsRoot == null) return;
		FileHandle cacheRoot = modsRoot.child(MODS_CACHE_DIR);
		deleteRecursively(cacheRoot);
	}

	public static int deleteMod(ModInfo info) {
		if (info == null || info.modDir == null) return DELETE_FAILED;
		FileHandle modDir = info.modDir;

		FileHandle cacheRoot = findCacheRoot(modDir);
		if (cacheRoot != null) {
			FileHandle source = getCacheSource(cacheRoot);
			boolean sourceDeleted = false;
			if (source != null && source.exists()) {
				sourceDeleted = source.delete();
			}
			deleteRecursively(cacheRoot);
			if (source != null && source.exists() && !sourceDeleted) {
				return DELETE_CACHE_ONLY;
			}
			return DELETE_OK;
		}

		if (modDir.exists()) {
			deleteRecursively(modDir);
			return modDir.exists() ? DELETE_FAILED : DELETE_OK;
		}
		return DELETE_FAILED;
	}

	public static ModItemDef getItem(String id) {
		return itemsById.get(id);
	}

	public static Item rollItem(Generator.Category category, float baseWeight) {
		ArrayList<ModEntry> entries = itemsByCategory.get(category);
		if (entries == null || entries.isEmpty()) return null;

		float modTotal = 0f;
		for (ModEntry entry : entries) {
			modTotal += Math.max(0f, entry.weight);
		}
		if (modTotal <= 0f) return null;

		int pickIndex;
		if (baseWeight <= 0f) {
			float[] weights = new float[entries.size()];
			for (int i = 0; i < entries.size(); i++) {
				weights[i] = Math.max(0f, entries.get(i).weight);
			}
			pickIndex = Random.chances(weights);
			if (pickIndex == -1) return null;
			return createItem(entries.get(pickIndex));
		}

		float[] weights = new float[entries.size() + 1];
		weights[0] = baseWeight;
		for (int i = 0; i < entries.size(); i++) {
			weights[i + 1] = Math.max(0f, entries.get(i).weight);
		}

		pickIndex = Random.chances(weights);
		if (pickIndex <= 0) return null;
		return createItem(entries.get(pickIndex - 1));
	}

	private static void loadMod(FileHandle modDir, FileHandle manifest) {
		JsonValue root = new JsonReader().parse(manifest);
		int apiVersion = root.getInt("api_version", MOD_API_VERSION);
		if (apiVersion != MOD_API_VERSION) {
			DeviceCompat.log("ModManager", "Unsupported mod api version " + apiVersion + " in " + modDir.path());
			return;
		}
		int minVersion = root.getInt("min_game_version", -1);
		int maxVersion = root.getInt("max_game_version", -1);
		if (Game.versionCode > 0) {
			if (minVersion > 0 && Game.versionCode < minVersion) {
				DeviceCompat.log("ModManager", "Mod requires newer game version: " + modDir.path());
				return;
			}
			if (maxVersion > 0 && Game.versionCode > maxVersion) {
				DeviceCompat.log("ModManager", "Mod requires older game version: " + modDir.path());
				return;
			}
		}

		Boolean override = readEnabledOverride(modDir);
		boolean enabled = root.getBoolean("enabled", true);
		if (override != null) {
			enabled = override;
		}
		if (!enabled) {
			return;
		}

		String modId = root.getString("id", modDir.name());
		String modName = root.getString("name", modId);
		ModRegistry registry = new ModRegistry(modId);

		loadCodeMod(modDir, root, registry);

		JsonValue itemsNode = resolveItemsNode(root, modDir);

		if (itemsNode != null) {
			JsonValue itemsArray = itemsNode.has("items") ? itemsNode.get("items") : itemsNode;
			if (itemsArray == null || !itemsArray.isArray()) {
				DeviceCompat.log("ModManager", "Invalid items format for mod " + modName);
				return;
			}

			for (JsonValue itemNode = itemsArray.child; itemNode != null; itemNode = itemNode.next) {
				ModItemDef def = parseItem(modId, modDir, itemNode);
				if (def == null) continue;

				if (!registry.register(def)) {
					DeviceCompat.log("ModManager", "Duplicate or invalid mod item id: " + def.id);
				}
			}
		}
	}

	public static boolean registerItem(ModItemDef def) {
		if (!validateItem(def)) return false;
		if (itemsById.containsKey(def.id) || factoriesById.containsKey(def.id)) {
			return false;
		}
		itemsById.put(def.id, def);
		ModEntry entry = new ModEntry();
		entry.def = def;
		entry.weight = Math.max(0f, def.spawnWeight);
		itemsByCategory.computeIfAbsent(def.category, k -> new ArrayList<>()).add(entry);
		return true;
	}

	public static boolean registerFactory(String id, Generator.Category category, float weight, ModItemFactory factory) {
		if (id == null || id.trim().isEmpty()) return false;
		if (category == null || factory == null) return false;
		if (itemsById.containsKey(id) || factoriesById.containsKey(id)) return false;

		ModEntry entry = new ModEntry();
		entry.factory = factory;
		entry.weight = Math.max(0f, weight);
		itemsByCategory.computeIfAbsent(category, k -> new ArrayList<>()).add(entry);
		factoriesById.put(id, factory);
		return true;
	}

	private static boolean validateItem(ModItemDef def) {
		if (def == null) return false;
		if (def.id == null || def.id.trim().isEmpty()) return false;
		if (def.modId == null || def.modId.trim().isEmpty()) return false;
		if (def.category == null) return false;
		if (def.itemType == null) def.itemType = ModItemDef.ItemType.ITEM;

		if (def.tier <= 0) def.tier = 1;
		if (def.internalTier <= 0) def.internalTier = def.tier;
		if (def.quantity <= 0) def.quantity = 1;

		return true;
	}

	private static ModItemDef parseItem(String modId, FileHandle modDir, JsonValue itemNode) {
		String id = itemNode.getString("id", null);
		if (id == null || id.trim().isEmpty()) {
			DeviceCompat.log("ModManager", "Skipping item with missing id");
			return null;
		}

		ModItemDef def = new ModItemDef();
		def.modId = modId;
		def.id = id;
		def.name = itemNode.getString("name", id);
		def.desc = itemNode.getString("desc", "");

		String catStr = itemNode.getString("category", "FOOD");
		Generator.Category cat = parseCategory(catStr);
		if (cat == null) {
			DeviceCompat.log("ModManager", "Invalid category for item " + id + ": " + catStr);
			return null;
		}
		def.category = cat;

		def.sprite = itemNode.getInt("sprite", ItemSpriteSheet.SOMETHING);
		String spritePath = itemNode.getString("sprite_path", null);
		if (spritePath != null && !spritePath.trim().isEmpty()) {
			FileHandle spriteFile = modDir.child(spritePath.trim());
			if (spriteFile.exists()) {
				def.spriteFile = spriteFile;
				def.spriteX = itemNode.getInt("sprite_x", 0);
				def.spriteY = itemNode.getInt("sprite_y", 0);
				def.spriteW = itemNode.getInt("sprite_w", 0);
				def.spriteH = itemNode.getInt("sprite_h", 0);
			} else {
				DeviceCompat.log("ModManager", "Missing sprite file: " + spriteFile.path());
			}
		}
		String typeStr = itemNode.getString("item_type", "ITEM").trim().toUpperCase();
		try {
			def.itemType = ModItemDef.ItemType.valueOf(typeStr);
		} catch (IllegalArgumentException e) {
			def.itemType = ModItemDef.ItemType.ITEM;
		}

		boolean stackableSpecified = itemNode.has("stackable");
		def.stackable = itemNode.getBoolean("stackable", false);
		if (!stackableSpecified && def.itemType == ModItemDef.ItemType.MISSILE_WEAPON) {
			def.stackable = true;
		}
		def.value = itemNode.getLong("value", 0);
		def.spawnWeight = itemNode.getFloat("spawn_weight", 1f);
		def.useTime = itemNode.getFloat("use_time", 1f);
		def.quantity = itemNode.getLong("quantity", def.quantity);

		def.tier = itemNode.getInt("tier", def.tier);
		def.internalTier = itemNode.getInt("internal_tier", def.internalTier);
		def.accuracy = itemNode.getFloat("accuracy", def.accuracy);
		def.delay = itemNode.getFloat("delay", def.delay);
		def.reach = itemNode.getInt("reach", def.reach);
		def.minBase = itemNode.getLong("min_base", def.minBase);
		def.maxBase = itemNode.getLong("max_base", def.maxBase);
		def.minScale = itemNode.getLong("min_scale", def.minScale);
		def.maxScale = itemNode.getLong("max_scale", def.maxScale);
		def.strReq = itemNode.getLong("str_req", def.strReq);
		def.missileBaseUses = itemNode.getFloat("missile_base_uses", def.missileBaseUses);
		def.missileDurability = itemNode.getFloat("missile_durability", def.missileDurability);
		def.missileSticky = itemNode.getBoolean("missile_sticky", def.missileSticky);

		JsonValue useNode = itemNode.get("use");
		if (useNode != null) {
			def.use = parseUse(useNode);
		}

		return def;
	}

	private static ModItemDef.UseEffect parseUse(JsonValue useNode) {
		String typeStr = useNode.getString("type", "").trim().toUpperCase();
		if (typeStr.isEmpty()) return null;

		ModItemDef.UseEffect effect = new ModItemDef.UseEffect();
		try {
			effect.type = ModItemDef.UseEffect.Type.valueOf(typeStr);
		} catch (IllegalArgumentException e) {
			DeviceCompat.log("ModManager", "Unknown use type: " + typeStr);
			return null;
		}

		effect.amount = useNode.getFloat("amount", 0f);
		effect.duration = useNode.getFloat("duration", 0f);
		effect.buffClass = useNode.getString("buff_class", null);
		effect.message = useNode.getString("message", null);
		return effect;
	}

	private static Generator.Category parseCategory(String catStr) {
		if (catStr == null) return null;
		try {
			return Generator.Category.valueOf(catStr.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private static ArrayList<FileHandle> discoverModRoots(FileHandle modsRoot) {
		ArrayList<FileHandle> result = new ArrayList<>();
		FileHandle[] children = modsRoot.list();
		if (children == null) return result;

		for (FileHandle child : children) {
			if (!child.isDirectory()) continue;
			if (MODS_CACHE_DIR.equals(child.name())) continue;
			if (child.child(MOD_FILE).exists()) {
				result.add(child);
			}
		}

		FileHandle cacheRoot = modsRoot.child(MODS_CACHE_DIR);
		for (FileHandle child : children) {
			if (child.isDirectory()) continue;
			if (!"zip".equalsIgnoreCase(child.extension())) continue;
			FileHandle modDir = prepareZipMod(child, cacheRoot);
			if (modDir != null) {
				result.add(modDir);
			}
		}

		return result;
	}

	private static FileHandle prepareZipMod(FileHandle zipFile, FileHandle cacheRoot) {
		ZipModInfo info;
		try {
			info = inspectZip(zipFile);
		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to read zip: " + zipFile.path());
			Game.reportException(e);
			return null;
		}
		if (info == null || info.manifestPath == null) {
			DeviceCompat.log("ModManager", "Zip is missing " + MOD_FILE + ": " + zipFile.name());
			return null;
		}

		if (!cacheRoot.exists()) {
			cacheRoot.mkdirs();
		}

		String cacheName = sanitizeCacheName(zipFile.nameWithoutExtension());
		FileHandle cacheDir = cacheRoot.child(cacheName);
		FileHandle rootDir = resolveRoot(cacheDir, info.rootPath);

		boolean needsExtract = !isCacheValid(cacheDir, zipFile, info.manifestPath);
		if (needsExtract) {
			Boolean previousEnabled = readEnabledOverride(rootDir);
			deleteRecursively(cacheDir);
			cacheDir.mkdirs();
			try {
				extractZip(zipFile, cacheDir);
			} catch (Exception e) {
				DeviceCompat.log("ModManager", "Failed to extract zip: " + zipFile.path());
				Game.reportException(e);
				return null;
			}
			rootDir = resolveRoot(cacheDir, info.rootPath);
			if (previousEnabled != null) {
				writeEnabledOverride(rootDir, previousEnabled);
			}
			writeCacheMeta(cacheDir, zipFile, info.manifestPath);
		}

		FileHandle manifest = rootDir.child(MOD_FILE);
		if (!manifest.exists()) {
			DeviceCompat.log("ModManager", "Cached mod missing " + MOD_FILE + ": " + rootDir.path());
			return null;
		}
		return rootDir;
	}

	private static ZipModInfo inspectZip(FileHandle zipFile) throws Exception {
		if (zipFile == null || zipFile.file() == null) return null;
		if (!zipFile.exists()) return null;

		String bestManifest = null;
		int bestDepth = Integer.MAX_VALUE;
		try (ZipFile zip = new ZipFile(zipFile.file())) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				if (entry.isDirectory()) continue;
				String name = normalizeZipPath(entry.getName());
				if (name == null) continue;
				if (name.equals(MOD_FILE) || name.endsWith("/" + MOD_FILE)) {
					int depth = countPathSegments(name);
					if (depth < bestDepth) {
						bestDepth = depth;
						bestManifest = name;
					}
				}
			}
		}

		if (bestManifest == null) return null;

		ZipModInfo info = new ZipModInfo();
		info.manifestPath = bestManifest;
		if (bestManifest.equals(MOD_FILE)) {
			info.rootPath = "";
		} else {
			info.rootPath = bestManifest.substring(0, bestManifest.length() - MOD_FILE.length());
			if (info.rootPath.endsWith("/")) {
				info.rootPath = info.rootPath.substring(0, info.rootPath.length() - 1);
			}
		}
		return info;
	}

	private static void extractZip(FileHandle zipFile, FileHandle cacheDir) throws Exception {
		if (zipFile == null || !zipFile.exists()) return;
		if (!cacheDir.exists()) cacheDir.mkdirs();

		File cacheRoot = cacheDir.file();
		String basePath = cacheRoot.getCanonicalPath() + File.separator;

		try (ZipFile zip = new ZipFile(zipFile.file())) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String name = normalizeZipPath(entry.getName());
				if (name == null || name.isEmpty()) continue;
				if (name.startsWith("/") || name.startsWith("\\")) continue;

				FileHandle out = cacheDir.child(name);
				File outFile = out.file();
				String outPath = outFile.getCanonicalPath();
				if (!outPath.startsWith(basePath)) {
					DeviceCompat.log("ModManager", "Blocked suspicious zip entry: " + name);
					continue;
				}

				if (entry.isDirectory()) {
					out.mkdirs();
					continue;
				}

				out.parent().mkdirs();
				try (InputStream in = zip.getInputStream(entry)) {
					out.write(in, false);
				}
			}
		}
	}

	private static String sanitizeCacheName(String name) {
		if (name == null) return "mod";
		String cleaned = name.replaceAll("[^a-zA-Z0-9._-]", "_");
		if (cleaned.isEmpty()) cleaned = "mod";
		return cleaned;
	}

	private static String normalizeZipPath(String path) {
		if (path == null) return null;
		return path.replace("\\", "/");
	}

	private static int countPathSegments(String path) {
		if (path == null || path.isEmpty()) return 0;
		int count = 1;
		for (int i = 0; i < path.length(); i++) {
			if (path.charAt(i) == '/') count++;
		}
		return count;
	}

	private static FileHandle resolveRoot(FileHandle cacheDir, String rootPath) {
		if (rootPath == null || rootPath.isEmpty()) {
			return cacheDir;
		}
		return cacheDir.child(rootPath);
	}

	private static boolean isCacheValid(FileHandle cacheDir, FileHandle zipFile, String manifestPath) {
		FileHandle meta = cacheDir.child(MOD_CACHE_META);
		if (!meta.exists()) return false;
		try (InputStream in = meta.read()) {
			Properties props = new Properties();
			props.load(in);
			long lastModified = Long.parseLong(props.getProperty("source_last_modified", "-1"));
			long length = Long.parseLong(props.getProperty("source_length", "-1"));
			String cachedManifest = props.getProperty("manifest_path", "");
			if (lastModified != zipFile.lastModified()) return false;
			if (length != zipFile.length()) return false;
			return manifestPath != null && manifestPath.equals(cachedManifest);
		} catch (Exception e) {
			return false;
		}
	}

	private static void writeCacheMeta(FileHandle cacheDir, FileHandle zipFile, String manifestPath) {
		FileHandle meta = cacheDir.child(MOD_CACHE_META);
		Properties props = new Properties();
		props.setProperty("source_last_modified", Long.toString(zipFile.lastModified()));
		props.setProperty("source_length", Long.toString(zipFile.length()));
		props.setProperty("manifest_path", manifestPath == null ? "" : manifestPath);
		try {
			if (zipFile.file() != null) {
				props.setProperty("source_path", zipFile.file().getAbsolutePath());
			}
		} catch (Exception ignored) {
		}
		try {
			meta.parent().mkdirs();
			meta.writeString(toPropertiesString(props), false, "UTF-8");
		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to write cache meta for " + zipFile.name());
		}
	}

	private static String toPropertiesString(Properties props) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String key : props.stringPropertyNames()) {
			sb.append(key).append('=').append(props.getProperty(key)).append('\n');
		}
		return sb.toString();
	}

	private static void deleteRecursively(FileHandle handle) {
		if (handle == null || !handle.exists()) return;
		if (handle.isDirectory()) {
			for (FileHandle child : handle.list()) {
				deleteRecursively(child);
			}
		}
		handle.delete();
	}

	private static FileHandle findCacheRoot(FileHandle modDir) {
		FileHandle current = modDir;
		for (int i = 0; i < 8 && current != null; i++) {
			FileHandle meta = current.child(MOD_CACHE_META);
			if (meta.exists()) return current;
			if (current.parent() == null) break;
			current = current.parent();
		}
		return null;
	}

	private static FileHandle getCacheSource(FileHandle cacheRoot) {
		if (cacheRoot == null) return null;
		FileHandle meta = cacheRoot.child(MOD_CACHE_META);
		if (!meta.exists()) return null;
		try (InputStream in = meta.read()) {
			Properties props = new Properties();
			props.load(in);
			String sourcePath = props.getProperty("source_path", null);
			if (sourcePath == null || sourcePath.trim().isEmpty()) return null;
			return Gdx.files.absolute(sourcePath.trim());
		} catch (Exception e) {
			return null;
		}
	}

	private static Boolean readEnabledOverride(FileHandle modDir) {
		if (modDir == null) return null;
		FileHandle state = enabledOverrideFile(modDir);
		if (!state.exists()) return null;
		try {
			String text = state.readString("UTF-8").trim();
			if (text.isEmpty()) return null;
			return Boolean.parseBoolean(text);
		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to read " + MOD_STATE_FILE + " in " + modDir.path());
			return null;
		}
	}

	private static boolean writeEnabledOverride(FileHandle modDir, boolean enabled) {
		if (modDir == null) return false;
		FileHandle state = enabledOverrideFile(modDir);
		try {
			modDir.mkdirs();
			state.writeString(Boolean.toString(enabled), false, "UTF-8");
			return true;
		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to write " + MOD_STATE_FILE + " in " + modDir.path());
			return false;
		}
	}

	private static FileHandle enabledOverrideFile(FileHandle modDir) {
		return modDir.child(MOD_STATE_FILE);
	}

	public static Item createItemPreview(ModItemDef def) {
		return createItem(def);
	}

	private static Item createItem(ModEntry entry) {
		if (entry == null) return null;
		if (entry.factory != null) {
			try {
				return entry.factory.create();
			} catch (Exception e) {
				DeviceCompat.log("ModManager", "Factory failed to create item");
				Game.reportException(e);
				return null;
			}
		}
		return createItem(entry.def);
	}

	private static Item createItem(ModItemDef def) {
		if (def == null) return null;
		switch (def.itemType) {
			case MELEE_WEAPON:
				return new ModMeleeWeapon(def);
			case MISSILE_WEAPON:
				return new ModMissileWeapon(def);
			case ARMOR:
				return new ModArmor(def);
			case ITEM:
			default:
				return new ModItem(def);
		}
	}

	private static JsonValue resolveItemsNode(JsonValue root, FileHandle modDir) {
		if (root == null || modDir == null) return null;
		JsonValue itemsNode = root.get("items");
		if (itemsNode == null) {
			String itemsPath = root.getString("items_file", null);
			if (itemsPath != null) {
				FileHandle itemsFile = modDir.child(itemsPath);
				if (itemsFile.exists()) {
					itemsNode = new JsonReader().parse(itemsFile);
				}
			}
		} else if (itemsNode.isString()) {
			FileHandle itemsFile = modDir.child(itemsNode.asString());
			if (itemsFile.exists()) {
				itemsNode = new JsonReader().parse(itemsFile);
			}
		}
		return itemsNode;
	}

	private static class ZipModInfo {
		String manifestPath;
		String rootPath;
	}

	private static class ModEntry {
		ModItemDef def;
		ModItemFactory factory;
		float weight;
	}

	private static void loadCodeMod(FileHandle modDir, JsonValue root, ModRegistry registry) {
		if (!DeviceCompat.isDesktop()) return;

		String entrypoint = root.getString("entrypoint", null);
		if (entrypoint == null || entrypoint.trim().isEmpty()) return;

		String jarName = root.getString("jar", null);
		ClassLoader loader = ModManager.class.getClassLoader();
		if (jarName != null && !jarName.trim().isEmpty()) {
			FileHandle jarFile = modDir.child(jarName.trim());
			if (jarFile.exists()) {
				try {
					URL jarUrl = jarFile.file().toURI().toURL();
					loader = new URLClassLoader(new URL[]{jarUrl}, loader);
				} catch (Exception e) {
					DeviceCompat.log("ModManager", "Failed to load jar: " + jarFile.path());
					Game.reportException(e);
					return;
				}
			} else {
				DeviceCompat.log("ModManager", "Jar not found: " + jarFile.path());
				return;
			}
		}

		try {
			Class<?> cls = Class.forName(entrypoint.trim(), true, loader);
			if (!GameMod.class.isAssignableFrom(cls)) {
				DeviceCompat.log("ModManager", "Entrypoint does not implement GameMod: " + entrypoint);
				return;
			}
			GameMod mod = (GameMod) cls.getDeclaredConstructor().newInstance();
			mod.onLoad();
			mod.onRegister(registry);
		} catch (Exception e) {
			DeviceCompat.log("ModManager", "Failed to load entrypoint: " + entrypoint);
			Game.reportException(e);
		}
	}
}
