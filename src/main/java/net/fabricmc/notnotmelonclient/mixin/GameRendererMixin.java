package net.fabricmc.notnotmelonclient.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.notnotmelonclient.Main;
import net.fabricmc.notnotmelonclient.util.RenderUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.util.hit.BlockHitResult;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Inject(method = "shouldRenderBlockOutline", at = @At("RETURN"), cancellable = true)
	private void shouldRenderBlockOutline(CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue()) return;
		HitResult hitResult = Main.client.crosshairTarget;
		if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
			if (Main.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
				BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
				if (RenderUtil.boxedPoints.contains(blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
					cir.setReturnValue(false);
				}
			}
		}
	}
}
