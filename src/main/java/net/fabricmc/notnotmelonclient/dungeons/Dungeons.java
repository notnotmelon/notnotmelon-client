package net.fabricmc.notnotmelonclient.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.events.EntitySpawned;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

public class Dungeons {
	private static final MinecraftClient client = Main.client;

	@FunctionalInterface public interface ChangeRoomEvent {
		void onChangeRoom();
	
		Event<ChangeRoomEvent> EVENT = EventFactory.createArrayBacked(ChangeRoomEvent.class,
			(listeners) -> () -> {
				for (ChangeRoomEvent listener : listeners) listener.onChangeRoom();
			});
	}
	
	private static int lastX;
	private static int lastZ;
	public static void tick(MinecraftClient c) {
		if (client.player == null) return;
		if (!Util.isDungeons()) return;
		int[] roomCoords = getRoomCoords();
		int roomX = roomCoords[0];
		int roomZ = roomCoords[1];

		if (roomX != lastX || roomZ != lastZ) {
			lastX = roomX;
			lastZ = roomZ;
			ChangeRoomEvent.EVENT.invoker().onChangeRoom();
		}
	}

	public static int[] getRoomCoords() {
		PlayerEntity player = client.player;
		int roomX = (int) Math.ceil((player.getX() + 8.5) / 32);
		int roomZ = (int) Math.ceil((player.getZ() + 8.5) / 32);
		return new int[]{roomX, roomZ};
	}

	public static double[] getRoomCenter() {
		int[] roomCoords = getRoomCoords();
		return new double[]{roomCoords[0] * 32 - 24.5, roomCoords[1] * 32 - 24.5};
	}

	public static Box getRoomBounds() {
		double[] roomCenter = getRoomCenter();
		double roomX = roomCenter[0];
		double roomZ = roomCenter[1];

		return new Box(roomX - 16, -64, roomZ - 16, roomX + 16, 255, roomZ + 16);
	}

	public static void registerEvents() {
		ClientTickEvents.END_CLIENT_TICK.register(Dungeons::tick);
		WorldRenderEvents.END.register(TicTacToeSolver::render);
		ChangeRoomEvent.EVENT.register(TicTacToeSolver::onChangeRoom);
		EntitySpawned.EVENT.register(TicTacToeSolver::onEntitySpawned);
		WorldRenderEvents.END.register(CreeperBeamSolver::render);
		ChangeRoomEvent.EVENT.register(CreeperBeamSolver::onChangeRoom);
	}
}
