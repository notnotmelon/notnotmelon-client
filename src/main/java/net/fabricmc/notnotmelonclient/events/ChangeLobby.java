package net.fabricmc.notnotmelonclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public interface ChangeLobby {
    void onChangeLobby();

    Event<ChangeLobby> EVENT = EventFactory.createArrayBacked(ChangeLobby.class,
        (listeners) -> () -> {
            for (ChangeLobby listener : listeners)
                listener.onChangeLobby();
        });

    static void onServerJoin(ClientPlayNetworkHandler clientPlayNetworkHandler, PacketSender packetSender, MinecraftClient minecraftClient) {
        Scheduler.schedule(() -> {
            Util.locationTracker();
            if (Util.isSkyblock) EVENT.invoker().onChangeLobby();
        },1);
    }
}
