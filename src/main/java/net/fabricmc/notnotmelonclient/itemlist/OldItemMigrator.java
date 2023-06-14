package net.fabricmc.notnotmelonclient.itemlist;

import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import java.util.Map;

public class OldItemMigrator {
	public final static Map<String, String> MIGRATIONS = Map.ofEntries(
		Map.entry("minecraft:brick_block", "minecraft:bricks"),
		Map.entry("minecraft:deadbush", "minecraft:dead_bush"),
		Map.entry("minecraft:fireworks", "minecraft:firework_rocket"),
		Map.entry("minecraft:firework_charge", "minecraft:firework_star"),
		Map.entry("minecraft:golden_rail", "minecraft:powered_rail"),
		Map.entry("minecraft:hardened_clay", "minecraft:terracotta"),
		Map.entry("minecraft:lit_pumpkin", "minecraft:jack_o_lantern"),
		Map.entry("minecraft:melon_block", "minecraft:melon"),
		Map.entry("minecraft:mob_spawner", "minecraft:spawner"),
		Map.entry("minecraft:monster_egg", "minecraft:infested_stone_bricks"),
		Map.entry("minecraft:netherbrick", "minecraft:nether_brick"),
		Map.entry("minecraft:noteblock", "minecraft:note_block"),
		Map.entry("minecraft:quartz_ore", "minecraft:nether_quartz_ore"),
		Map.entry("minecraft:reeds", "minecraft:sugar_cane"),
		Map.entry("minecraft:slime", "minecraft:slime_block"),
		Map.entry("minecraft:snow_layer", "minecraft:snow"),
		Map.entry("minecraft:speckled_melon", "minecraft:glistering_melon_slice"),
		Map.entry("minecraft:stained_hardened_clay", "minecraft:terracotta"),
		Map.entry("minecraft:stone_slab2", "minecraft:red_sandstone_slab"),
		Map.entry("minecraft:stone_stairs", "minecraft:cobblestone_stairs"),
		Map.entry("minecraft:tallgrass", "minecraft:grass"),
		Map.entry("minecraft:waterlily", "minecraft:lily_pad"),
		Map.entry("minecraft:web", "minecraft:cobweb"),
		Map.entry("minecraft:yellow_flower", "minecraft:dandelion")
	);
	
	public final static Map<Integer, String> SPAWN_EGGS = Map.ofEntries(
		Map.entry(50, "minecraft:creeper_spawn_egg"),
		Map.entry(51, "minecraft:skeleton_spawn_egg"),
		Map.entry(52, "minecraft:spider_spawn_egg"),
		Map.entry(54, "minecraft:zombie_spawn_egg"),
		Map.entry(55, "minecraft:slime_spawn_egg"),
		Map.entry(56, "minecraft:ghast_spawn_egg"),
		Map.entry(57, "minecraft:zombified_piglin_spawn_egg"),
		Map.entry(58, "minecraft:enderman_spawn_egg"),
		Map.entry(59, "minecraft:cave_spider_spawn_egg"),
		Map.entry(60, "minecraft:silverfish_spawn_egg"),
		Map.entry(61, "minecraft:blaze_spawn_egg"),
		Map.entry(62, "minecraft:magma_cube_spawn_egg"),
		Map.entry(65, "minecraft:bat_spawn_egg"),
		Map.entry(66, "minecraft:witch_spawn_egg"),
		Map.entry(67, "minecraft:endermite_spawn_egg"),
		Map.entry(68, "minecraft:guardian_spawn_egg"),
		Map.entry(90, "minecraft:pig_spawn_egg"),
		Map.entry(91, "minecraft:sheep_spawn_egg"),
		Map.entry(92, "minecraft:cow_spawn_egg"),
		Map.entry(93, "minecraft:chicken_spawn_egg"),
		Map.entry(94, "minecraft:squid_spawn_egg"),
		Map.entry(95, "minecraft:wolf_spawn_egg"),
		Map.entry(96, "minecraft:mooshroom_spawn_egg"),
		Map.entry(98, "minecraft:ocelot_spawn_egg"),
		Map.entry(100, "minecraft:horse_spawn_egg"),
		Map.entry(101, "minecraft:rabbit_spawn_egg"),
		Map.entry(120, "minecraft:villager_spawn_egg")
	);

	public final static String[] BLOCK_COLORS = {"white_", "orange_", "magenta_", "light_blue_", "yellow_", "lime_", "pink_", "gray_", "light_gray_", "cyan_", "purple_", "blue_", "brown_", "green_", "red_", "black_"};
	public final static String[] COBBLESTONE_WALLS = {"minecraft:cobblestone_wall", "minecraft:mossy_cobblestone_wall"};
	public final static String[] COOKED_FISHES = {"minecraft:cooked_cod", "minecraft:cooked_salmon"};
	public final static String[] DIRTS = {"minecraft:dirt", "minecraft:coarse_dirt", "minecraft:podzol"};
	public final static String[] DOUBLE_PLANTS = {"minecraft:sunflower", "minecraft:lilac", "minecraft:tall_grass", "minecraft:large_fern", "minecraft:rose_bush", "minecraft:peony"};
	public final static String[] DYE_COLORS = {"minecraft:ink_sac", "minecraft:red_dye", "minecraft:green_dye", "minecraft:cocoa_beans", "minecraft:lapis_lazuli", "minecraft:purple_dye", "minecraft:cyan_dye", "minecraft:light_gray_dye", "minecraft:gray_dye", "minecraft:pink_dye", "minecraft:lime_dye", "minecraft:yellow_dye", "minecraft:light_blue_dye", "minecraft:magenta_dye", "minecraft:orange_dye", "minecraft:bone_meal"};
	public final static String[] FISHES = {"minecraft:cod", "minecraft:salmon", "minecraft:tropical_fish", "minecraft:pufferfish"};
	public final static String[] PRISMARINES = {"minecraft:prismarine", "minecraft:prismarine_bricks", "minecraft:dark_prismarine"};
	public final static String[] RED_FLOWERS = {"minecraft:poppy", "minecraft:blue_orchid", "minecraft:allium", "minecraft:azure_bluet", "minecraft:red_tulip", "minecraft:orange_tulip", "minecraft:white_tulip", "minecraft:pink_tulip", "minecraft:oxeye_daisy"};
	public final static String[] SKULLS = {"minecraft:skeleton_skull", "minecraft:wither_skeleton_skull", "minecraft:zombie_head", "minecraft:player_head", "minecraft:creeper_head"};
	public final static String[] SPONGES = {"minecraft:sponge", "minecraft:wet_sponge"};
	public final static String[] STONES = {"minecraft:stone", "minecraft:granite", "minecraft:polished_granite", "minecraft:diorite", "minecraft:polished_diorite", "minecraft:andesite", "minecraft:polished_andesite"};
	public final static String[] STONE_BRICKS = {"minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:chiseled_stone_bricks"};
	public final static String[] STONE_SLABS = {"minecraft:smooth_stone_slab", "minecraft:sandstone_slab", "minecraft:smooth_stone_slab", "minecraft:cobblestone_slab", "minecraft:brick_slab", "minecraft:stone_brick_slab", "minecraft:nether_brick_slab", "minecraft:quartz_slab"};
	public final static String[] TREES = {"oak_", "spruce_", "birch_", "jungle_", "acacia_", "dark_oak_"};

	public static String migrateItemId(String id, int damage) {
		switch (id) {
			case "minecraft:banner": return "minecraft:" + BLOCK_COLORS[15 - damage] + "banner";
			case "minecraft:cobblestone_wall": return COBBLESTONE_WALLS[damage];
			case "minecraft:cooked_fish": return COOKED_FISHES[damage];
			case "minecraft:dirt": return DIRTS[damage];
			case "minecraft:double_plant": return DOUBLE_PLANTS[damage];
			case "minecraft:dye": return DYE_COLORS[damage];
			case "minecraft:fish": return FISHES[damage];
			case "minecraft:leaves2": return "minecraft:" + TREES[damage + 4] + "leaves";
			case "minecraft:log2": return "minecraft:" + TREES[damage + 4] + "log";
			case "minecraft:prismarine": return PRISMARINES[damage];
			case "minecraft:red_flower": return RED_FLOWERS[damage];
			case "minecraft:skull": return SKULLS[damage];
			case "minecraft:spawn_egg": return SPAWN_EGGS.getOrDefault(damage, "minecraft:skeleton_spawn_egg");
			case "minecraft:sponge": return SPONGES[damage];
			case "minecraft:stonebrick": return STONE_BRICKS[damage];
			case "minecraft:stone": return STONES[damage];
			case "minecraft:stone_slab": return STONE_SLABS[damage];
		}

		id = MIGRATIONS.getOrDefault(id, id);
		if (isIdInRegistry(id)) return id;

		String shortId = id.replaceFirst(".+?:", "");
		if (damage < BLOCK_COLORS.length && isIdInRegistry("minecraft:" + BLOCK_COLORS[damage] + shortId))
			return "minecraft:" + BLOCK_COLORS[damage] + shortId;
		if (damage < TREES.length) {
			String treeId = "minecraft:" + TREES[damage] + shortId;
			if (isIdInRegistry(treeId)) return treeId;
			if (id.contains("wooden_")) return id.replaceFirst("wooden_", TREES[damage]);
		}
		if (id.startsWith("minecraft:record")) return id.replaceFirst("minecraft:record", "minecraft:music_disc");
		return id;
	}

	public static boolean isIdInRegistry(String id) {
		return !Registries.ITEM.get(new Identifier(id)).equals(Items.AIR);
	}
}
