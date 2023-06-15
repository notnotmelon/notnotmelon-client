package net.fabricmc.notnotmelonclient.slayer;

import net.fabricmc.notnotmelonclient.config.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;

import static net.fabricmc.notnotmelonclient.Main.client;

public class MinibossPing {
	public static final String[] minibosses = new String[]{
		"Revenant Sycophant",
		"Revenant Champion",
		"Deformed Revenant",
		"Atoned Champion",
		"Atoned Revenant",

		"Tarantula Vermin",
		"Tarantula Beast",
		"Mutant Tarantula",

		"Pack Enforcer",
		"Sven Follower",
		"Sven Alpha",

		"Voidling Devotee",
		"Voidling Radical",
		"Voidcrazed Maniac",

		"Flare Demon",
		"Kindleheart Demon",
		"Burningsoul Demon"
	};

	public static void onEntitySpawned(Entity entity) {
		if (!(entity instanceof ArmorStandEntity) || !Config.getConfig().minibossPing || !entity.hasCustomName()) return;
		float distance = entity.distanceTo(client.player);
		if (distance > 30) return;
		String entityName = entity.getCustomName().getString();
		for (String miniboss : minibosses) {
			if (entityName.contains(miniboss)) {
				client.inGameHud.setTitleTicks(0, 80, 5);
				client.inGameHud.setTitle(Text.of(""));
				client.inGameHud.setSubtitle(entity.getCustomName());
				return;
			}
		}
	}
}
