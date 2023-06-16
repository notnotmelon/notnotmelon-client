package net.fabricmc.notnotmelonclient.dungeons;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.dungeons.solvers.ThreeWeirdos;
import net.fabricmc.notnotmelonclient.dungeons.solvers.CreeperBeam;
import net.fabricmc.notnotmelonclient.dungeons.solvers.TicTacToe;
import net.fabricmc.notnotmelonclient.events.ChangeLobby;
import net.fabricmc.notnotmelonclient.events.ChatTrigger;
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
		if (client.player == null || !Util.isDungeons()) return;
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
		ChangeLobby.EVENT.register(Dungeons::reset);
		ClientTickEvents.END_CLIENT_TICK.register(Dungeons::tick);
		WorldRenderEvents.END.register(TicTacToe::render);
		ChangeRoomEvent.EVENT.register(TicTacToe::onChangeRoom);
		EntitySpawned.EVENT.register(TicTacToe::onEntitySpawned);
		WorldRenderEvents.END.register(CreeperBeam::render);
		ChangeRoomEvent.EVENT.register(CreeperBeam::onChangeRoom);
		EntitySpawned.EVENT.register(CreeperBeam::onEntitySpawned);
		ChatTrigger.EVENT.register(ThreeWeirdos::onMessage);
		WorldRenderEvents.END.register(ThreeWeirdos::render);
	}

	public static void reset() {
		CreeperBeam.lines = null;
		TicTacToe.bestMoveIndicator = null;
		ThreeWeirdos.correctChest = null;
	}
}
