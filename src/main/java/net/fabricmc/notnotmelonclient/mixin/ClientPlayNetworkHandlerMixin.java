package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.fishing.Fishing;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onPlaySound", at = @At("RETURN"))
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        Fishing.onSound(packet);
    }
}