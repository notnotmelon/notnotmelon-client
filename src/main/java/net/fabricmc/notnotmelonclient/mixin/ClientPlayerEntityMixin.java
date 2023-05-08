package net.fabricmc.notnotmelonclient.mixin;

import com.mojang.authlib.GameProfile;

import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    // public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, PlayerPublicKey publicKey) {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        // super(world, profile, publicKey);
        super(world, profile);
    }

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    public void dropSelectedItem(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = this.getEquippedStack(EquipmentSlot.MAINHAND);
		if (FavoriteItem.isProtected(stack)) {
            FavoriteItem.printProtectMessage(stack, "dropping");
            cir.setReturnValue(false);
        }
    }
}