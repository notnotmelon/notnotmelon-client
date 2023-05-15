package net.fabricmc.notnotmelonclient.util;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

/**
 * Used as a superclass for chat-triggered events. For example reparty and API key.
 */
public interface ChatTrigger {
	ActionResult onMessage(Text message, String asString);

	Event<ChatTrigger> EVENT = EventFactory.createArrayBacked(ChatTrigger.class,
		(listeners) -> (message, asString) -> {
			for (ChatTrigger listener : listeners) {
				ActionResult result = listener.onMessage(message, asString);
				if (result == ActionResult.FAIL) return result;
			}
			return ActionResult.PASS;
		});
}
