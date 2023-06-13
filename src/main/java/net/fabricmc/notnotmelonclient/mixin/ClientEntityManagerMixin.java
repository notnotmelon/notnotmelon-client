package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.events.EntitySpawned;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEntityManager.class)
public class ClientEntityManagerMixin<T extends EntityLike> {
	@Inject(method = "addEntity", at = @At("TAIL"))
	private void addEntity(T entity, CallbackInfo ci) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.age == 0) {
			EntitySpawned.EVENT.invoker().onEntitySpawned(livingEntity);
		}
	}
}
