package net.fabricmc.notnotmelonclient.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.fabricmc.notnotmelonclient.config.Config.CONFIG;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
	@Inject(method = "init", at = @At("TAIL"))
	private void init(CallbackInfo ci) {
		if (!CONFIG.hideRecipeBook) return;
		InventoryScreen inventoryScreen = (InventoryScreen) (Object) this;
		inventoryScreen.remove(inventoryScreen.recipeBook);
		inventoryScreen.recipeBook.setOpen(false);
		inventoryScreen.x = (inventoryScreen.width - inventoryScreen.backgroundWidth) / 2;
		for (Element child : inventoryScreen.children())
			if (child instanceof TexturedButtonWidget texturedButtonWidget && texturedButtonWidget.texture == InventoryScreen.RECIPE_BUTTON_TEXTURE) {
				inventoryScreen.remove(child);
				return;
			}
	}
}
