package net.fabricmc.notnotmelonclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.itemlist.ItemList;
import net.fabricmc.notnotmelonclient.itemlist.SearchBar;
import net.fabricmc.notnotmelonclient.misc.CursorResetFix;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {
    protected HandledScreenMixin(Text title) { super(title); }

    @Shadow @Nullable protected Slot focusedSlot;

	// misleading method name. this also triggers on key press
    @Inject(at = @At("HEAD"), method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", cancellable = true)
    private void onMouseClick(Slot slot, int invSlot, int button, SlotActionType actionType, CallbackInfo ci) {
		if (!Util.isSkyblock) return;
        FavoriteItem.onSlotClick(slot, invSlot, (HandledScreen<?>) (Screen) this, actionType, ci);
    }

    // prevents empty tooltips from rendering
	@Inject(at = @At("HEAD"), method = "drawMouseoverTooltip", cancellable = true)
    private void drawMouseoverTooltip(CallbackInfo ci) {
        ScrollableTooltips.changeHoveredSlot(this.focusedSlot);
		if (!Util.isSkyblock || !Config.getConfig().hideEmptyTooltips) return;

		if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack stack = this.focusedSlot.getStack();
            if (stack.getName().getString().equals(" "))
                ci.cancel();
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
	private void close(CallbackInfo info) {
		ScrollableTooltips.reset();
	}

    @Inject(method = "init", at = @At("TAIL"))
    private void init(CallbackInfo info) {
		HandledScreen<?> screen = (HandledScreen<?>) (Screen) this;
		CursorResetFix.onOpenScreen(screen);
		screen.addDrawableChild(new ItemList());
    }

	@Redirect(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;close()V"))
	public void keyPressed(HandledScreen<?> instance) {
		Element focused = instance.getFocused();
		if (focused instanceof SearchBar searchBar && searchBar.isActive()) {
			searchBar.keyPressed(client.options.inventoryKey.boundKey.getCode(), 0, 0);
		} else {
			instance.close();
		}
	}

	@Inject(method = "drawSlot", at = @At("HEAD"))
	public void drawSlot(MatrixStack matrices, Slot slot, CallbackInfo ci) {
		if (SearchBar.yellowMode && slot.hasStack() && SearchBar.matches(slot.getStack())) {
			RenderSystem.enableDepthTest();
			DrawableHelper.fill(matrices, slot.x, slot.y, slot.x + 16, slot.y + 16, 0x88FFC105);
		}
	}
}