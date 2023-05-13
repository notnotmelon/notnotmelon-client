package net.fabricmc.notnotmelonclient.mixin;

import net.fabricmc.notnotmelonclient.config.Config;
import net.fabricmc.notnotmelonclient.misc.FavoriteItem;
import net.fabricmc.notnotmelonclient.misc.ScrollableTooltips;
import net.fabricmc.notnotmelonclient.util.Util;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {
    protected HandledScreenMixin(Text title) { super(title); }

    @Shadow @Nullable protected Slot focusedSlot;
    @Shadow protected Text playerInventoryTitle;

    // misleading method name. this also triggers on keypress
    @Inject(at = @At("HEAD"), method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", cancellable = true)
    public void onMouseClick(Slot slot, int invSlot, int button, SlotActionType actionType, CallbackInfo ci) {
		if (!Util.isSkyblock) return;
        FavoriteItem.onSlotClick(slot, invSlot, (HandledScreen<?>) (Screen) this, actionType, ci);
    }

    // prevents empty tooltips from rendering
	@Inject(at = @At("HEAD"), method = "drawMouseoverTooltip", cancellable = true)
    public void drawMouseoverTooltip(CallbackInfo ci) {
        ScrollableTooltips.changeHoveredSlot(this.focusedSlot);
		if (!Util.isSkyblock || !Config.getConfig().hideEmptyTooltips) return;

		if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            ItemStack stack = this.focusedSlot.getStack();
            if (stack.getName().getString().equals(" "))
                ci.cancel();
        }
    }
}