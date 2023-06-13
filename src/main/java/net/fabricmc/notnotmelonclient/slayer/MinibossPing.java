package net.fabricmc.notnotmelonclient.slayer;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.notnotmelonclient.Main.client;

public class MinibossPing {
	private record Miniboss(String name, String health, Formatting ... formattings) {}
	public static final Miniboss[] minibosses = new Miniboss[]{
		new Miniboss("Revenant Sycophant", "24k", Formatting.YELLOW),
		new Miniboss("Revenant Champion", "90k", Formatting.RED),
		new Miniboss("Deformed Revenant", "360k", Formatting.DARK_RED),
		new Miniboss("Atoned Champion", "600k", Formatting.AQUA),
		new Miniboss("Atoned Revenant", "2.4M", Formatting.DARK_AQUA),

		new Miniboss("Tarantula Vermin", "54k", Formatting.YELLOW),
		new Miniboss("Tarantula Beast", "144k", Formatting.GREEN),
		new Miniboss("Mutant Tarantula", "576k", Formatting.DARK_GREEN),

		new Miniboss("Pack Enforcer", "45k", Formatting.RED),
		new Miniboss("Sven Follower", "120k", Formatting.YELLOW),
		new Miniboss("Sven Alpha", "480k", Formatting.GOLD),

		new Miniboss("Voidling Devotee", "12M", Formatting.AQUA),
		new Miniboss("Voidling Radical", "25M", Formatting.DARK_PURPLE),
		new Miniboss("Voidcrazed Maniac", "75M", Formatting.LIGHT_PURPLE),

		new Miniboss("Flare Demon", "12M", Formatting.YELLOW),
		new Miniboss("Kindleheart Demon", "25M", Formatting.RED),
		new Miniboss("Burningsoul Demon", "75M", Formatting.DARK_RED)
	};

	public static void onEntitySpawned(LivingEntity livingEntity) {
		if (!livingEntity.hasCustomName()) return;
		String entityName = livingEntity.getCustomName().getString().replaceAll("§.", "");
		for (Miniboss miniboss : minibosses) {
			if (entityName.contains(miniboss.name)) {
				client.inGameHud.setTitleTicks(0, 20, 5);
				client.inGameHud.setTitle(Text.literal(miniboss.name + '!').formatted(miniboss.formattings).append(Text.literal(" (" + miniboss.health + ")").formatted(Formatting.BOLD)));
				return;
			}
		}
	}
}
