package net.fabricmc.notnotmelonclient.commands;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Unique;

public class ProtectItem {
    @Unique private static final Identifier STAR_TEXTURE = new Identifier("notnotmelonclient", "textures/gui/star.png");

    public static void renderStar(MatrixStack matrices, int x, int y, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, STAR_TEXTURE);

        // Draw bookmark shadow
        RenderSystem.setShaderColor(0f, 0f, 0f, alpha * 0.25f);
        matrices.push();
        matrices.translate(0.5f, 0.5f, 0f);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 8, 8, 8, 8);
        matrices.pop();

        // Draw bookmark
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
        DrawableHelper.drawTexture(matrices, x, y, 0, 0, 8, 8, 8, 8);

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

	/*public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("protectitem").executes(ctx -> execute(ctx.getSource())));
    }

	private static int execute(FabricClientCommandSource source) {
        for (String s : Util.getSidebar())
            Util.print(s);
		return Command.SINGLE_SUCCESS;
    }*/
}
