package net.fabricmc.notnotmelonclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.notnotmelonclient.util.Scheduler;
import net.minecraft.entity.LivingEntity;

public interface EntitySpawned {
	void onEntitySpawned(LivingEntity entity);

	Event<EntitySpawned> EVENT = EventFactory.createArrayBacked(EntitySpawned.class,
		(listeners) -> (LivingEntity entity) -> Scheduler.schedule(() -> {
			for (EntitySpawned listener : listeners)
				listener.onEntitySpawned(entity);
		}, 1));
}