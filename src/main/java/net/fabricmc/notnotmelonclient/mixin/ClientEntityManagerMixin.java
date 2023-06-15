package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.events.EntitySpawned;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.EntityLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEntityManager.class)
public class ClientEntityManagerMixin<T extends EntityLike> {
	@Inject(method = "addEntity", at = @At("TAIL"))
	private void addEntity(T entityLike, CallbackInfo ci) {
		if (entityLike instanceof Entity entity) {
			EntitySpawned.EVENT.invoker().onEntitySpawned(entity);
		}
	}
}
