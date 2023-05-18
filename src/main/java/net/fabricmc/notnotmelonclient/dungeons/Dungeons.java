package net.fabricmc.notnotmelonclient.dungeons;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class Dungeons {
	@FunctionalInterface public interface ChangeRoomEvent {
		void onChangeRoom();
	
		Event<ChangeRoomEvent> EVENT = EventFactory.createArrayBacked(ChangeRoomEvent.class,
			(listeners) -> () -> {
				for (ChangeRoomEvent listener : listeners) listener.onChangeRoom();
			});
	}

	private static int lastX;
	private static int lastZ;
	public static void tick(MinecraftClient client) {
		if (!Util.isDungeons) return;
		PlayerEntity player = client.player;
		int roomX = (int) ((player.getX() + 8.5) / 32);
		int roomZ = (int) ((player.getZ() + 8.5) / 32);

		if (roomX != lastX || roomZ != lastZ) {
			lastX = roomX;
			lastZ = roomZ;
			ChangeRoomEvent.EVENT.invoker().onChangeRoom();
		}
	}

	public static void registerEvents() {
		ChangeRoomEvent.EVENT.register(TicTacToeSolver::onChangeRoom);
	}
}
