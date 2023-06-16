package net.fabricmc.notnotmelonclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.minecraft.entity.Entity;

public interface EntitySpawned {
	void onEntitySpawned(Entity entity);

	Event<EntitySpawned> EVENT = EventFactory.createArrayBacked(EntitySpawned.class,
		(listeners) -> (Entity entity) -> Scheduler.schedule(() -> {
			for (EntitySpawned listener : listeners)
				listener.onEntitySpawned(entity);
		}, 1));
}
