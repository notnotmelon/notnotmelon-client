package net.fabricmc.notnotmelonclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;

public interface SoundEvent {
    void onSound(PlaySoundS2CPacket packet, String soundName);

    Event<SoundEvent> EVENT = EventFactory.createArrayBacked(SoundEvent.class,
        (listeners) -> (packet, soundName) -> {
            for (SoundEvent listener : listeners)
                listener.onSound(packet, soundName);
        });
}
